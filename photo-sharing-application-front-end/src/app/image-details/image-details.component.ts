import { Component, OnInit, Signal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ImageService } from '../image/image.service';
import { Comment } from '../model/comment';
import { Page } from '../model/page';
import { CommentService } from '../comment/comment.service';
import { AuthService } from '../auth/auth.service';
import { Account } from '../model/account';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-image-details',
  templateUrl: './image-details.component.html',
  styleUrls: ['./image-details.component.css'],
})
export class ImageDetailsComponent implements OnInit {
  IdOfCurrentDisplayedImage = signal<string>("");
  numberOfLikes = signal<number>(0);
  accountDetails!: Signal<Account | undefined>;
  commentPage$!: Observable<Page<Comment>>;

  constructor(
    private route: ActivatedRoute,
    private imageService: ImageService,
    private commentService: CommentService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe({next: params => {
      this.IdOfCurrentDisplayedImage.set(params.get('id') || "");
    }});

    this.accountDetails = this.authService.getAccountDetails();

    this.imageService
    .getNumberOfLikesFromImage(this.IdOfCurrentDisplayedImage())
    .subscribe({
      next: (d) => {
        this.numberOfLikes.set(d.data.likes);
      },
    });

    this.updatePageContent(0);
  }

  private updatePageContent(pageNumber: number) {
    this.commentPage$ = this.commentService.getCommentsFromImage(this.IdOfCurrentDisplayedImage(), pageNumber);
  }


  fetchNextPageOfComments(currentPageNumber: number, isLast: boolean) {
    if(isLast) return;
    this.updatePageContent(currentPageNumber + 1);
  }

  fetchPreviousPageOfComments(currentPagenumber: number) {
    if(currentPagenumber <= 0) return;
    this.updatePageContent(currentPagenumber - 1);
  }

}
