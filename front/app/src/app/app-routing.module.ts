import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InfoComponent } from './info/info.component';
import { SettingsComponent } from './settings/settings.component';
import { ListenComponent } from './audio/listen/listen.component';
import { AppOkGuard } from './guard/app-ok.guard';
import { StartingComponent } from './starting/starting.component';

export enum AppPath {
  Info = '',
  Settings = 'settings',
  Listen = 'listen',
  Starting = 'starting',
  NotRecording = 'not-recording'
}

const routes: Routes = [
  { path: AppPath.Info, component: InfoComponent, canActivate: [AppOkGuard] },
  { path: AppPath.Settings, component: SettingsComponent, canActivate: [AppOkGuard] },
  { path: AppPath.Listen, component: ListenComponent, canActivate: [AppOkGuard] },
  { path: AppPath.Starting, component: StartingComponent },
  { path: AppPath.NotRecording, component: StartingComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
