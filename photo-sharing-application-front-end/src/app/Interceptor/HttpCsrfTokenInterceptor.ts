import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { CsrfTokenService } from '../service/csrf-token/csrf-token.service';
import { Injectable, Signal } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable()
export class HttpCsrfTokenInterceptor implements HttpInterceptor {

  constructor(private csrfTokenService: CsrfTokenService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const respHeaderName: string = 'X-XSRF-TOKEN';
    let csrfToken: Signal<string> = this.csrfTokenService.getCsrfToken();

    if (!(req.method === 'GET' || req.method === 'HEAD') && csrfToken) {
      req = req.clone({
        headers: req.headers.set(respHeaderName, csrfToken()),
      });
    }
    return next.handle(req);
  }
}
