import { Component, OnInit } from '@angular/core';
import { StreamService } from 'src/generated-sources/rest/services';
import { ServerInfoDto } from '../../generated-sources/rest/models/server-info-dto';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent implements OnInit {
  // true if the component has loaded
  loaded = false;

  infoDto: ServerInfoDto;

  awakeSince: string;

  constructor(private streamService: StreamService) {}

  ngOnInit() {
    this.streamService.infoUsingGET().subscribe(info => {
      this.infoDto = info;
      this.setAwakeSince();
      this.loaded = true;
    });
  }

  private setAwakeSince() {
    const d = Math.floor(this.infoDto.awakeSince / 3600 / 24);
    const h = Math.floor(this.infoDto.awakeSince / 3600);
    const m = Math.floor((this.infoDto.awakeSince % 3600) / 60);
    const s = Math.floor((this.infoDto.awakeSince % 3600) % 60);

    this.awakeSince = '';
    if (d > 0) {
      this.awakeSince += d + ' days, ';
    }
    if (d > 0 || h > 0) {
      this.awakeSince += h + ' hours, ';
    }
    if (d > 0 || h > 0 || m > 0) {
      this.awakeSince += m + ' minutes ';
    }
    if (d > 0 || h > 0 || m > 0 || s > 0) {
      this.awakeSince += 'and ' + s + ' seconds';
    }
  }
}
