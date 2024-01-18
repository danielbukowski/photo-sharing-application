import { Component, WritableSignal, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordService } from '../services/password/password.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgotten-password-page',
  templateUrl: './forgotten-password-page.component.html',
})
export class ForgottenPasswordPageComponent {
  validationErrorMessage: WritableSignal<string> = signal('');
  generalErrorMessage: WritableSignal<string> = signal('');
  isBeingProcessed: WritableSignal<boolean> = signal(false);
  forgottenPasswordForm!: FormGroup;

  constructor(
    private passwordService: PasswordService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.forgottenPasswordForm = this.fb.group({
      email: ['', Validators.required],
    });
  }

  private resetErrorMessages(): void {
    this.validationErrorMessage.set('');
    this.generalErrorMessage.set('');
  }

  onSubmit() {
    this.isBeingProcessed.set(true);
    this.passwordService
      .sendPasswordResetRequest(this.forgottenPasswordForm.value)
      .subscribe({
        next: () => {
          this.router.navigateByUrl('/login');
        },
        error: (e) => {
          this.resetErrorMessages();
          if (e.error.fieldNames) {
            this.validationErrorMessage.set(e.error.fieldNames.email);
          } else {
            this.generalErrorMessage.set(e.error.reason || 'Internal Server Error');
          }
          this.isBeingProcessed.set(false);
        },
      });
  }
}
