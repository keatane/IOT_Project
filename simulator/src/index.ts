import { program } from "commander";
import {assert,range,randomNumber,sleep,entry,MQTTAPI} from "./utils.js";
import { REGISTER_API,LOGIN_API,PAIR_API,FILTER_API, EDGE_PAIR_API, GET_JUGS_API, CHANGE_PASSWORD_API, DELETE_JUG_API, RENAME_JUG_API, DELETE_ACCOUNT_API, CHANGE_EMAIL_API, TOTAL_LITRES, TOTAL_LITRES_FILTER, DAILY_LITRES, HOUR_LITRES, WEEK_LITRES } from "./api.js";
import { createClient } from "redis";

async function sendLoop(id:string|number){
    let stop=false;
    process.on("SIGINT",()=>stop=true);
    while(!stop){
        await sendData(id,randomNumber(0,1));
        console.log("Sent, looping");
        await sleep(1);
    }
    await sendData(id,0);
}


async function simulator(n:number|string) {
    n=Number(n.toString())
    const background=[];
    for(let i of range(n)){
        const instance=singleInstance(`simulation${i}`,`simulation${i}`,i);
        background.push(instance);
        await sleep(1);
    }
    await Promise.allSettled(background);
}

async function singleInstance(username:string,password:string,id:string|number){
    id=Number(id.toString())
    await register(username,password);
    const obj=await login(username,password);
    const token=obj["token"];
    assert(token!==undefined);
    await pair(id,token);
    await sendLoop(id);
}

async function register(username:string,password:string) {
    return await REGISTER_API.send({username,password});
}

async function filter(id:string|number,token:string,capacity:string|number) {
    id=Number(id.toString())
    const filterCapacity=Number(capacity.toString())
    return await FILTER_API.send({id,token,filterCapacity});
}

async function login(username:string,password:string) {
    return await LOGIN_API.send({username,password});
}

async function pair(id: string|number, token: string) {
    id=Number(id.toString())
    return await PAIR_API.send({id:Number(id),token});
}

async function sendData(id:string|number,data:string|Number){
    return await new MQTTAPI<number,null>(`/Thingworx/Jug${id}/litresPerSecond`,null).send(Number(data));
}

async function arduinoServer(id:string|number){
    id=Number(id.toString());
    return await EDGE_PAIR_API.recv(async (request)=>{
        console.log(request);
        const token=request.token;
        await pair(id,token);
        return {id}
    });
}

async function getJugs(token:string){
    return await GET_JUGS_API.send({token})
}

async function setFilter(token:string,id:number|string,value:number|string){
    id=Number(id)
    value=Number(value)
    return await FILTER_API.send({token,id,filterCapacity: value})
}

async function changePassword(token:string,oldPassword:string,newPassword:string){
    return CHANGE_PASSWORD_API.send({token,oldPw:oldPassword,newPw:newPassword})
}

async function deleteJug(token:string,id:number|string){
    id=Number(id)
    return DELETE_JUG_API.send({token,id})
}

async function renameJug(token:string,id:number|string,name:string){
    id=Number(id)
    return RENAME_JUG_API.send({token,id,name})
}

async function deleteAccount(token:string){
    return DELETE_ACCOUNT_API.send({token})
}

async function changeEmail(token:string,email:string){
    return CHANGE_EMAIL_API.send({token,newEmail:email})
}

async function totalLitres(token:string,id:number|string){
    id=Number(id)
    return TOTAL_LITRES.send({token,id})
}

async function totalLitresFilter(token:string,id:number|string){
    id=Number(id)
    return TOTAL_LITRES_FILTER.send({token,id})
}

async function dailyLitres(token:string,id:number|string){
    id=Number(id)
    return DAILY_LITRES.send({token,id})
}

async function hourLitres(token:string,id:number|string){
    id=Number(id)
    return HOUR_LITRES.send({token,id})
}

async function weekLitres(token:string,id:number|string){
    id=Number(id)
    return WEEK_LITRES.send({token,id})
}

async function arduinoClient(ssid:string,pw:string,token:string){
    return await EDGE_PAIR_API.send({ssid,pw,token});
}

async function arduinoSimulator(id:string|number){
    await arduinoServer(id);
    await sendLoop(id);
}

async function createRedisData(id:string){
    const host=process.env["IP"]||"127.0.0.1";
    console.log(`data:${id}`)
    const client=createClient({url: `redis://${host}:6379`})
    client.connect()
    await client.set(`total:${id}`,Math.random()*100)
    await client.set(`filtered:${id}`,Math.random()*100)
    if(!await client.exists(`data:${id}`))
        await client.ts.create(`data:${id}`,'RETENTION',1000*60*60*24*7)
    const args=Array.from(range(0,10),
            _=>[`data:${id}`,Math.floor(new Date().getTime()-1000*60*60*24-Math.random()*1000000).toString(),Math.floor(Math.random()*100).toString()])
    .concat(Array.from(range(0,10),
             _=>[`data:${id}`,Math.floor(new Date().getTime()-Math.random()*1000*60*60).toString(),Math.floor(Math.random()*100).toString()]))
    .reduce((acc,elem)=>acc.concat(elem))
    await client.sendCommand(["TS.MADD",...args]);

}

program.command("register <username> <password>").action(entry(register));
program.command("login <username> <password>").action(entry(login));
program.command("pair <id> <token>").action(entry(pair));
program.command("send-data <id> <data>").action(entry(sendData));
program.command("send-loop <id>").action(entry(sendLoop));
program.command("simulator-single <username> <password> <id>").action(entry(singleInstance));
program.command("simulator <n>").action(entry(simulator));
program.command("filter <n> <token> <capacity>").action(entry(filter));
program.command("arduino-client <ssid> <pw> <token>").action(entry(arduinoClient));
program.command("arduino-server <id>").action(entry(arduinoServer));
program.command("arduino-simulator <id>").action(entry(arduinoSimulator));
program.command("set-filter <token> <id> <value>").action(entry(setFilter));
program.command("get-jugs <token>").action(entry(getJugs));
program.command("change-password <token> <oldPassword> <newPassword>").action(entry(changePassword));
program.command("delete-jug <token> <id>").action(entry(deleteJug));
program.command("rename-jug <token> <id> <name>").action(entry(renameJug));
program.command("delete-account <token>").action(entry(deleteAccount));
program.command("change-email <token> <email>").action(entry(changeEmail));
program.command("total-litres <token> <id>").action(entry(totalLitres));
program.command("total-litres-filter <token> <id>").action(entry(totalLitresFilter));
program.command("daily-litres <token> <id>").action(entry(dailyLitres));
program.command("hour-litres <token> <id>").action(entry(hourLitres));
program.command("week-litres <token> <id>").action(entry(weekLitres));
program.command("create-redis-data <id>").action(entry(createRedisData));
program.parse();
