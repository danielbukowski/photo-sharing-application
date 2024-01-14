import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-image-detail-comment',
  templateUrl: './image-detail-comment.component.html',
})
export class ImageDetailCommentComponent {
  @Input({ required: true }) content!: string;
  @Input({ required: true }) email!: string;
}
