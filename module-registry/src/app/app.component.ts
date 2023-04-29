import { Component, OnInit } from '@angular/core';
import { IAuthenticationRequest } from './schemas/IAuthenticationRequest';
import { HttpClient, HttpErrorResponse, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { catchError, take } from 'rxjs';
import { IAuthenticationToken } from './schemas/IAuthenticationToken';
import { IPackageMetadata } from './schemas/IPackageMetadata';
import { IPackageQuery } from './schemas/IPackageQuery';
import { IEnumerateOffset } from './schemas/IEnumerateOffset';
import { IPackagesResult } from './interfaces/IPackagesResult';

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

    constructor(private http: HttpClient) {
        this.getAuth();
        console.log(this.isAdmin, this.authToken);
    }

    ngOnInit(): void {
        this.setupLogin();
        this.setupLogout();
        this.setupRegistryReset();
    }

    private getAuth(): void {
        let token: string | null = sessionStorage.getItem('auth');
        let isAdmin: string | null = sessionStorage.getItem('isAdmin');
        if (token && isAdmin) {
            this.authToken = token;
            this.isAdmin = (isAdmin == 'true');
        } else {
            this.authToken = "";
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
            sessionStorage.setItem('auth', token);
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

            let authRequest: IAuthenticationRequest = {
                User: {
                    name: usernameInput.value.trim(),
                    isAdmin: adminCheck.checked
                },
                Secret: {
                    password: passwordInput.value.trim()
                }
            };

            let res: HttpAnyResponse<IAuthenticationToken> = await new Promise<HttpAnyResponse<IAuthenticationToken>>(
                (resolve) => {
                    this.http.put<IAuthenticationToken>('/authenticate', authRequest, { observe: 'response',
                        withCredentials: false })
                    .pipe(take(1), catchError((error: HttpErrorResponse) => {
                        resolve(error);
                        throw Error();
                    })).forEach(v => resolve(v));
                }
            );

            let response: HttpResponse<IAuthenticationToken> | null = res as HttpResponse<IAuthenticationToken>;
            if (response && response.status == HttpStatusCode.Ok && response.body) {
                this.setAuth(response.body, authRequest.User.isAdmin);
            } else {
                console.log(res);
            }

            loginButton.disabled = false;
            usernameInput.disabled = false;
            passwordInput.disabled = false;
            adminCheck.disabled = false;
        };

        loginButton.disabled = false;
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

            let res: HttpAnyResponse<Object> = await new Promise<HttpAnyResponse<Object>>(
                (resolve) => {
                    this.http.delete('/reset', {
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
            if (!response || response.status != HttpStatusCode.Ok) {
                console.log(res);
            }

            resetRegistryButton.disabled = false;
        };

        resetRegistryButton.disabled = false;
    }

    private async packagesRequest(packageQueries: IPackageQuery[], offset: IEnumerateOffset | null): Promise<IPackagesResult | null> {
        let params: { [param: string]: string | number | boolean | ReadonlyArray<string | number | boolean>; } = {};
        if (offset) {
            params["offset"] = offset;
        }

        let res: HttpAnyResponse<IPackageMetadata[]> = await new Promise<HttpAnyResponse<IPackageMetadata[]>>(
            (resolve) => {
                this.http.post<IPackageMetadata[]>('/packages', packageQueries,
                {
                    observe: 'response',
                    withCredentials: false,
                    params: params,
                    headers: { "X-Authorization": this.authToken }
                }).pipe(take(1), catchError((error: HttpErrorResponse) => {
                    resolve(error);
                    throw Error();
                })).forEach(v => resolve(v));
            }
        );

        let response: HttpResponse<IPackageMetadata[]> | null = res as HttpResponse<IPackageMetadata[]>;

        if (response && response.status == HttpStatusCode.Ok && response.body) {
            return {
                metadatas: response.body,
                offset: response.headers.get('offset')
            };
        } else {
            console.log(res);
        }

        return null;
    }
}
