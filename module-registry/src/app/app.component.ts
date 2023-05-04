import { Component, OnInit } from '@angular/core';
import { IAuthenticationRequest } from './schemas/IAuthenticationRequest';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { catchError, take } from 'rxjs';
import { IAuthenticationToken } from './schemas/IAuthenticationToken';
import { IPackageMetadata, MetadataToTr } from './schemas/IPackageMetadata';
import { IPackageQuery } from './schemas/IPackageQuery';
import { IEnumerateOffset } from './schemas/IEnumerateOffset';
import { IPackagesPostResult } from './schemas/IPackagesPostResult';
import { IPackage } from './schemas/IPackage';
import { IPackageData } from './schemas/IPackageData';
import { IPackageRating } from './schemas/IPackageRating';
import { IPackageHistoryEntry } from './schemas/IPackageHistoryEntry';
import { IPackageName } from './schemas/IPackageName';
import { IPackageRegEx } from './schemas/IPackageRegEx';
import { environment } from 'src/environments/environment';

type HttpAnyResponse<T> = HttpResponse<T> | HttpErrorResponse;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'module-registry';

    public authToken: string;
    public isAdmin: boolean;

    public table: HTMLTableElement;
    public tbody: HTMLTableSectionElement;
    public theader: HTMLTableSectionElement;
    public tableNextButton: HTMLButtonElement;
    public tablePreviousButton: HTMLButtonElement;

    public packageSearchButton: HTMLButtonElement;
    public regExSearchButton: HTMLButtonElement;

    private packageQueries: IPackageQuery[] | null      = null;
    public queryOffset: string | null                  = null;
    private queryOffsetPrev: string | null                  = null;
    public packagesResult: IPackagesPostResult | null  = null;
    public regExResult: IPackagesPostResult | null     = null;

    constructor(private http: HttpClient) {
        this.getAuth();
        console.log(this.isAdmin, this.authToken);
    }

    async ngOnInit(): Promise<void> {
        await Promise.all([
            this.setupLogin(),
            this.setupLogout(),
            this.setupRegistryReset(),
            this.setupDisplayTable(),
            this.setupPackageSearchTable()
        ]);
        this.packageSearchButton.disabled = false;
        this.tableNextButton.disabled = false;
        this.tablePreviousButton.disabled = false;
    }

    private getAuth(): void {
        let token: string | null = sessionStorage.getItem('auth');
        let isAdmin: string | null = sessionStorage.getItem('isAdmin');
        if (token && isAdmin) {
            this.authToken = token;
            this.isAdmin = (isAdmin == 'true');
        } else {
            this.authToken = '';
            this.isAdmin = false;
        }
    }

    private unsetAuth(): void {
        sessionStorage.removeItem('auth');
        sessionStorage.removeItem('isAdmin');
        this.getAuth();
    }

    private setAuth(token: string, isAdmin: boolean): void {
        this.unsetAuth();
        if (token.length > 0) {
            sessionStorage.setItem('auth', `bearer ${token}`);
            sessionStorage.setItem('isAdmin', String(isAdmin));
        }
        this.getAuth();
    }

    private async setupLogin(): Promise<void> {
        let usernameInput: HTMLInputElement = document.getElementById("usernameInput") as HTMLInputElement;
        let passwordInput: HTMLInputElement = document.getElementById("passwordInput") as HTMLInputElement;
        let adminCheck: HTMLInputElement = document.getElementById("adminCheck") as HTMLInputElement;

        let loginButton: HTMLButtonElement = document.getElementById("loginButton") as HTMLButtonElement;

        loginButton.onclick = async (event: MouseEvent) => {
            loginButton.disabled = true;
            usernameInput.disabled = true;
            passwordInput.disabled = true;
            adminCheck.disabled = true;

            usernameInput.value = usernameInput.value.trim();
            passwordInput.value = passwordInput.value.trim();

            let token: IAuthenticationToken | null = await this.authenticatePutRequest(usernameInput.value, passwordInput.value, adminCheck.checked);

            if (token) {
                this.setAuth(token, adminCheck.checked);
            }

            loginButton.disabled = false;
            usernameInput.disabled = false;
            passwordInput.disabled = false;
            adminCheck.disabled = false;
        };

        loginButton.disabled = false;

        if (!environment.production) {
            usernameInput.value = "admin1";
            passwordInput.value = "admin1";
            adminCheck.checked = true;
            if (this.authToken.length == 0) {
                loginButton.click();
            }
        }
    }

    private async setupLogout(): Promise<void> {
        let logoutButton: HTMLButtonElement = document.getElementById("logoutButton") as HTMLButtonElement;
        logoutButton.onclick = (event: MouseEvent) => this.unsetAuth();
        logoutButton.disabled = false;
    }

    private async setupRegistryReset(): Promise<void> {
        let resetRegistryButton: HTMLButtonElement = document.getElementById('resetRegistryButton') as HTMLButtonElement;

        resetRegistryButton.onclick = async (event: MouseEvent) => {
            resetRegistryButton.disabled = true;

            this.getAuth();

            await this.resetDeleteRequest();

            resetRegistryButton.disabled = false;
        };

        resetRegistryButton.disabled = false;
    }

    private static createSearchTableRow(name: string, version: string): HTMLTableRowElement {
        let row: HTMLTableRowElement = document.createElement('tr');

        let td: HTMLTableCellElement = document.createElement('td');
        td.style.padding = '0.25rem';
        td.style.borderBottom = 'none';
        let input: HTMLInputElement = document.createElement('input');
        input.setAttribute('type', 'text');
        input.value = name;
        td.appendChild(input);
        row.appendChild(td);

        td = document.createElement('td');
        td.style.padding = '0.25rem';
        td.style.borderBottom = 'none';
        input = document.createElement('input');
        input.setAttribute('type', 'text');
        input.value = version;
        td.appendChild(input);
        row.appendChild(td);

        td = document.createElement('td');
        td.style.padding = '0.25rem';
        td.style.borderBottom = 'none';
        let button: HTMLButtonElement = document.createElement('button');
        button.innerText = 'Delete';
        button.onclick = (event: MouseEvent) => { (row.parentNode as HTMLElement).removeChild(row); };
        td.appendChild(button);
        row.appendChild(td);

        return row;
    }

    private async setupDisplayTable(): Promise<void> {
        this.table = document.getElementById('table') as HTMLTableElement;
        this.theader = document.getElementById('theader') as HTMLTableSectionElement;
        this.tbody = document.getElementById('tbody') as HTMLTableSectionElement;
        this.tableNextButton = document.getElementById('tableNextButton') as HTMLButtonElement;
        this.tablePreviousButton = document.getElementById('tablePreviousButton') as HTMLButtonElement;

        // this.tableNextButton.onclick = (event: MouseEvent) => {
        //     this.
        // };
    }

    private static removeRowsFromTable(selection: HTMLTableSectionElement): void {
        for (let i = selection.rows.length - 1; i > -1; i--) {
            let row: HTMLTableRowElement | null = selection.rows.item(i);
            if (row) {
                selection.removeChild(row);
            }
        }
    }

    private async setDisplayTableHeaders(headers: string[]): Promise<void> {
        AppComponent.removeRowsFromTable(this.theader);

        let row: HTMLTableRowElement = document.createElement('tr');

        headers.forEach((h) => {
            let th: HTMLTableCellElement = document.createElement('th');
            th.innerText = h;
            th.setAttribute('scope', 'col');
            row.appendChild(th);
        });

        this.theader.appendChild(row);
    }

    private displayMetadata(metadata: IPackageMetadata[]): void {
        AppComponent.removeRowsFromTable(this.tbody);
        metadata.forEach((m) => this.tbody.appendChild(MetadataToTr(m)));
    }

    private async displayPackagesResult(): Promise<void> {
        const headers: string[] = ['Name', 'Version', 'ID'];

        if (!this.packagesResult) {
            return;
        }

        let pHeaders: Promise<void> = this.setDisplayTableHeaders(headers);

        this.displayMetadata(this.packagesResult.metadatas);

        await pHeaders;

        this.tablePreviousButton.disabled = this.queryOffset == null;
        this.tableNextButton.disabled = this.packagesResult.offset == null;
    }

    private async setupPackageSearchTable(): Promise<void> {
        // add button
        let packageSearchAddButton: HTMLButtonElement = document.getElementById('packageSearchAddButton') as HTMLButtonElement;
        let packageSearchAddButtonTr: HTMLTableRowElement = packageSearchAddButton.parentNode!.parentNode as HTMLTableRowElement;
        packageSearchAddButton.onclick = (event: MouseEvent) => {
            packageSearchAddButtonTr.before(AppComponent.createSearchTableRow("", ""));
        };

        packageSearchAddButton.disabled = false;
        if (!environment.production) {
            packageSearchAddButtonTr.before(AppComponent.createSearchTableRow("lodash", "Exact (5.0.0)"));
            packageSearchAddButtonTr.before(AppComponent.createSearchTableRow("lodash", "Exact (5.0.6)"));
        } else {
            packageSearchAddButton.click();
        }

        // search button
        this.packageSearchButton = document.getElementById('packageSearchSearchButton') as HTMLButtonElement;
        let packageSearchTbody = document.getElementById('packageSearchTbody') as HTMLTableSectionElement;

        this.packageSearchButton.onclick = async (event: MouseEvent) => {
            this.packageSearchButton.disabled = true;

            packageSearchAddButton.disabled = true;

            let nextState: boolean = this.tableNextButton.disabled;
            let prevState: boolean = this.tablePreviousButton.disabled;

            this.tableNextButton.disabled = true;
            this.tablePreviousButton.disabled = true;

            let packageRows: [HTMLInputElement, HTMLInputElement, HTMLButtonElement][] = [];
            for (let i = 0; i < packageSearchTbody.rows.length; i++) {
                let row: HTMLTableRowElement | null = packageSearchTbody.rows.item(i);

                if (row && row.cells.length == 3) {
                    packageRows.push([
                        row.cells.item(0)!.firstChild as HTMLInputElement,
                        row.cells.item(1)!.firstChild as HTMLInputElement,
                        row.cells.item(2)!.firstChild as HTMLButtonElement
                    ]);
                }
            }

            packageSearchAddButton.disabled = false;

            packageRows = packageRows.filter((r) => {
                r[2].disabled = true;
                r[0].disabled = true;
                r[1].disabled = true;

                r[0].value = r[0].value.trim();
                r[1].value = r[1].value.trim();

                if (r[0].value.length == 0 || r[1].value.length == 0) {
                    r[0].disabled = false;
                    r[1].disabled = false;
                    r[2].disabled = false;
                    return false;
                }

                return true;
            });

            let tempPackageQueries = packageRows.map((v) => { return { Name: v[0].value, Version: v[1].value,  }; });

            packageRows.forEach((r) => {
                r[0].disabled = false;
                r[1].disabled = false;
                r[2].disabled = false;
            });

            this.getAuth();

            let result: IPackagesPostResult | null = await this.packagesPostRequest(tempPackageQueries, "0");

            console.log(result)

            if (result) {
                this.packageQueries = tempPackageQueries;
                this.queryOffset = null;
                this.queryOffsetPrev = null;
                this.packagesResult = result;
                this.regExResult = null;

                // display the packages
                await this.displayPackagesResult();
            } else {
                this.tableNextButton.disabled = nextState;
                this.tablePreviousButton.disabled = prevState;
            }

            this.packageSearchButton.disabled = false;
        };
    }

    // generic *Request methods
    private async deleteRequest(url: string): Promise<boolean> {
        let res: HttpAnyResponse<Object> = await new Promise<HttpAnyResponse<Object>>(
            (resolve) => {
                this.http.delete<Object>(url, {
                    observe: 'response',
                    withCredentials: false,
                    headers: { "X-Authorization": this.authToken }
                }).pipe(take(1), catchError((error: HttpErrorResponse) => {
                    resolve(error);
                    throw Error();
                })).forEach(v => resolve(v));
            }
        );

        let response: HttpResponse<Object> | null = res as HttpResponse<Object>;

        if (response && response.status == HttpStatusCode.Ok) {
            return true;
        } else {
            console.log(res);
        }

        return false;
    }

    private async getRequest<T>(url: string): Promise<T | null> {
        let res: HttpAnyResponse<T> = await new Promise<HttpAnyResponse<T>>(
            (resolve) => {
                this.http.get<T>(url, {
                    observe: 'response',
                    withCredentials: false,
                    headers: { "X-Authorization": this.authToken }
                }).pipe(take(1), catchError((error: HttpErrorResponse) => {
                    resolve(error);
                    throw Error();
                })).forEach(v => resolve(v));
            }
        );

        let response: HttpResponse<T> | null = res as HttpResponse<T>;

        if (response && response.status == HttpStatusCode.Ok && response.body) {
            return response.body;
        } else {
            console.log(res);
        }

        return null;
    }

    private async postRequest<X, Y>(url: string, body: X,
        params: { [param: string]: string | number | boolean | ReadonlyArray<string | number | boolean>; } | undefined,
        headers: HttpHeaders | { [header: string]: string | string[]; } | undefined): Promise<HttpAnyResponse<Y>>
    {
        return await new Promise<HttpAnyResponse<Y>>(
            (resolve) => {
                this.http.post<Y>(url, body, {
                    observe: 'response',
                    withCredentials: false,
                    params: params,
                    headers: headers
                }).pipe(take(1), catchError((error: HttpErrorResponse) => {
                    resolve(error);
                    throw Error();
                })).forEach(v => resolve(v));
            }
        );
    }

    // non-generic *Request methods
    // All string-like values passed to non-generic *Request methods must be pre-processed, aside from URI encoding
    private async packagesPostRequest(packageQueries: IPackageQuery[], offset: IEnumerateOffset | null): Promise<IPackagesPostResult | null> {
        let params: { [param: string]: string | number | boolean | ReadonlyArray<string | number | boolean>; } = {};
        if (offset) {
            params["offset"] = offset;
        }

        console.log(packageQueries);
        console.log(JSON.stringify(packageQueries))

        let res: HttpAnyResponse<IPackageMetadata[]> = await this.postRequest<IPackageQuery[], IPackageMetadata[]>(
            '/packages', packageQueries, params, {
                "X-Authorization": this.authToken,
                "Content-Type": "application/json",
                "accept": "*/*",
                "Response-Type": "text"
            });

        let response: HttpResponse<IPackageMetadata[]> | null = res as HttpResponse<IPackageMetadata[]>;

        if (response && response.status == HttpStatusCode.Ok && response.body) {
            return { metadatas: response.body, offset: response.headers.get('offset') };
        } else {
            console.log(res);
        }

        return null;
    }

    private async resetDeleteRequest(): Promise<boolean> {
        return await this.deleteRequest('/reset');
    }

    private async packageIdGetRequest(id: string): Promise<IPackage | null> {
        if (id.length == 0) {
            return null;
        }

        id = encodeURIComponent(id);

        return await this.getRequest<IPackage>(`/package/${id}`);
    }

    private async packageIdPutRequest(id: string, pkg: IPackage): Promise<boolean> {
        if (id.length == 0) {
            return false;
        }

        id = encodeURIComponent(id);

        let res: HttpAnyResponse<Object> = await new Promise<HttpAnyResponse<Object>>(
            (resolve) => {
                this.http.put<Object>(`/package/${id}`, pkg, {
                    observe: 'response',
                    withCredentials: false,
                    headers: { "X-Authorization": this.authToken }
                }).pipe(take(1), catchError((error: HttpErrorResponse) => {
                    resolve(error);
                    throw Error();
                })).forEach(v => resolve(v));
            }
        );

        let response: HttpResponse<Object> | null = res as HttpResponse<Object>;

        if (response && response.status == HttpStatusCode.Ok) {
            return true;
        } else {
            console.log(res);
        }

        return false;
    }

    private async packageIdDeleteRequest(id: string): Promise<boolean> {
        if (id.length == 0) {
            return false;
        }

        id = encodeURIComponent(id);

        return await this.deleteRequest(`/package/${id}`);
    }

    private async packagePostRequest(pkg: IPackageData): Promise<IPackage | null> {
        let res: HttpAnyResponse<IPackage> = await this.postRequest<IPackageData, IPackage>('/package', pkg, undefined,
            { "X-Authorization": this.authToken });

        let response: HttpResponse<IPackage> | null = res as HttpResponse<IPackage>;

        if (response && response.status == HttpStatusCode.Created && response.body) {
            return response.body;
        } else {
            console.log(res);
        }

        return null;
    }

    private async packageIdRateGetRequest(id: string): Promise<IPackageRating | null> {
        if (id.length == 0) {
            return null;
        }

        id = encodeURIComponent(id);

        return await this.getRequest<IPackageRating>(`/package/${id}/rate`);
    }

    private async authenticatePutRequest(username: string, password: string, isAdmin: boolean): Promise<IAuthenticationToken | null> {
        let authRequest: IAuthenticationRequest = {
            User: { name: username, isAdmin: isAdmin },
            Secret: { password: password }
        };

        let res: HttpAnyResponse<IAuthenticationToken> = await new Promise<HttpAnyResponse<IAuthenticationToken>>(
            (resolve) => {
                this.http.put('/authenticate', authRequest, {
                    observe: 'response',
                    headers: {
                        "Content-Type": "application/json",
                        "accept": "*/*",
                        "Response-Type": "text"
                    },
                    responseType: "text"
                }).pipe(take(1), catchError((error: HttpErrorResponse) => {
                    resolve(error);
                    throw Error();
                })).forEach(v => resolve(v));
            }
        );

        let response: HttpResponse<IAuthenticationToken> | null = res as HttpResponse<IAuthenticationToken>;

        if (response && response.status == HttpStatusCode.Ok && response.body) {
            return response.body;
        } else {
            console.log(res);
        }

        return null;
    }

    private async packageByNameNameGetRequest(name: IPackageName): Promise<IPackageHistoryEntry[] | null> {
        if (name.length == 0) {
            return null;
        }

        name = encodeURIComponent(name);

        return await this.getRequest<IPackageHistoryEntry[]>(`/package/byName/${name}`);
    }

    private async packageByNameNameDeleteRequest(name: IPackageName): Promise<boolean> {
        if (name.length == 0) {
            return false;
        }

        name = encodeURIComponent(name);

        return await this.deleteRequest(`/package/byName/${name}`);
    }

    private async packageByRegExPostRequest(r: IPackageRegEx): Promise<IPackageMetadata[] | null> {
        let res: HttpAnyResponse<IPackageMetadata[]> = await this.postRequest<IPackageRegEx, IPackageMetadata[]>(
            '/package/byRegEx', r, undefined, { "X-Authorization": this.authToken });

        let response: HttpResponse<IPackageMetadata[]> | null = res as HttpResponse<IPackageMetadata[]>;

        if (response && response.status == HttpStatusCode.Ok && response.body) {
            return response.body;
        } else {
            console.log(res);
        }

        return null;
    }
}
