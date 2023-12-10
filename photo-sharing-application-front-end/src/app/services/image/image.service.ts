import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ImageDetailsPostRequest } from '../../models/image-details-post-request';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  constructor(private http: HttpClient) {}

  getPageOfLatestImages(pageNumber: number): Observable<any> {
    return this.http.get('http://localhost:8081/api/v1/images', {
      params: { pageNumber },
    });
  }

  uploadImage(
    imagePostRequest: ImageDetailsPostRequest,
    image: File
  ): Observable<any> {
    let formData = new FormData();

    formData.append('image', image);
    formData.append(
      'imageProperties',
      new Blob([JSON.stringify(imagePostRequest)], {
        type: 'application/json',
      })
    );

    return this.http.post(
      'http://localhost:8081/api/v3/accounts/images',
      formData
    );
  }

  getNumberOfLikesFromImage(imageId: string): Observable<any> {
    return this.http.get(
      `http://localhost:8081/api/v1/images/${imageId}/likes`
    );
  }
}