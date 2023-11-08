import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { CsrfTokenService } from '../csrf-token/csrf-token.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class HttpCsrfTokenInterceptor implements HttpInterceptor {

  constructor(private csrfTokenService: CsrfTokenService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const respHeaderName = 'X-XSRF-TOKEN';
    let token = this.csrfTokenService.getCsrfToken;

    if (!(req.method === "GET" || req.method === "HEAD") && token) {
      req = req.clone({ headers: req.headers.set(respHeaderName, token) });
    }
    return next.handle(req);
  }

}
