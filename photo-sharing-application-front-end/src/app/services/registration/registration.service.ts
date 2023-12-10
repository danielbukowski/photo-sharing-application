import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegistrationForm } from '../../models/registration-form';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  constructor(private http: HttpClient) {}

  registerAccount(registrationForm: RegistrationForm): Observable<any> {
    return this.http.post(
      'http://localhost:8081/api/v3/accounts',
      registrationForm,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  }
}
