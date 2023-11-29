import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VerificationService } from './verification.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css'],
})
export class VerificationComponent implements OnInit {
  token: string = "";
  verificationResponse: string = "";

  constructor(
    private route: ActivatedRoute,
    private verificationService: VerificationService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams) => {
      this.token = queryParams['token'];
    });
  }

  verifyAccount(): void {
    this.verificationService.verifyAccountByToken(this.token).subscribe({
      next: (n) => { 
        this.verificationResponse = "Your account has been successfully verified! C:";
      },
      error: (e) => {
        this.verificationResponse = e.error.reason;
      },
    });
  }
}
