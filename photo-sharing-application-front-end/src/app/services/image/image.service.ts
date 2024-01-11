import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ImageUploaderRequest } from '../../models/image-uploader-request';
import { environment } from 'src/environments/environment';
import { Page } from 'src/app/models/page';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  constructor(private http: HttpClient) {}

  getPageOfLatestImages(pageNumber: number): Observable<Page<string>> {
    return this.http.get<Page<string>>(`${environment.apiUrl}/api/v1/images`, {
      params: { pageNumber },
    });
  }

  uploadImage(
    imageUploaderRequest: ImageUploaderRequest,
    image: File
  ): Observable<unknown> {
    let formData = new FormData();

    formData.append('image', image);
    formData.append(
      'imageProperties',
      new Blob([JSON.stringify(imageUploaderRequest)], {
        type: 'application/json',
      })
    );

    return this.http.post(
      `${environment.apiUrl}/api/v3/accounts/images`,
      formData
    );
  }

  getNumberOfLikesFromImage(imageId: string): Observable<unknown> {
    return this.http.get(
      `${environment.apiUrl}/api/v1/images/${imageId}/likes`
    );
  }
}
