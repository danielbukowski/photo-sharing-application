import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { LoginComponent } from './login/login.component';
import { ForgottenPasswordPageComponent } from './forgotten-password/forgotten-password-page.component';
import { RegistrationComponent } from './registration/registration.component';
import { VerificationComponent } from './verification/verification.component';
import { AddImagePageComponent } from './add-image/add-image-page.component';
import { emailVerificationGuard } from './guard/guards';
import { ImageDetailsComponent } from './image-details/image-details.component';

const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegistrationComponent,
  },
  {
    path: 'forget-password',
    component: ForgottenPasswordPageComponent,
  },
  {
    path: 'verify',
    component: VerificationComponent,
  },
  {
    path: 'add-image',
    component: AddImagePageComponent,
    canActivate: [emailVerificationGuard]
  },
  {
    path: 'image/:id',
    component: ImageDetailsComponent,
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
  imports: [RouterModule.forRoot(routes, {scrollPositionRestoration: 'enabled'})],
  exports: [RouterModule],
})
export class AppRoutingModule {}
