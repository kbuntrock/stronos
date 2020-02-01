import { Component, OnInit } from '@angular/core';
import { StreamService } from 'src/generated-sources/rest/services';
import { CapturePeripheral } from 'src/generated-sources/rest/models';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  // true if the component has loaded
  loaded = false;
  requests = 0;

  peripherals: Array<CapturePeripheral>;
  selectedPeripheral: string;

  volume = 1;

  constructor(private streamService: StreamService) {}

  ngOnInit() {
    this.streamService.getVolumeUsingGET().subscribe(volume => {
      this.volume = volume;
      this.updateLoadedState();
    });
    this.streamService
      .getAvailableCaptureDevicesUsingGET()
      .subscribe(peripherals => {
        this.peripherals = peripherals;
        this.updateLoadedState();
      });
  }

  private updateLoadedState() {
    this.requests += 1;
    if (this.requests >= 2) {
      this.loaded = true;
    }
  }

  updateVolume() {
    console.info('update volume : ' + this.volume);
    this.streamService.setVolumeUsingPOST(this.volume).subscribe(volume => {
      this.volume = volume;
    });
  }

  selectCapturePeripheral() {
    this.streamService
      .setCaptureDeviceUsingPOST(this.selectedPeripheral)
      .subscribe(result => {
        this.streamService
          .getAvailableCaptureDevicesUsingGET()
          .subscribe(peripherals => {
            this.peripherals = peripherals;
          });
      });
  }
}
