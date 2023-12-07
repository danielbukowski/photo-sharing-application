import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './home/home-page.component';
import { LoginComponent } from './login/login.component';
import { NavbarComponent } from './navbar/navbar.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { RegistrationComponent } from './registration/registration.component';
import { VerificationComponent } from './verification/verification.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpCookieInterceptor } from './Interceptor/HttpCookieInterceptor';
import { HttpCsrfTokenInterceptor } from './Interceptor/HttpCsrfTokenInterceptor';
import { ForgottenPasswordPageComponent } from './forgotten-password/forgotten-password-page.component';
import { AddImagePageComponent } from './add-image/add-image-page.component';
import { ImageDetailsComponent } from './image-details/image-details.component';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    LoginComponent,
    NavbarComponent,
    PageNotFoundComponent,
    ForgottenPasswordPageComponent,
    RegistrationComponent,
    VerificationComponent,
    AddImagePageComponent,
    ImageDetailsComponent
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule, ReactiveFormsModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpCookieInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpCsrfTokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
