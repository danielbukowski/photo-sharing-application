import { ChangeDetectionStrategy, Component, Input, WritableSignal, signal } from '@angular/core';

@Component({
  selector: 'app-home-image',
  templateUrl: './home-image.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomeImageComponent {
  isLoaded: WritableSignal<boolean> = signal(false);
  @Input({ required: true }) imageId!: string;
}
