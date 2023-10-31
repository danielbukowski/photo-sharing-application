import { Component, OnInit } from '@angular/core';
import { CsrfTokenService } from './csrf-token/csrf-token.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {

  constructor(private CsrfTokenService: CsrfTokenService) {}

  ngOnInit(): void {
    if (localStorage.getItem('theme') === 'dark' || window.matchMedia('(prefers-color-scheme: dark)').matches) {
      localStorage.setItem('theme', 'dark');
      document.documentElement.classList.add('dark');
    } else {
      localStorage.setItem('theme', 'light');
      document.documentElement.classList.remove('dark');
    }
    this.CsrfTokenService.generateCsrfToken();
  }

}
