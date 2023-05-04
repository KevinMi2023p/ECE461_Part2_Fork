import { IEnumerateOffset } from "./IEnumerateOffset";
import { IPackageMetadata } from "./IPackageMetadata";

export interface IPackagesPostResult {
    metadatas: IPackageMetadata[];
    offset: IEnumerateOffset | null;
}