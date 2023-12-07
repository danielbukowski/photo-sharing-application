import { Component, OnInit } from '@angular/core';
import { LoginService } from '../service/login/login.service';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth/auth.service';
import { CsrfTokenService } from '../service/csrf-token/csrf-token.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  hasBadCredentials$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  loginForm!: FormGroup;

  constructor(
    private loginService: LoginService,
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
    this.loginService.login(this.loginForm.value).subscribe({
      next: (n) => {
        this.csrfToken.generateCsrfToken();
        this.authService.updateAuthentication();
        this.router.navigate(['/home']);
      },
      error: (e) => {
        this.hasBadCredentials$.next(true);
      },
    });
  }
}
