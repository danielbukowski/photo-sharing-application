import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ForgottenPasswordService } from '../services/forgotten-password/forgotten-password.service';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-forgotten-password-page',
  templateUrl: './forgotten-password-page.component.html'
})
export class ForgottenPasswordPageComponent {
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
        next: () => {
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
