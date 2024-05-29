import { connectAsync } from "mqtt";
import fetch from "node-fetch";
import { createServer } from "node:http";
import { Readable } from "node:stream";
import {MQTT_USERNAME,MQTT_PASSWORD} from "./secrets.js"

export async function sleep(s: number) {
  await new Promise((resolve) => {
    setTimeout(resolve, s * 1000);
  });
}

function streamToString(stream: Readable): Promise<string> {
  const chunks: Buffer[] = [];
  return new Promise((resolve, reject) => {
    stream.on("data", (chunk) => chunks.push(Buffer.from(chunk)));
    stream.on("error", (err) => reject(err));
    stream.on("end", () => resolve(Buffer.concat(chunks).toString("utf8")));
  });
}

export function assert(condition: boolean): asserts condition {
  if (!condition) throw new Error();
}

export function randomNumber(min: number, max: number) {
  return Math.random() * (max - min) + min;
}

export function entry(f: (..._: string[]) => Promise<any>) {
  async function inner(...args: string[]) {
    console.log(await f(...args));
  }
  return inner;
}

export enum Method {
  GET = "get",
  POST = "post",
}

export class RestAPI<RequestType, ResponseType> {
  constructor(
    private path: string,
    private method: Method,
    private host: String = "127.0.0.1",
    private port: String = "1880",
  ) {
        if(process.env["IP"])this.host=process.env["IP"];
        if(process.env["PORT"])this.port=process.env["PORT"];
    }
  public async send(obj: RequestType): Promise<ResponseType> {
    const response = await fetch(
      `http://${this.host}:${this.port}/${this.path}`,
      {
        method: this.method,
        body: JSON.stringify(obj),
      },
    );
    return (await response.json()) as ResponseType;
  }
  public recv(
    callback: (_: RequestType) => Promise<ResponseType>,
  ): Promise<RequestType> {
    return new Promise((resolve) => {
      const httpServer = createServer(async (request, response) => {
        httpServer.close()
        const stringRequest = await streamToString(request);
        const requestObject: RequestType = JSON.parse(stringRequest);
        const result:ResponseType = await callback(requestObject);
        response.end(JSON.stringify(result));
        resolve(requestObject);
      });
      httpServer.listen(this.port);
    });
  }
}

export class MQTTAPI<RequestType, ResponseType> {
  constructor(
    private publishTopic: string,
    private subscribeTopic: string | null,
  ) {}
  public async send(obj: RequestType): Promise<ResponseType> {
    const client = await connectAsync("mqtt://212.78.1.205:1883", {
      username: MQTT_USERNAME,
      password: MQTT_PASSWORD,
    });
    await client.publishAsync(this.publishTopic, JSON.stringify(obj));
    if (this.subscribeTopic == null) return null as any;
    await client.subscribeAsync(this.subscribeTopic);
    const promise = new Promise<ResponseType>((resolve) => {
      client.on("message", (_, message) => {
        console.log(message.toString());
        resolve(JSON.parse(message.toString()));
      });
    });
    const result = await promise;
    await client.endAsync();
    return result;
  }
}

export function* range(start: number, stop?: number) {
  if (stop === undefined) {
    stop = start;
    start = 0;
  }
  for (let i = start; i < stop; i++) yield i;
}
