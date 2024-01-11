import { Component, Input, WritableSignal, signal } from '@angular/core';

@Component({
  selector: 'app-home-image',
  templateUrl: './home-image.component.html',
})
export class HomeImageComponent {
  isLoaded: WritableSignal<boolean> = signal(false);
  @Input() imageId!: string;
}
