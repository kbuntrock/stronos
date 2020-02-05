import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from './shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ApiModule } from './../generated-sources/rest/api.module';
import { environment } from 'src/environments/environment';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { SettingsComponent } from './settings/settings.component';
import { InfoComponent } from './info/info.component';
import { LoaderComponent } from './loader/loader.component';
import { ListenComponent } from './audio/listen/listen.component';
import { StartingComponent } from './starting/starting.component';

@NgModule({
  declarations: [AppComponent, SettingsComponent, InfoComponent, LoaderComponent, ListenComponent, StartingComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    DragDropModule,
    FontAwesomeModule,
    ApiModule.forRoot({ rootUrl: environment.backendUrl })
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
