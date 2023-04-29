import { Component, OnInit } from '@angular/core';
import { IAuthRequest } from './auth/IAuthRequest';
import { HttpClient, HttpErrorResponse, HttpResponse, HttpStatusCode } from '@angular/common/http';
import { catchError, take } from 'rxjs';

type HttpAnyResponse<T> = HttpResponse<T> | HttpErrorResponse;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'module-registry';
    public authToken: string;

    constructor(private http: HttpClient) {
        this.getAuthToken();
        console.log(this.authToken);
    }

    private getAuthToken(): void {
        let token: string | null = sessionStorage.getItem('auth');
        if (token) {
            this.authToken = token;
        } else {
            this.authToken = "";
        }
    }

    private setAuthToken(token: string): void {
        sessionStorage.setItem('auth', token);
    }

    private unsetAuthToken(): void {
        sessionStorage.removeItem('auth');
        this.authToken = "";
    }

    ngOnInit(): void {
        this.setLoginOnClick();
        this.setLogoutOnClick();
    }

    private async setLoginOnClick(): Promise<void> {
        let usernameInput: HTMLInputElement = document.getElementById("usernameInput") as HTMLInputElement;
        let passwordInput: HTMLInputElement = document.getElementById("passwordInput") as HTMLInputElement;
        let adminCheck: HTMLInputElement = document.getElementById("adminCheck") as HTMLInputElement;

        let loginButton: HTMLButtonElement = document.getElementById("loginButton") as HTMLButtonElement;
        loginButton.onclick = async (event: MouseEvent) => {
            loginButton.disabled = true;
            usernameInput.disabled = true;
            passwordInput.disabled = true;
            adminCheck.disabled = true;

            let authRequest: IAuthRequest = {
                User: {
                    name: usernameInput.value.trim(),
                    isAdmin: adminCheck.checked
                },
                Secret: {
                    password: passwordInput.value.trim()
                }
            };

            let res: HttpAnyResponse<string> = await new Promise<HttpAnyResponse<string>>(
                (resolve) => {
                    this.http.put<string>('/authenticate', authRequest, { observe: 'response',
                        withCredentials: false })
                    .pipe(take(1), catchError((error: HttpErrorResponse) => {
                        resolve(error);
                        throw Error();
                    })).forEach(v => resolve(v));
                }
            );

            let response: HttpResponse<string> | null = res as HttpResponse<string>;
            if (response && response.status == HttpStatusCode.Ok && response.body) {
                this.setAuthToken(response.body);
            } else {
                console.log(res);
            }

            this.getAuthToken();

            loginButton.disabled = false;
            usernameInput.disabled = false;
            passwordInput.disabled = false;
            adminCheck.disabled = false;
        };

        loginButton.disabled = false;
    }

    private async setLogoutOnClick(): Promise<void> {
        let logoutButton: HTMLButtonElement = document.getElementById("logoutButton") as HTMLButtonElement;
        logoutButton.onclick = (event: MouseEvent) => this.unsetAuthToken();
        logoutButton.disabled = false;
    }
}
