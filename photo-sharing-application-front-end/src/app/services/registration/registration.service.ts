import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegistrationForm } from '../../models/registration-form';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class RegistrationService {
  constructor(private http: HttpClient) {}

  registerAccount(registrationForm: RegistrationForm): Observable<unknown> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(
      `${environment.apiUrl}/api/v3/accounts`,
      registrationForm,
      {
        headers,
      }
    );
  }
}
