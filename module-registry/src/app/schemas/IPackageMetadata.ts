import { IPackageID } from "./IPackageID";
import { IPackageName } from "./IPackageName";

export interface IPackageMetadata {
    Name: IPackageName | null;
    Version: string | null;
    ID: IPackageID | null;
}