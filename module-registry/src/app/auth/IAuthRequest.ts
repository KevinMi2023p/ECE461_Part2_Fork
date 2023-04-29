import { IUser } from "./IUser";
import { ISecret } from "./ISecret";

export interface IAuthRequest {
    User: IUser;
    Secret: ISecret;
}