import { Component, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ForgottenPasswordService } from './forgotten-password.service';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-password-reset',
  templateUrl: './forgotten-password.component.html',
  styleUrls: ['./forgotten-password.css'],
})
export class ForgottenPasswordComponent implements OnDestroy {
  validationError$: BehaviorSubject<string> = new BehaviorSubject('');
  generalError$: BehaviorSubject<string> = new BehaviorSubject('');
  isBeingProcessed$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  forgetPasswordForm!: FormGroup;

  constructor(
    private forgottenPasswordService: ForgottenPasswordService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.forgetPasswordForm = this.fb.group({
      email: ['', Validators.required],
    });
  }

  ngOnDestroy(): void {
    this.validationError$.unsubscribe();
    this.generalError$.unsubscribe();
    this.isBeingProcessed$.unsubscribe();
  }

  resetForm(): void {
    this.forgetPasswordForm.reset();
    this.validationError$.next('');
    this.generalError$.next('');
  }

  onSubmit() {
    this.isBeingProcessed$.next(true);
    this.forgottenPasswordService
      .sendPasswordResetRequest(this.forgetPasswordForm.value)
      .subscribe({
        next: (n) => {
          this.router.navigate(['/login']);
        },
        error: (e) => {
          this.resetForm();
          if (e.error.fieldNames) {
            this.validationError$.next(e.error.fieldNames.email);
          } else {
            this.generalError$.next(e.error.reason);
          }
          this.isBeingProcessed$.next(false);
        },
      });
  }
}
