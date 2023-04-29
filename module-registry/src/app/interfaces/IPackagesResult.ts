import { IEnumerateOffset } from "../schemas/IEnumerateOffset";
import { IPackageMetadata } from "../schemas/IPackageMetadata";

export interface IPackagesResult {
    metadatas: IPackageMetadata[];
    offset: IEnumerateOffset | null;
}