import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PasswordService } from '../services/password/password.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-password-reset-page',
  templateUrl: './password-reset-page.component.html',
})
export class PasswordResetPageComponent implements OnInit {
  passwordResetForm!: FormGroup;
  token: string = '';
  isBeingProcessed: WritableSignal<boolean> = signal(false);
  generalErrorMessage: WritableSignal<string> = signal('');
  validationErrors: WritableSignal<string[]> = signal([]);

  constructor(
    private fb: FormBuilder,
    private passwordService: PasswordService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams) => {
      this.token = queryParams['token'];
    });
    this.passwordResetForm = this.fb.group({
      newPassword: ['', Validators.required],
    });
  }

  onSubmit(): void {
    this.isBeingProcessed.set(true);
    this.passwordService
      .changePasswordByToken(this.token, this.passwordResetForm.value)
      .subscribe({
        next: () => {
          this.router.navigate(['home']);
        },
        error: (e) => {
          this.generalErrorMessage.set(e.error.reason);
          this.validationErrors.set(e.error.fieldNames?.newPassword || []);
          this.isBeingProcessed.set(false);
        },
      });
  }
}
