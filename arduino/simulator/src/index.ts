import { connectAsync } from "mqtt";
import { program } from "commander";

async function connect(host: string, port: string) {
  await connectAsync('mqtt://127.0.0.1:1883',{username: ""});
}

program.command("connect <host> <ip>").action(connect);
program.parse();
