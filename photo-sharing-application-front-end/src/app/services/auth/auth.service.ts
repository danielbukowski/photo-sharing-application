import { Injectable, Signal, signal } from '@angular/core';
import { Account } from '../../models/account';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Credentials } from 'src/app/models/credentials';
import { Observable } from 'rxjs';
import { Buffer } from 'buffer';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  #accountDetails = signal<Account | undefined>(undefined);

  constructor(private http: HttpClient) {}

  getAccountDetails(): Signal<Account | undefined> {
    return this.#accountDetails.asReadonly();
  }

  updateAuthentication(): void {
    this.http.get<any>(`${environment.apiUrl}/api/v3/accounts`).subscribe({
      next: (d) => this.#accountDetails.set(d.data),
      error: () => this.#accountDetails.set(undefined),
    });
  }

  logIn(credentials: Credentials): Observable<any> {
    const headers = new HttpHeaders().set(
      'Authorization',
      'Basic ' +
        Buffer.from(`${credentials.email}:${credentials.password}`).toString(
          'base64'
        )
    );

    return this.http.post(
      `${environment.apiUrl}/api/v1/sessions`,
      {},
      {
        headers,
      }
    );
  }

  logOut(): void {
    this.http.delete(`${environment.apiUrl}/api/v1/sessions`).subscribe({
      next: () => {
        this.#accountDetails.set(undefined);
      },
    });
  }
}
