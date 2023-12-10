import { HttpClient } from '@angular/common/http';
import { Injectable, Signal, WritableSignal, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CsrfTokenService {
  #token: WritableSignal<string> = signal<string>('');

  constructor(private http: HttpClient) {}

  getCsrfToken(): Signal<string> {
    return this.#token.asReadonly();
  }

  updateCsrfToken(): void {
    this.http
      .get<any>('http://localhost:8081/api/v1/csrf')
      .subscribe({
        next: (d) => this.#token.set(d.data.token),
        error: () => this.#token.set(''),
      });
  }
}
