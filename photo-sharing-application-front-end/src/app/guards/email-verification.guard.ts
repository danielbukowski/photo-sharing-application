import { Signal, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { Account } from '../models/account';

export const emailVerificationGuard: CanActivateFn = (): boolean => {
  const router = inject(Router);
  const authService = inject(AuthService);
  const accountDetails: Signal<Account | undefined> = authService.getAccountDetails();

  if (accountDetails()?.isEmailVerified) {
    return true;
  }
  router.navigateByUrl('/home');
  return false;
};
