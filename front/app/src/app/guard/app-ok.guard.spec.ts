import { TestBed, async, inject } from '@angular/core/testing';

import { AppOkGuard } from './app-ok.guard';

describe('AppOkGuard', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AppOkGuard]
    });
  });

  it('should ...', inject([AppOkGuard], (guard: AppOkGuard) => {
    expect(guard).toBeTruthy();
  }));
});
