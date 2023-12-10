import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../models/page';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  constructor(private http: HttpClient) {}

  addCommentToImage(commentContent: string, imageId: string): Observable<any> {
    return this.http.post(
      `http://localhost:8081/api/v1/images/${imageId}/comments`,
      { content: commentContent }
    );
  }

  getCommentsFromImage(
    imageId: string,
    pageNumber: number
  ): Observable<Page<any>> {
    return this.http.get<Page<Comment>>(
      `http://localhost:8081/api/v1/images/${imageId}/comments`,
      { params: { pageNumber } }
    );
  }
}
