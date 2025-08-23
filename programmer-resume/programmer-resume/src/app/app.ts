import { Component, OnInit } from '@angular/core';
import { Resume, ResumeData } from './services/resume-data';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  resumeData$: Observable<Resume>;

  constructor(private resumeDataService: ResumeData) {}

  ngOnInit() {
    this.resumeData$ = this.resumeDataService.getResumeData();
  }
}
