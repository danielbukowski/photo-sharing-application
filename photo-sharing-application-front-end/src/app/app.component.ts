import { Component, OnInit } from '@angular/core';
import { CsrfTokenService } from './csrf-token/csrf-token.service';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {

  constructor(private CsrfTokenService: CsrfTokenService, private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.updateAuthentication();
    this.setTheme();
    this.CsrfTokenService.generateCsrfToken();
  }

  private setTheme() {
    if (localStorage.getItem('theme') === 'dark' || window.matchMedia('(prefers-color-scheme: dark)').matches) {
      localStorage.setItem('theme', 'dark');
      document.documentElement.classList.add('dark');
    } else {
      localStorage.setItem('theme', 'light');
      document.documentElement.classList.remove('dark');
    }
  }

}
