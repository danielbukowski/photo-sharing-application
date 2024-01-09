import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VerificationService } from '../services/verification/verification.service';

@Component({
  selector: 'app-verifcation-page',
  templateUrl: './verification-page.component.html',
})
export class VerificationPageComponent implements OnInit {
  messageResponse: WritableSignal<string> = signal('');

  constructor(
    private route: ActivatedRoute,
    private verificationService: VerificationService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams) => {
      setTimeout(() => this.verifyAccount(queryParams['token']), 2000);
    });
  }

  verifyAccount(token: string): void {
    this.verificationService.verifyAccountByToken(token).subscribe({
      next: () => {
        this.messageResponse.set(
          'Your account has been successfully verified! C:'
        );
      },
      error: (e) => {
        this.messageResponse.set(e.error.reason);
      },
    });
  }
}
