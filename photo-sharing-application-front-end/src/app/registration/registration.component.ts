import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { RegistrationService } from './registration.service';
import { Router } from '@angular/router';
import { RegistrationForm } from '../model/registration-form';
import { FormErrors } from '../model/form-errors';

@Component({
  selector: 'app-register',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent {
  registrationForm: RegistrationForm = {
    nickname: '',
    email: '',
    password: '',
  };

  formErrors = {} as FormErrors;

  isValid = true;

  constructor(
    private registrationService: RegistrationService,
    private router: Router
  ) {}

  onSubmit(form: NgForm): void {
    this.isValid = true;
    this.registrationService.register(this.registrationForm).subscribe({
      next: (data) => this.router.navigate(['/login']),
      error: (err) => {
        if(err.status == "400") {
        this.formErrors = err.error.fieldNames;
        this.isValid = false;
        }
        
      },
    });
  }
}
