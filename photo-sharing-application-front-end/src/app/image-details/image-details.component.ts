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

  }

}
