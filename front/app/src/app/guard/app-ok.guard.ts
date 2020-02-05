import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { StreamService } from 'src/generated-sources/rest/services';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AppOkGuard implements CanActivate {

  constructor(private streamService: StreamService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    return this.streamService.infoUsingGET().pipe(map(info => {
      if(!info.recordingSupported) {
        this.router.navigate(['not-recording']);
        return false;
      } else if(!info.warmupComplete) {
        this.router.navigate(['starting']);
        return false;
      }
      return true;
    }));
    return false;
  }
  
}
