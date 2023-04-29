import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'module-registry';
    public authToken: string;

    constructor() {
        this.getAuthToken();
    }

    private getAuthToken(): void {
        let token: string | null = sessionStorage.getItem("auth");
        if (token) {
            this.authToken = token;
        } else {
            this.authToken = "";
        }
    }

    private setAuthToken(token: string): void {
        sessionStorage.setItem("auth", token);
    }

    ngOnInit(): void {
    }
}
