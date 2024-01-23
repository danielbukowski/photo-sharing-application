import {
  Component,
  OnInit,
  Signal,
  TrackByFunction,
  WritableSignal,
  computed,
  signal,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Comment } from '../models/comment';
import { Page } from '../models/page';
import { CommentService } from '../services/comment/comment.service';
import { AuthService } from '../services/auth/auth.service';
import { Account } from '../models/account';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-image-detail-page',
  templateUrl: './image-detail-page.component.html',
})
export class ImageDetailPageComponent implements OnInit {
  imageId: WritableSignal<string> = signal<string>('');
  accountDetails!: Signal<Account | undefined>;
  isLoggedIn = computed(() => this.accountDetails() !== undefined);
  isEmailVerified = computed(() => this.accountDetails()?.isEmailVerified);
  commentPage$!: Observable<Page<Comment>>;
  isBeingProcessed: WritableSignal<boolean> = signal(false);
  commentTextArea: string = '';

  constructor(
    private route: ActivatedRoute,
    private commentService: CommentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next: (params) => {
        this.imageId.set(params.get('id') || '');
      },
    });
    this.accountDetails = this.authService.getAccountDetails();
    this.updatePageContent(0);
  }

  trackByCommentId: TrackByFunction<string> = (index, commentId: string) => commentId;

  updatePageContent(pageNumber: number): void {
    this.commentPage$ = this.commentService.getCommentsFromImage(
      this.imageId(),
      pageNumber
    );
  }

  addCommentToImage(): void {
    this.isBeingProcessed.set(true);
    this.commentService
      .addCommentToImage(
        this.commentTextArea,
        this.imageId()
      )
      .subscribe({
        next: () => {
          this.commentTextArea = '';
          this.isBeingProcessed.set(false);
        },
        error: () => {
          this.isBeingProcessed.set(false);
        },
      });
  }

}
