import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class HttpCookieInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<string>, next: HttpHandler): Observable<HttpEvent<string>> {
    req = req.clone({
      withCredentials: true,
    });
    return next.handle(req);
  }

}
