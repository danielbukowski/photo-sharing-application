import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class LogoutService {
  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.http.delete('http://localhost:8081/api/v1/sessions').subscribe({
      next: (n) => {
        this.authService.updateAuthentication();
        this.router.navigate(['/home']);
      },
    });
  }
}
