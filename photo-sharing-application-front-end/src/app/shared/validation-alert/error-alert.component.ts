import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-error-alert',
  templateUrl: './error-alert.component.html',
})
export class ErrorAlertComponent {
  @Input() errorMessage?: string;
}
