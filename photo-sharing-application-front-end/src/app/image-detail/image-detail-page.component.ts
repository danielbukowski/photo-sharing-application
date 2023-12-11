import { Component, OnInit, Signal, WritableSignal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Comment } from '../models/comment';
import { Page } from '../models/page';
import { CommentService } from '../services/comment/comment.service';
import { AuthService } from '../services/auth/auth.service';
import { Account } from '../models/account';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-image-detail-page',
  templateUrl: './image-detail-page.component.html'
})
export class ImageDetailPageComponent implements OnInit {
  IdOfCurrentDisplayedImage: WritableSignal<string> = signal<string>('');
  accountDetails!: Signal<Account | undefined>;
  commentPage$!: Observable<Page<Comment>>;

  constructor(
    private route: ActivatedRoute,
    private commentService: CommentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe({
      next: (params) => {
        this.IdOfCurrentDisplayedImage.set(params.get('id') || '');
      },
    });
    this.accountDetails = this.authService.getAccountDetails();
    this.updatePageContent(0);
  }

  private updatePageContent(pageNumber: number) {
    this.commentPage$ = this.commentService.getCommentsFromImage(
      this.IdOfCurrentDisplayedImage(),
      pageNumber
    );
  }

  addCommentToImage(textAreaElement: HTMLTextAreaElement): void {
    if (textAreaElement.value === '') return;

    this.commentService
      .addCommentToImage(
        textAreaElement.value,
        this.IdOfCurrentDisplayedImage()
      )
      .subscribe({
        next: () => {
          textAreaElement.value = '';
        },
        error: () => {},
      });
  }

  fetchNextPageOfComments(currentPageNumber: number, isLast: boolean) {
    if (isLast) return;
    this.updatePageContent(currentPageNumber + 1);
  }

  fetchPreviousPageOfComments(currentPageNumber: number) {
    if (currentPageNumber <= 0) return;
    this.updatePageContent(currentPageNumber - 1);
  }
}
