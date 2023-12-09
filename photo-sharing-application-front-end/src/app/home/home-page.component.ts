import { Component, OnInit } from '@angular/core';
import { ImageService } from '../service/image/image.service';
import { Observable } from 'rxjs';
import { Page } from '../model/page';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
})
export class HomePageComponent implements OnInit {
  imagePage$!: Observable<Page<String>>;

  constructor(private imageService: ImageService) {}

  ngOnInit(): void {
    this.imagePage$ = this.imageService.getPageOfLatestImages(0);
  }

  private updatePageContent(pageNumber: number) {
    this.imagePage$ = this.imageService.getPageOfLatestImages(pageNumber);
  }

  fetchNextPageOfComments(currentPageNumber: number, isLast: boolean) {
    if (isLast) return;
    this.updatePageContent(currentPageNumber + 1);
  }

  fetchPreviousPageOfComments(currentPagenumber: number) {
    if (currentPagenumber <= 0) return;
    this.updatePageContent(currentPagenumber - 1);
  }
}
