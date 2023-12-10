import { Component, OnInit, Signal, computed, signal } from '@angular/core';
import { LogoutService } from '../services/logout/logout.service';
import { AuthService } from '../services/auth/auth.service';
import { Account } from '../models/account';
import { Observable, of } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
})
export class NavbarComponent implements OnInit {
  accountDetails: Signal<Account | undefined> = signal(undefined);
  isAuthenticated = computed(() => this.accountDetails() !== undefined);

  constructor(
    public logoutService: LogoutService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.initThemeButton();
    this.accountDetails = this.authService.getAccountDetails();
  }

  private initThemeButton() {
    if (localStorage.getItem('theme') === 'dark') {
      document.getElementById('moon-icon')?.toggleAttribute('hidden');
    } else {
      document.getElementById('sun-icon')?.toggleAttribute('hidden');
    }
  }

  changeTheme(): void {
    if (localStorage.getItem('theme') === 'dark') {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    } else {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    }
    document.getElementById('moon-icon')?.toggleAttribute('hidden');
    document.getElementById('sun-icon')?.toggleAttribute('hidden');
  }
}
