import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ForgottenPasswordService {
  constructor(private http: HttpClient) {}

  sendPasswordResetRequest(email: string): Observable<any> {
    let headers = new HttpHeaders();
    headers.append('Content-Type', 'application/json');
    return this.http.post(
      'http://localhost:8081/api/v3/accounts/password-reset',
      email,
      { headers: headers }
    );
  }
}
