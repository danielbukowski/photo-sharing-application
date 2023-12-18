import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { Observable, filter, map } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

export const emailVerificationGuard: CanActivateFn = (): boolean => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const accountDetails = authService.getAccountDetails();

  if (accountDetails()?.isEmailVerified) {
    return true;
  }
  router.navigate(['home']);
  return false;
};
