import { Component } from '@angular/core';
import { ImageService } from '../image/image.service';
import { Router } from '@angular/router';
import { ImageDetailsPostRequest } from '../model/image-details-post-request';


@Component({
  selector: 'app-add-image-page',
  templateUrl: './add-image-page.component.html',
  styleUrls: ['./add-image-page.component.css'],
})
export class AddImagePageComponent {
  isBeingProcessed: boolean = false;
  imagePostRequest: ImageDetailsPostRequest = {
    title: '',
    isPrivate: false,
  } as ImageDetailsPostRequest;
  image!: File;

  constructor(private imageService: ImageService, private router: Router) {}

  getImageFromInput(event: any) {
    this.image = event.target.files ? event.target.files[0] : '';
  }

  onSubmit() {
    this.isBeingProcessed = true;
    this.imageService.uploadImage(this.imagePostRequest, this.image).subscribe({
      next: (n) => {
        this.router.navigate(['/home']);
      },
      error: (e) => {
        this.isBeingProcessed = false;
      },
    });
  }
}
