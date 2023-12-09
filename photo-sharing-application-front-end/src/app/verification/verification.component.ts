import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { VerificationService } from '../service/verification/verification.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-verify',
  templateUrl: './verification.component.html'
})
export class VerificationComponent implements OnInit {
  verificationResponse$: BehaviorSubject<string> = new BehaviorSubject('');

  constructor(
    private route: ActivatedRoute,
    private verificationService: VerificationService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams) => {
      setTimeout(() => this.verifyAccount(queryParams['token']), 1000);
    });
  }

  verifyAccount(token: string): void {
    this.verificationService.verifyAccountByToken(token).subscribe({
      next: () => {
        this.verificationResponse$.next(
          'Your account has been successfully verified! C:'
        );
      },
      error: (e) => {
        this.verificationResponse$.next(e.error.reason);
      },
    });
  }
}
