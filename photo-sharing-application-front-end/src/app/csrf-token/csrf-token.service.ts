import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CsrfTokenService {
  private token = signal<string>('');

  constructor(private http: HttpClient) {}

  get getCsrfToken(): string {
    return this.token() as string;
  }

  generateCsrfToken() {
    return this.http
      .get('http://localhost:8081/api/v1/csrf')
      .subscribe((resp: any) => this.token.set(resp.data.token));
  }
}
