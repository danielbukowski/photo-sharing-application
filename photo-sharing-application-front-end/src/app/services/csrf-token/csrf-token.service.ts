import { HttpClient } from '@angular/common/http';
import { Injectable, Signal, WritableSignal, signal } from '@angular/core';
import { environment } from 'src/environments/environment';

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
      .get<any>(`${environment.apiUrl}/api/v1/csrf`)
      .subscribe({
        next: (d) => this.#token.set(d.data.token),
        error: () => this.#token.set(''),
      });
  }
}
