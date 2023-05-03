import { IUser } from "./IUser";
import { IUserAuthenticationInfo } from "./IUserAuthenticationInfo";

export interface IAuthenticationRequest {
    User: IUser;
    Secret: IUserAuthenticationInfo;
}