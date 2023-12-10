import { TestBed } from '@angular/core/testing';

import { ForgottenPasswordService } from './forgotten-password.service';

describe('ForgottenPasswordService', () => {
  let service: ForgottenPasswordService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ForgottenPasswordService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
