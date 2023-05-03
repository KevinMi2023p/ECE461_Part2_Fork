import { IPackageData } from "./IPackageData";
import { IPackageMetadata } from "./IPackageMetadata";

export interface IPackage {
    metadata: IPackageMetadata | null;
    data: IPackageData | null;
}