import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class VerificationService {
  constructor(private http: HttpClient) {}

  verifyAccountByToken(token: string): Observable<any> {
    return this.http.post(
      `http://localhost:8081/api/v3/accounts/email-verification`,
      {},
      {
        params: {
          token,
        },
      }
    );
  }
}
