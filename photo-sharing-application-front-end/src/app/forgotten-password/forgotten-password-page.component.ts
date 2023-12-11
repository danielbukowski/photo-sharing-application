import { Component, WritableSignal, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ForgottenPasswordService } from '../services/forgotten-password/forgotten-password.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgotten-password-page',
  templateUrl: './forgotten-password-page.component.html'
})
export class ForgottenPasswordPageComponent {
  validationError: WritableSignal<string> = signal('');
  generalError: WritableSignal<string> = signal('');
  isBeingProcessed: WritableSignal<boolean> = signal(false);
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
    this.validationError.set('');
    this.generalError.set('');
  }

  onSubmit() {
    this.isBeingProcessed.set(true);
    this.forgottenPasswordService
      .sendPasswordResetRequest(this.forgetPasswordForm.value)
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (e) => {
          this.resetForm();
          if (e.error.fieldNames) {
            this.validationError.set(e.error.fieldNames.email);
          } else {
            this.generalError.set(e.error.reason);
          }
          this.isBeingProcessed.set(false);
        },
      });
  }
}
