import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { InfoComponent } from './info/info.component';
import { SettingsComponent } from './settings/settings.component';

export enum AppPath {
  Info = '',
  Settings = 'settings'
}

const routes: Routes = [
  { path: AppPath.Info, component: InfoComponent },
  { path: AppPath.Settings, component: SettingsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
