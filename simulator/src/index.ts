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


async function connect(host: string, port: string) {
  await connectAsync("mqtt://127.0.0.1:1883", { username: "" });
}

async function register(username:string,password:string) {
    console.log((await REGISTER_API.send({username,password})).status)
}

async function login(username:string,password:string) {
    LOGIN_API.send({username,password});
}

async function registerDevices(n: string, token: string) {
  const client = await connectAsync("mqtt://212.78.1.205", {
    username: "studenti",
    password: "studentiDRUIDLAB_1",
  });
  await client.publishAsync("/jug/pair", JSON.stringify({ id: "", token: "" }));
}

program.command("connect <host> <ip>").action(connect);
program.command("register <username> <password>").action(register);
program.command("login <username> <password>").action(login);
program.parse();
