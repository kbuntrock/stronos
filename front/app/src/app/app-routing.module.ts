import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  /*{ path: '', component: JoinGameMenuComponent, canActivate: [NotInGameGuard] },
  {
    path: 'card',
    component: CardDisplayComponent
  },
  {
    path: 'players',
    component: PlayerOrganisationComponent,
    canActivate: [NotInGameGuard]
  },
  {
    path: 'create',
    component: CreateGameComponent,
    canActivate: [NotInGameGuard]
  },
  { path: 'join', component: JoinGameComponent, canActivate: [NotInGameGuard] },
  { path: 'game', component: GameComponent, canActivate: [InGameGuard] }
  */
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
