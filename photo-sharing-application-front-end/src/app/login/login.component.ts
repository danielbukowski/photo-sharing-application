import { Component, OnInit } from '@angular/core';
import { Credentials } from '../model/credentials';
import { LoginService } from './login.service';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { CsrfTokenService } from '../csrf-token/csrf-token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  credentials: Credentials = {} as Credentials;
  haveBadCredentials: boolean = false;

  constructor(
    private loginService: LoginService,
    private router: Router,
    private csrfToken: CsrfTokenService,
    private authService: AuthService
  ) {}

  onSubmit(): void {
    this.loginService.login(this.credentials).subscribe({
      next: (d) => {
        this.csrfToken.generateCsrfToken();
        this.authService.updateAuthentication();
        this.router.navigateByUrl('/home');
      },
      error: (e) => {
        this.haveBadCredentials = true;
      },
    });
  }
}
