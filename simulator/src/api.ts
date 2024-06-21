import {RestAPI,Method,MQTTAPI} from "./utils.js";


interface RegisterRequest{
    username:string,
    password:string
}

interface Empty{}

export const REGISTER_API=new RestAPI<RegisterRequest,Empty>("register",Method.POST);

interface LoginRequest{
    username:string
    password:string
    firebaseToken:String|null
}

interface LoginResponse{
    token:string
    userId:Number
}

export const LOGIN_API=new RestAPI<LoginRequest,LoginResponse>("login",Method.POST);

interface FilterRequest{
    token:string,
    id:number,
    filterCapacity:number
}

export const FILTER_API=new RestAPI<FilterRequest,Empty>("filter",Method.POST);

interface GetJugsRequest{
    token:string,
}

interface Jug{

}

interface GetJugsResponse{
    jugs:Jug[]
}

export const GET_JUGS_API=new RestAPI<GetJugsRequest,GetJugsResponse>("getJugs",Method.POST);

interface DeleteJugRequest{
    token:string,
    id:number
}

export const DELETE_JUG_API=new RestAPI<DeleteJugRequest,Empty>("deleteJug",Method.POST);

interface RenameJugRequest{
    token:string,
    id:number,
    name:string
}

export const RENAME_JUG_API=new RestAPI<RenameJugRequest,Empty>("renameJug",Method.POST);

interface DeleteAccountRequest{
    token:string,
}

export const DELETE_ACCOUNT_API=new RestAPI<DeleteAccountRequest,Empty>("deleteAccount",Method.POST);

interface ChangeEmailRequest{
    token:string,
    newEmail:string,
}

export const CHANGE_EMAIL_API=new RestAPI<ChangeEmailRequest,Empty>("email",Method.POST);

interface ChangePasswordRequest{
    token:string,
    oldPw:string,
    newPw:string
}

export const CHANGE_PASSWORD_API=new RestAPI<ChangePasswordRequest,Empty>("pw",Method.POST);

interface JugDataRequest{
    token:string,
    id:number
}

export const TOTAL_LITRES=new RestAPI<JugDataRequest,Number>("getTotalLitres",Method.POST);
export const TOTAL_LITRES_FILTER=new RestAPI<JugDataRequest,Number>("getTotalLitresFilter",Method.POST);
export const DAILY_LITRES=new RestAPI<JugDataRequest,Number>("getDailyLitres",Method.POST);
export const HOUR_LITRES=new RestAPI<JugDataRequest,Number[]>("getHourLitres",Method.POST);
export const WEEK_LITRES=new RestAPI<JugDataRequest,Number[]>("getWeekLitres",Method.POST);

interface SetLocationRequest{
    token:string,
    id:number,
    lat:number,
    lon:number
}

export const SET_LOCATION_API=new RestAPI<SetLocationRequest,Empty>("setLocation",Method.POST);


interface PairRequest{
    id:number,
    token:string,
}


export const PAIR_API=new MQTTAPI<PairRequest,Empty>("/jug/pair","/jug/pair/response");


interface EdgePairRequest{
    ssid:string,
    pw:string,
    token:string
}

export const EDGE_PAIR_API=new RestAPI<EdgePairRequest,Empty>("pair",Method.POST,"192.168.4.1","8080");

