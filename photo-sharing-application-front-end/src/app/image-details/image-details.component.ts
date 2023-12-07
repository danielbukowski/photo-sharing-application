import { Component, OnInit, Signal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Comment } from '../model/comment';
import { Page } from '../model/page';
import { CommentService } from '../service/comment/comment.service';
import { AuthService } from '../service/auth/auth.service';
import { Account } from '../model/account';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-image-details',
  templateUrl: './image-details.component.html'
})
export class ImageDetailsComponent implements OnInit {
  IdOfCurrentDisplayedImage = signal<string>('');
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
