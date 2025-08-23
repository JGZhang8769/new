import { TestBed } from '@angular/core/testing';

import { ResumeData } from './resume-data';

describe('ResumeData', () => {
  let service: ResumeData;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResumeData);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
