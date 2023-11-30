import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegistrationService } from './registration.service';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit, OnDestroy {
  registrationForm!: FormGroup;
  errorsInForm = {
    nickname:  [] as string[],
    email: [] as string[],
    password: [] as string[]
  };
  generalErrorReason!: string;
  isBeingProcessed$: BehaviorSubject<boolean> = new BehaviorSubject(false);

  constructor(
    private registrationService: RegistrationService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.registrationForm = this.fb.group({
      nickname: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnDestroy(): void {
    this.isBeingProcessed$.unsubscribe();
  }

  private resetErrorMessages() {
    this.errorsInForm = {
      nickname: [],
      email: [],
      password: []
    };
    this.generalErrorReason = "";
  }

  onSubmit(): void {
    this.resetErrorMessages();
    this.isBeingProcessed$.next(true);
    this.registrationService
      .registerAccount(this.registrationForm.value)
      .subscribe({
        next: (n) => this.router.navigate(['/login']),
        error: (e) => {
          if (e.error.fieldNames) {
            this.errorsInForm.nickname.push(...e.error.fieldNames.nickname || []);
            this.errorsInForm.email.push(...e.error.fieldNames.email || []);
            this.errorsInForm.password.push(...e.error.fieldNames.password || []);
          } else {
            this.generalErrorReason = e.error.reason;
          }
          this.isBeingProcessed$.next(false);
        }
      });
  }
}
