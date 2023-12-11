import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class LogoutService {
  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private router: Router
  ) {}

  logOut(): void {
    this.http.delete(`${environment.apiUrl}/api/v1/sessions`).subscribe({
      next: () => {
        this.authService.updateAuthentication();
        this.router.navigate(['/home']);
      },
    });
  }
}
