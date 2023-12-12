import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { CsrfTokenService } from '../services/csrf-token/csrf-token.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html'
})
export class LoginPageComponent implements OnInit {
  hasBadCredentials: WritableSignal<boolean> = signal(false);
  loginForm!: FormGroup;

  constructor(
    private router: Router,
    private csrfToken: CsrfTokenService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit(): void {
    this.authService.logIn(this.loginForm.value).subscribe({
      next: () => {
        this.csrfToken.updateCsrfToken();
        this.authService.updateAuthentication();
        this.router.navigate(['/home']);
      },
      error: () => {
        this.hasBadCredentials.set(true);
      },
    });
  }
}
