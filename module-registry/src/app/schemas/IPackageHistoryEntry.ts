import { IUser } from "./IUser";
import { IPackageMetadata } from "./IPackageMetadata";
import { PackageAction } from "./PackageAction";

export interface IPackageHistoryEntry {
    User: IUser;
    Date: Date;
    PackageMetadata: IPackageMetadata;
    Action: PackageAction;
}