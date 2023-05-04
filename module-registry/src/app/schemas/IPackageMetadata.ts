import { IPackageID } from "./IPackageID";
import { IPackageName } from "./IPackageName";

export interface IPackageMetadata {
    Name: IPackageName | null;
    Version: string | null;
    ID: IPackageID | null;
}

export function MetadataToTr(metadata: IPackageMetadata): HTMLTableRowElement {
    let row: HTMLTableRowElement = document.createElement('tr');

    let th: HTMLTableCellElement = document.createElement('th');
    if (metadata.Name) {
        th.innerText = metadata.Name;
    }
    row.appendChild(th);

    th = document.createElement('th');
    if (metadata.Version) {
        th.innerText = metadata.Version;
    }
    row.appendChild(th);

    th = document.createElement('th');
    if (metadata.ID) {
        th.innerText = metadata.ID;
    }
    row.appendChild(th);

    return row;
}