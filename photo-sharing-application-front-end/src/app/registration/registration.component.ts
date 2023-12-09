import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegistrationService } from '../service/registration/registration.service';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './registration.component.html'
})
export class RegistrationComponent implements OnInit {
  validationErrors$ = new BehaviorSubject({
    nickname: [] as string[],
    email: [] as string[],
    password: [] as string[],
  });
  generalError$: BehaviorSubject<string> = new BehaviorSubject('');
  isBeingProcessed$: BehaviorSubject<boolean> = new BehaviorSubject(false);
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
    this.validationErrors$.next({
      nickname: [],
      email: [],
      password: [],
    });
    this.generalError$.next('');
  }

  onSubmit(): void {
    this.resetErrorMessages();
    this.isBeingProcessed$.next(true);
    this.registrationService
      .registerAccount(this.registrationForm.value)
      .subscribe({
        next: () => this.router.navigate(['/login']),
        error: (e) => {
          if (e.error.fieldNames) {
            this.validationErrors$.next({ ...e.error.fieldNames });
          } else {
            this.generalError$.next(e.error.reason);
          }
          this.isBeingProcessed$.next(false);
        },
      });
  }
}
