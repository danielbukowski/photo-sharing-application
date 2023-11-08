import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VerificationService } from './verification.service';
import { log } from 'console';

@Component({
  selector: 'app-verify',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css'],
})
export class VerificationComponent implements OnInit {
  private token: string = "";
  verificationResponse: string = "";

  constructor(
    private route: ActivatedRoute,
    private verificationService: VerificationService
  ) {}
  ngOnInit(): void {
    this.route.queryParams.subscribe((p) => {
      this.token = p['token'];
    });
  }

  verifyAccont(): void {
    this.verificationService.verifyAccountByToken(this.token).subscribe({
      next: (n) => { 
        this.verificationResponse = "Your account has been successfully verified";
      },
      error: (e) => {
        this.verificationResponse = e.error.reason;
      },
    });
  }
}
