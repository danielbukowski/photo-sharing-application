import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ForgottenPasswordService } from './forgotten-password.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-password-reset',
  templateUrl: './forgotten-password.component.html',
  styleUrls: ['./forgotten-password.css']
})
export class ForgottenPasswordComponent {

  constructor(private forgottenPasswordService: ForgottenPasswordService, private router: Router) { }

  errorReason: string = "";

  onSubmit(form: NgForm) {
      this.forgottenPasswordService.sendPasswordResetRequest(form.value).subscribe({
        next: (n) => {
          this.router.navigate(["/login"]);
        }, 
        error: err => {
          if(err.status === 404) {
            this.errorReason = "The email has been not found"
          } else {
            this.errorReason = err.error.fieldNames.email;
          }
         form.resetForm();
        }}
      )
  }
}
