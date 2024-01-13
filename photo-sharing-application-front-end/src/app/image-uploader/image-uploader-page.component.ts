import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { ImageService } from '../services/image/image.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-image-uploader-page',
  templateUrl: './image-uploader-page.component.html',
})
export class ImageUploaderPageComponent implements OnInit {
  isBeingProcessed: WritableSignal<boolean> = signal(false);
  image!: File;
  ImageUploaderForm!: FormGroup;
  errorMessage: WritableSignal<string> = signal('');

  constructor(
    private imageService: ImageService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.ImageUploaderForm = this.fb.group({
      title: ['', Validators.required],
      isPrivate: [false, Validators.required],
      image: [this.image, Validators.required],
    });
  }

  getImageFromInput(event: any) {
    this.image = event.target.files ? event.target.files[0] : '';
  }

  onSubmit() {
    this.isBeingProcessed.set(true);
    this.imageService
      .uploadImage(this.ImageUploaderForm.value, this.image)
      .subscribe({
        next: () => {
          this.router.navigate(['/home']);
        },
        error: (e) => {
          this.errorMessage.set(e.error.reason || 'Internal Server Error');
          this.isBeingProcessed.set(false);
        },
      });
  }
}
