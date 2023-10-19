import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {

  ngOnInit(): void {
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
