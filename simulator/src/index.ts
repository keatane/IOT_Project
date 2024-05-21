import { program } from "commander";
import {assert,range,randomNumber,sleep,entry,MQTTAPI} from "./utils.js";
import { REGISTER_API,LOGIN_API,PAIR_API,FILTER_API } from "./api.js";



async function simulator(n:number|string) {
    n=Number(n.toString())
    for(let i of range(n)){
        singleInstance(`simulation${i}`,`simulation${i}`,i);
    }
}

async function singleInstance(username:string,password:string,id:string|number){
    id=Number(id.toString())
    await register(username,password);
    const obj=await login(username,password);
    const token=obj.token;
    assert(token!==null);
    await pair(id,token);
    while(true){
        await sendData(id,randomNumber(0,1));
        console.log("Sent, looping");
        await sleep(1);
    }
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

program.command("register <username> <password>").action(entry(register));
program.command("login <username> <password>").action(entry(login));
program.command("pair <id> <token>").action(entry(pair));
program.command("send-data <id> <data>").action(entry(sendData));
program.command("simulator-single <username> <password> <id>").action(entry(singleInstance));
program.command("simulator <n>").action(entry(simulator));
program.command("filter <n> <token> <capacity>").action(entry(filter));
program.parse();
