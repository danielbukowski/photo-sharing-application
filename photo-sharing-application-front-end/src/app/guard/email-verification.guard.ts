import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth/auth.service';
import { Observable, filter, map } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

export const emailVerificationGuard: CanActivateFn =
  (): Observable<boolean> => {
    const router = inject(Router);
    const authService = inject(AuthService);
    const accountDetails = toObservable(authService.getAccountDetails());

    return accountDetails.pipe(
      filter((a) => a !== undefined),
      map((a) => {
        if (!a?.isEmailVerified) {
          router.navigate(['home']);
          return false;
        }
        return true;
      })
    );
  };
