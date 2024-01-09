import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './home/home-page.component';
import { LoginPageComponent } from './login/login-page.component';
import { NavbarComponent } from './navbar/navbar.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegistrationPageComponent } from './registration/registration-page.component';
import { VerificationPageComponent } from './verification/verification-page.component';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  HttpClientModule,
} from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpCookieInterceptor } from './Interceptors/HttpCookieInterceptor';
import { HttpCsrfTokenInterceptor } from './Interceptors/HttpCsrfTokenInterceptor';
import { ForgottenPasswordPageComponent } from './forgotten-password/forgotten-password-page.component';
import { ImageUploaderPageComponent } from './image-uploader/image-uploader-page.component';
import { ImageDetailPageComponent } from './image-detail/image-detail-page.component';
import { AuthService } from './services/auth/auth.service';
import { CsrfTokenService } from './services/csrf-token/csrf-token.service';
import { PasswordResetPageComponent } from './password-reset/password-reset-page.component';
import { ErrorAlertComponent } from './shared/validation-alert/error-alert.component';
import { SpinnerComponent } from './shared/spinner/spinner.component';

export function initApp(
  authService: AuthService,
  CsrfTokenService: CsrfTokenService
) {
  return (): Promise<void> => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (
          localStorage.getItem('theme') === 'dark' ||
          window.matchMedia('(prefers-color-scheme: dark)').matches
        ) {
          localStorage.setItem('theme', 'dark');
          document.documentElement.classList.add('dark');
        } else {
          localStorage.setItem('theme', 'light');
          document.documentElement.classList.remove('dark');
        }

        CsrfTokenService.updateCsrfToken();
        authService.updateAuthentication();
        resolve();
      });
    });
  };
}

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    LoginPageComponent,
    NavbarComponent,
    PageNotFoundComponent,
    ForgottenPasswordPageComponent,
    RegistrationPageComponent,
    VerificationPageComponent,
    ImageUploaderPageComponent,
    ImageDetailPageComponent,
    PasswordResetPageComponent,
    ErrorAlertComponent,
    SpinnerComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpCookieInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpCsrfTokenInterceptor,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initApp,
      multi: true,
      deps: [AuthService, CsrfTokenService, HttpClient],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
