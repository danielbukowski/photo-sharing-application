import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Credentials } from '../../models/credentials';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { Buffer } from 'buffer';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private http: HttpClient) {}

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
