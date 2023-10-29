import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { PasswordResetService } from './password-reset.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.css']
})
export class PasswordResetComponent {

  constructor(private passwordResetService: PasswordResetService, private router: Router) { }

  errorReason: string = "";

  onSubmit(form: NgForm) {
    console.log(form.value);
    
      this.passwordResetService.sendPasswordResetRequest(form.value).subscribe({
        next: (n) => {
          this.router.navigate(["/login"]);
        }, 
        error: err => {
          form.resetForm();
          this.errorReason = err.error.fieldNames.email;
          console.log(err);
          
        }
      }
      )
  }
}
