import { Injectable, Signal, signal } from '@angular/core';
import { Account } from '../../models/account';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

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
}
