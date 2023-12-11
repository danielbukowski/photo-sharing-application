import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { ImageService } from '../services/image/image.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-image-page',
  templateUrl: './add-image-page.component.html'
})
export class AddImagePageComponent implements OnInit {
  isBeingProcessed: WritableSignal<boolean> = signal(false);
  image!: File;
  addImageForm!: FormGroup;

  constructor(
    private imageService: ImageService,
    private router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.addImageForm = this.fb.group({
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
      .uploadImage(this.addImageForm.value, this.image)
      .subscribe({
        next: () => {
          this.router.navigate(['/home']);
        },
        error: () => {
          this.isBeingProcessed.set(false);
        },
      });
  }
}
