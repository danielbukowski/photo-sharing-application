import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from '../../models/page';
import { environment } from 'src/environments/environment';
import { Comment } from 'src/app/models/comment';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  constructor(private http: HttpClient) {}

  addCommentToImage(
    commentContent: string,
    imageId: string
  ): Observable<unknown> {
    return this.http.post(
      `${environment.apiUrl}/api/v1/images/${imageId}/comments`,
      { content: commentContent }
    );
  }

  getCommentsFromImage(
    imageId: string,
    pageNumber: number
  ): Observable<Page<Comment>> {
    return this.http.get<Page<Comment>>(
      `${environment.apiUrl}/api/v1/images/${imageId}/comments`,
      { params: { pageNumber } }
    );
  }
}
