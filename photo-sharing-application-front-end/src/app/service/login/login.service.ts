import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Credentials } from '../../model/credentials';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';
import { Buffer } from 'buffer';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  constructor(private http: HttpClient) {}

  login(credentials: Credentials): Observable<any> {
    const httpOptions = {
      headers: new HttpHeaders({
        Authorization:'Basic ' + Buffer.from(`${credentials.email}:${credentials.password}`).toString('base64')
      })
    };
    return this.http.post(
      'http://localhost:8081/api/v1/sessions',
      {},
      httpOptions
    );
  }
}
