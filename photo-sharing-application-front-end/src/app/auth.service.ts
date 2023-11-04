import { Injectable, signal } from '@angular/core';
import { Account } from './model/account';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, filter, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
// DISCLAIMER: STORE ONLY OBSERVABLE
export class AuthService {
  private account$ = new BehaviorSubject<Account | undefined>(undefined);
  public isLoggedIn$: Observable<boolean>;

  constructor(private http: HttpClient) {
    this.http
      .get<Account | undefined>('http://localhost:8081/api/v3/accounts')
      .subscribe({
        next: (n) => this.account$.next(n),
        error: (e) => this.account$.next(undefined),
      });

    this.isLoggedIn$ = this.account$.pipe(
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
