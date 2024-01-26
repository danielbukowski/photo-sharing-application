import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-image-detail-comment',
  templateUrl: './image-detail-comment.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImageDetailCommentComponent {
  @Input({ required: true }) content!: string;
  @Input({ required: true }) wroteBy!: string;
}
