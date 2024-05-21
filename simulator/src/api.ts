import {RestAPI,Method,MQTTAPI} from "./utils.js";

enum ResponseStatus{
    OK="OK"
}

interface RegisterRequest{
    username:string,
    password:string
}

interface RegisterResponse{
    status:ResponseStatus
}

export const REGISTER_API=new RestAPI<RegisterRequest,RegisterResponse>("register",Method.POST);

interface LoginRequest{
    username:string
    password:string
}

interface LoginResponse{
    status:ResponseStatus
    token:string|null
}

export const LOGIN_API=new RestAPI<LoginRequest,LoginResponse>("login",Method.POST);

interface FilterRequest{
    token:string,
    id:number,
    filterCapacity:number
}

interface FilterResponse{
    status:ResponseStatus
}

export const FILTER_API=new RestAPI<FilterRequest,FilterResponse>("filter",Method.POST);


interface PairRequest{
    id:number,
    token:string,
}

interface PairResponse{
    status:ResponseStatus
}

export const PAIR_API=new MQTTAPI<PairRequest,PairResponse>("/jug/pair","/jug/pair/response");

