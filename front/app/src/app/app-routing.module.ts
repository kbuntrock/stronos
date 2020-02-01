import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InfoComponent } from './info/info.component';
import { SettingsComponent } from './settings/settings.component';
import { ListenComponent } from './audio/listen/listen.component';

export enum AppPath {
  Info = '',
  Settings = 'settings',
  Listen = 'listen'
}

const routes: Routes = [
  { path: AppPath.Info, component: InfoComponent },
  { path: AppPath.Settings, component: SettingsComponent },
  { path: AppPath.Listen, component: ListenComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
