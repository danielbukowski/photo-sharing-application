import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegistrationService } from '../services/registration/registration.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration-page',
  templateUrl: './registration-page.component.html',
})
export class RegistrationPageComponent implements OnInit {
  validationErrorMessageList: WritableSignal<
    { nickname: string[]; email: string[]; password: string[] } | undefined
  > = signal({
    nickname: [] as string[],
    email: [] as string[],
    password: [] as string[],
  });
  generalErrorMessage: WritableSignal<string> = signal('');
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
    this.validationErrorMessageList.set({
      nickname: [],
      email: [],
      password: [],
    });
    this.generalErrorMessage.set('');
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
            this.validationErrorMessageList.set({ ...e.error.fieldNames });
          } else {
            this.generalErrorMessage.set(e.error.reason || 'Internal Server Error');
          }
          this.isBeingProcessed.set(false);
        },
      });
  }
}
