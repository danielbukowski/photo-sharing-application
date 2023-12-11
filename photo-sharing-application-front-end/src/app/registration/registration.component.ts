import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegistrationService } from '../services/registration/registration.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './registration.component.html'
})
export class RegistrationComponent implements OnInit {
  validationErrors = signal({
    nickname: [] as string[],
    email: [] as string[],
    password: [] as string[],
  });
  generalError: WritableSignal<string> = signal('');
  isBeingProcessed: WritableSignal<boolean> = signal(false);
  registrationForm!: FormGroup;

  constructor(
    private registrationService: RegistrationService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.registrationForm = this.fb.group({
      nickname: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  private resetErrorMessages() {
    this.validationErrors.set({
      nickname: [],
      email: [],
      password: [],
    });
    this.generalError.set('');
  }

  onSubmit(): void {
    this.resetErrorMessages();
    this.isBeingProcessed.set(true);
    this.registrationService
      .registerAccount(this.registrationForm.value)
      .subscribe({
        next: () => this.router.navigate(['/login']),
        error: (e) => {
          if (e.error.fieldNames) {
            this.validationErrors.update(v => v = { ...e.error.fieldNames });
          } else {
            this.generalError.set(e.error.reason);
          }
          this.isBeingProcessed.set(false);
        },
      });
  }
}
