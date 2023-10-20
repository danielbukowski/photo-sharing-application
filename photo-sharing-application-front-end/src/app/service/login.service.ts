import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Credentials } from '../interfaces/credentials';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http: HttpClient) { }


  login(credentials: Credentials) {
    this.http.post("http://localhost:8081/api/v3/accounts", credentials);
  }
}
