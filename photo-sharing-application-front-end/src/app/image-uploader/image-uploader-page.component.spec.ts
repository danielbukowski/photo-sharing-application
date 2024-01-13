import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageUploaderPageComponent } from './image-uploader-page.component';
import { HttpClientModule } from '@angular/common/http';
import { NavbarComponent } from '../navbar/navbar.component';
import { ReactiveFormsModule } from '@angular/forms';

describe('ImageUploaderPageComponent', () => {
  let component: ImageUploaderPageComponent;
  let fixture: ComponentFixture<ImageUploaderPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImageUploaderPageComponent, NavbarComponent],
      imports: [HttpClientModule, ReactiveFormsModule]
    });
    fixture = TestBed.createComponent(ImageUploaderPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
