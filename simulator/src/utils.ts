import { connectAsync } from "mqtt";
import fetch from "node-fetch";

export async function sleep(s:number) {
  await new Promise((resolve) => {
    setTimeout(resolve, s*1000);
  });
}

export function assert(condition:boolean):asserts condition{
    if(!condition)throw new Error();
}

export function randomNumber(min:number,max:number){
    return Math.random()*(max-min)+min;
}

export function entry(f:(..._:string[])=>Promise<any>){
    async function inner(...args:string[]){
        console.log(await f(...args));
    }
    return inner
}

export enum Method {
  GET = "get",
  POST = "post",
}

export class RestAPI<RequestType, ResponseType> {
  constructor(
    private path: string,
    private method: Method,
  ) {}
  public async send(obj: RequestType): Promise<ResponseType> {
    const host=process.env['HOST']||"127.0.0.1"
    const port=process.env['PORT']||"1881"
    const response = await fetch(`http://${host}:${port}/${this.path}`, {
      method: this.method,
      body: JSON.stringify(obj),
    });
    return (await response.json()) as ResponseType;
  }
}

export class MQTTAPI<RequestType,ResponseType>{
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

export function*range(start:number,stop?:number){
    if(stop===undefined){
        stop=start;
        start=0;
    }
    for(let i=start;i<stop;i++)yield i;
}

