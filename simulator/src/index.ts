import { program } from "commander";
import {assert,range,randomNumber,sleep,entry,MQTTAPI} from "./utils.js";
import { REGISTER_API,LOGIN_API,PAIR_API,FILTER_API, EDGE_PAIR_API } from "./api.js";

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
        return await pair(id,token);
    });
}

async function arduinoClient(ssid:string,pw:string,token:string){
    return await EDGE_PAIR_API.send({ssid,pw,token});
}

async function arduinoSimulator(id:string|number){
    await arduinoServer(id);
    await sendLoop(id);
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
program.parse();
