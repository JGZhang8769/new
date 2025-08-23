import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {
  @Input() name: string;
  @Input() title: string;
  @Input() avatar: string;
  @Input() linkedin: string;
  @Input() github: string;
}
