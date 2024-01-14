import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageDetailCommentComponent } from './image-detail-comment.component';

describe('ImageDetailCommentComponent', () => {
  let component: ImageDetailCommentComponent;
  let fixture: ComponentFixture<ImageDetailCommentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImageDetailCommentComponent]
    });
    fixture = TestBed.createComponent(ImageDetailCommentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
