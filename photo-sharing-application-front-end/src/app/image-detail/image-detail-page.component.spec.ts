import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageDetailPageComponent } from './image-detail-page.component';

describe('ImageDetailPageComponent', () => {
  let component: ImageDetailPageComponent;
  let fixture: ComponentFixture<ImageDetailPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImageDetailPageComponent]
    });
    fixture = TestBed.createComponent(ImageDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
