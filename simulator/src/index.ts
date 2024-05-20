import { connectAsync } from "mqtt";
import { program } from "commander";
import fetch from "node-fetch";

const HOST = "127.0.0.1";
const PORT = 1881;

enum Method {
  GET = "get",
  POST = "post",
}

class RestAPI<RequestType, ResponseType> {
  constructor(
    private path: string,
    private method: Method,
  ) {}
  public async send(obj: RequestType): Promise<ResponseType> {
    const response = await fetch(`http://${HOST}:${PORT}/${this.path}`, {
      method: this.method,
      body: JSON.stringify(obj),
    });
    return (await response.json()) as ResponseType;
  }
}

interface RegisterRequest{
    username:string,
    password:string
}

enum RegisterResponseStatus{
    OK="OK"
}

interface RegisterResponse{
    status:RegisterResponseStatus
}

const REGISTER_API=new RestAPI<RegisterRequest,RegisterResponse>("register",Method.POST);

interface LoginRequest{
    username:string
    password:string
}

enum LoginResponseStatus{
    OK="OK"
}

interface LoginResponse{
    status:LoginResponseStatus
    token:string|null
}

const LOGIN_API=new RestAPI<LoginRequest,LoginResponse>("login",Method.POST);


class MQTTAPI<RequestType,ResponseType>{
  constructor(
    private publishTopic: string,
    private subscribeTopic:string|null
  ) {
  }
  public async send(obj: RequestType): Promise<ResponseType> {
      const client=await connectAsync("mqtt://212.78.1.205:1883", { username: "studenti",password:"studentiDRUIDLAB_1" });
      await client.publishAsync(this.publishTopic,JSON.stringify(obj))
      if(this.subscribeTopic==null)return null as any;
      await client.subscribeAsync(this.subscribeTopic)
      const promise=new Promise<ResponseType>((resolve)=>{client.on('message',(_,message)=>{console.log(message.toString());resolve(JSON.parse(message.toString()))})});
      const result=await promise;
      await client.endAsync();
      return result
  }
}

interface PairRequest{
    id:number,
    token:string,
}

enum PairResponseStatus{
    OK="OK"
}

interface PairResponse{
    status:PairResponseStatus
}

const PAIR_API=new MQTTAPI<PairRequest,PairResponse>("/jug/pair","/jug/pair/response");


function assert(obj:boolean):asserts obj is true{
    if(!obj)throw new Error("Assertion failure");
}

async function sleep(s) {
  await new Promise((resolve) => {
    setTimeout(resolve, s*1000);
  });
}



async function simulator(n:Number) {
  await connectAsync("mqtt://127.0.0.1:1883", { username: "" });
}

async function singleInstance(username:string,password:string,id:string){
    await register(username,password);
    const obj=await login(username,password);
    const token=obj.token;
    if(token===null)throw new Error();
    await pair(id,token);
    while(true){
        await sendData(id,"28");
        console.log("Sent, looping");
        sleep(1);
    }
}

async function register(username:string,password:string) {
    return (await REGISTER_API.send({username,password})).status;
}

async function login(username:string,password:string) {
    return await LOGIN_API.send({username,password});
}

async function pair(id: string, token: string) {
    return await PAIR_API.send({id:Number(id),token});
}

async function sendData(id:string,data:string){
    return await new MQTTAPI<Number,null>(`/Thingworx/Jug${id}/litresPerSecond`,null).send(Number(data));
}

function print(f:(...string)=>Promise<any>){
    async function inner(...args:string[]){
        console.log(await f(...args));
    }
    return inner
}


program.command("register <username> <password>").action(print(register));
program.command("login <username> <password>").action(print(login));
program.command("pair <id> <token>").action(print(pair));
program.command("send-data <id> <data>").action(print(pair));
program.command("simulator-single <username> <password> <id>").action(print(singleInstance));
program.parse();
