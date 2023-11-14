import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ImageDetailsPostRequest } from '../model/image-details-post-request';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  constructor(private http: HttpClient) {}

  getLatestImagesInFormOfIdList(): Observable<any> {
    return this.http.get('http://localhost:8081/api/v1/images');
  }

  uploadImage(imagePostRequest: ImageDetailsPostRequest, image: File): Observable<any> {
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
}
