import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';
import { faCogs, faInfoCircle } from '@fortawesome/free-solid-svg-icons';
import { AppRoutingModule, AppPath } from './app-routing.module';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  faCogs = faCogs;
  faInfoCircle = faInfoCircle;
  title = 'Stronos';

  constructor(private router: Router) {}

  ngOnInit(): void {}

  public navigateToSettings() {
    this.router.navigate([AppPath.Settings]);
  }

  public navigateToInfos() {
    this.router.navigate([AppPath.Info]);
  }
}
