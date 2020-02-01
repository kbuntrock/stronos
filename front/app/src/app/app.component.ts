import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';
import {
  faCogs,
  faInfoCircle,
  faHeadphones
} from '@fortawesome/free-solid-svg-icons';
import { AppRoutingModule, AppPath } from './app-routing.module';
import { ListenComponent } from './audio/listen/listen.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  faCogs = faCogs;
  faInfoCircle = faInfoCircle;
  faHeadphones = faHeadphones;
  title = 'Stronos';

  constructor(private router: Router) {}

  ngOnInit(): void {}

  public navigateToSettings() {
    this.router.navigate([AppPath.Settings]);
  }

  public navigateToInfos() {
    this.router.navigate([AppPath.Info]);
  }

  public navigateToListen() {
    this.router.navigate([AppPath.Listen]);
  }
}
