import { Injectable, Signal, signal } from '@angular/core';
import { Account } from '../../models/account';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Credentials } from 'src/app/models/credentials';
import { Observable } from 'rxjs';

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
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization:
          'Basic ' +
          Buffer.from(`${credentials.email}:${credentials.password}`).toString(
            'base64'
          ),
      }),
    };
    return this.http.post(
      `${environment.apiUrl}/api/v1/sessions`,
      {},
      httpOptions
    );
  }
}
