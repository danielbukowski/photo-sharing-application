import { Component, OnInit } from '@angular/core';
import { Credentials } from '../model/credentials';
import { LoginService } from './login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  credentials: Credentials = {} as Credentials;
  hasBadCredentials: boolean = false;

  constructor(
    private loginService: LoginService,
    private router: Router
  ) {}

  onSubmit(): void {
    this.loginService.login(this.credentials).subscribe({
      next: (data) => this.router.navigateByUrl('/home'),
      error: (err) => {(this.hasBadCredentials = true)
        console.log(err);
        
      
      },
    });
  }
}
