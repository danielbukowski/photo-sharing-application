import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageUploaderPageComponent } from './image-uploader-page.component';

describe('ImageUploaderPageComponent', () => {
  let component: ImageUploaderPageComponent;
  let fixture: ComponentFixture<ImageUploaderPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImageUploaderPageComponent]
    });
    fixture = TestBed.createComponent(ImageUploaderPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
