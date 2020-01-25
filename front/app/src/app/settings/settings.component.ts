import { Component, OnInit } from '@angular/core';
import { StreamService } from 'src/generated-sources/rest/services';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  // true if the component has loaded
  loaded = false;

  volume = 1;

  constructor(private streamService: StreamService) {}

  ngOnInit() {
    this.streamService.getVolumeUsingGET().subscribe(volume => {
      this.volume = volume;
      this.loaded = true;
    });
  }

  updateVolume() {
    console.info('update volume : ' + this.volume);
    this.streamService.setVolumeUsingPOST(this.volume).subscribe(volume => {
      this.volume = volume;
    });
  }
}
