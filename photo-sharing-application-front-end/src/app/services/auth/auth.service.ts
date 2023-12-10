import { Injectable, Signal, signal } from '@angular/core';
import { Account } from '../../models/account';
import { HttpClient } from '@angular/common/http';

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
    this.http.get<any>('http://localhost:8081/api/v3/accounts').subscribe({
      next: (d) => this.#accountDetails.set(d.data),
      error: () => this.#accountDetails.set(undefined),
    });
  }
}
