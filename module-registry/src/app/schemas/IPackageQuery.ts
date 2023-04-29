import { IPackageName } from "./IPackageName";
import { ISemverRange } from "./ISemverRange";

export interface IPackageQuery {
    Version: ISemverRange;
    Name: IPackageName;
}