import { Injectable, signal } from '@angular/core';
import { Account } from '../model/account';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, filter, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
// DISCLAIMER: STORE ONLY OBSERVABLE
export class AuthService {
  private accountDetails$ = new BehaviorSubject<Account | undefined>(undefined);

  constructor(private http: HttpClient) {
   }

  updateAuthentication(): void {
    this.http
    .get<Account | undefined>('http://localhost:8081/api/v3/accounts')
    .subscribe({
      next: (n) => this.accountDetails$.next(n),
      error: (e) => this.accountDetails$.next(undefined),
    });
  }

  isAuthenticated(): Observable<boolean> {
    return this.accountDetails$.pipe(
      filter((account) => {
        return account !== undefined;
      }),
      map((account) => {
        if (account) {
          return true;
        }
        return false;
      })
    );
  }
}
