import { Component, TrackByFunction } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from 'src/app/models/page';
import { ImageService } from 'src/app/services/image/image.service';

@Component({
  selector: 'app-home-image-gallery',
  templateUrl: './home-image-gallery.component.html',
})
export class HomeImageGalleryComponent {
  imagePage$!: Observable<Page<string>>;

  constructor(private imageService: ImageService) {}

  ngOnInit(): void {
    this.imagePage$ = this.imageService.getPageOfLatestImages(0);
  }

  trackByImageId: TrackByFunction<string> = (index, imageId: string) => imageId;

  updatePageContent(pageNumber: number) {
    this.imagePage$ = this.imageService.getPageOfLatestImages(pageNumber);
  }
}
