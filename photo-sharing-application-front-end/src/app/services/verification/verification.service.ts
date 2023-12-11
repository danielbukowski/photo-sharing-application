import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class VerificationService {
  constructor(private http: HttpClient) {}

  verifyAccountByToken(token: string): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}/api/v3/accounts/email-verification`,
      {},
      {
        params: {
          token,
        },
      }
    );
  }
}
