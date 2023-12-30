import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PasswordService {
  constructor(private http: HttpClient) {}

  sendPasswordResetRequest(email: string): Observable<any> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(
      `${environment.apiUrl}/api/v3/accounts/password-reset`,
      email,
      { headers }
    );
  }

  changePasswordByToken(token: string, newPassword: string): Observable<any> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put(
      `${environment.apiUrl}/api/v3/accounts/password-reset`,
      { newPassword },
      {
        headers,
        params: { token },
      }
    );
  }
}
