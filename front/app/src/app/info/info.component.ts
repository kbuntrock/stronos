import { Component, OnInit } from '@angular/core';
import { StreamService } from 'src/generated-sources/rest/services';
import { ServerInfoDto } from '../../generated-sources/rest/models/server-info-dto';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.scss']
})
export class InfoComponent implements OnInit {
  displayedColumns: string[] = ['ipAdress', 'userAgent', 'streamedSeconds'];
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
    this.awakeSince = this.formatSeconds(this.infoDto.awakeSince);
  }

  public formatSeconds(seconds: number): string {
    console.info('In seconds : ' + seconds);
    const d = Math.floor(seconds / 3600 / 24);
    const h = Math.floor((seconds - (d * 3600 * 24))/ 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = Math.floor((seconds % 3600) % 60);

    let formatted = '';
    if (d > 0) {
      formatted += d + 'd, ';
    }
    if (d > 0 || h > 0) {
      formatted += h + 'h, ';
    }
    if (d > 0 || h > 0 || m > 0) {
      formatted += m + 'm ';
    }
    if (d > 0 || h > 0 || m > 0) {
      formatted += 'and ' + s + 's';
    } else if (s > 0) {
      formatted += s + 's';
    }
    return formatted;
  }

  public formatUserAgent(userAgent: string): string {
    if (userAgent.search('Winamp') > 0) {
      return 'Sonos';
    }
    if (userAgent.length > 20) {
      let result = userAgent.substring(0, 20);
      result += '...';
      return result;
    }
    return userAgent;
  }
}
