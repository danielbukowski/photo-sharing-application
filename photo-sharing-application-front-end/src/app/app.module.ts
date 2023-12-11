import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './home/home-page.component';
import { LoginPageComponent } from './login/login-page.component';
import { NavbarComponent } from './navbar/navbar.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegistrationPageComponent } from './registration/registration-page.component';
import { VerificationComponent } from './verification/verification.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpCookieInterceptor } from './Interceptors/HttpCookieInterceptor';
import { HttpCsrfTokenInterceptor } from './Interceptors/HttpCsrfTokenInterceptor';
import { ForgottenPasswordPageComponent } from './forgotten-password/forgotten-password-page.component';
import { AddImagePageComponent } from './add-image/add-image-page.component';
import { ImageDetailComponent } from './image-detail/image-detail-page.component';

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
    AddImagePageComponent,
    ImageDetailComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
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
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
