import { Component, OnChanges, OnInit, Signal, SimpleChanges, WritableSignal, signal } from '@angular/core';
import { LogoutService } from '../logout/logout.service';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';
import { Account } from '../model/account';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  accountDetails: Signal<Account | undefined> = signal(undefined);

  constructor(public logoutService: LogoutService, public authService: AuthService) { }

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
