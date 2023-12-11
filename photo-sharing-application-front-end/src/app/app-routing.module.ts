import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomePageComponent } from './home/home-page.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { LoginPageComponent } from './login/login-page.component';
import { ForgottenPasswordPageComponent } from './forgotten-password/forgotten-password-page.component';
import { RegistrationPageComponent } from './registration/registration-page.component';
import { VerificationPageComponent } from './verification/verification-page.component';
import { AddImagePageComponent } from './add-image/add-image-page.component';
import { emailVerificationGuard } from './guards/email-verification.guard';
import { ImageDetailPageComponent } from './image-detail/image-detail-page.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomePageComponent,
  },
  {
    path: 'login',
    component: LoginPageComponent,
  },
  {
    path: 'register',
    component: RegistrationPageComponent,
  },
  {
    path: 'forget-password',
    component: ForgottenPasswordPageComponent,
  },
  {
    path: 'verify',
    component: VerificationPageComponent,
  },
  {
    path: 'add-image',
    component: AddImagePageComponent,
    canActivate: [emailVerificationGuard],
  },
  {
    path: 'image/:id',
    component: ImageDetailPageComponent
    ,
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full',
  },
  {
    path: '**',
    component: PageNotFoundComponent,
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
