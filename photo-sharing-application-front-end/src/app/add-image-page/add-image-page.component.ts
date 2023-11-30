import { Component, OnDestroy, OnInit } from '@angular/core';
import { ImageService } from '../image/image.service';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-image-page',
  templateUrl: './add-image-page.component.html',
  styleUrls: ['./add-image-page.component.css'],
})
export class AddImagePageComponent implements OnDestroy, OnInit {
  isBeingProcessed$: BehaviorSubject<boolean> = new BehaviorSubject(false);
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
  
  ngOnDestroy(): void {
    this.isBeingProcessed$.unsubscribe();
  }

  getImageFromInput(event: any) {
    this.image = event.target.files ? event.target.files[0] : '';
  }

  onSubmit() {
    this.isBeingProcessed$.next(true);
    this.imageService
      .uploadImage(this.addImageForm.value, this.image)
      .subscribe({
        next: (n) => {
          this.router.navigate(['/home']);
        },
        error: (e) => {
          this.isBeingProcessed$.next(false);
        },
      });
  }
}
