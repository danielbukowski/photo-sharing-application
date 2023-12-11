import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../models/page';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  constructor(private http: HttpClient) {}

  addCommentToImage(commentContent: string, imageId: string): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}/api/v1/images/${imageId}/comments`,
      { content: commentContent }
    );
  }

  getCommentsFromImage(
    imageId: string,
    pageNumber: number
  ): Observable<Page<any>> {
    return this.http.get<Page<Comment>>(
      `${environment.apiUrl}/api/v1/images/${imageId}/comments`,
      { params: { pageNumber } }
    );
  }
}
