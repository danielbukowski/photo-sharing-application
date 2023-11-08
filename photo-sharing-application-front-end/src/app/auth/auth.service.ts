import { Injectable, Signal, WritableSignal, signal } from '@angular/core';
import { Account } from '../model/account';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, filter, map } from 'rxjs';

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
    this.http
    .get<Account | undefined>('http://localhost:8081/api/v3/accounts')
    .subscribe({
      next: (n) => this.#accountDetails.set(n),
      error: (e) => this.#accountDetails.set(undefined),
    });
  }

  isAuthenticated(): boolean {
    return this.#accountDetails() !== undefined;
  }
  
}
