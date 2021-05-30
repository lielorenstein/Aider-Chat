import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginGuard } from './core/guards/login.guard';
import { ChannelsListComponent } from './shared/channels-list/channels-list.component';
import { ChatViewComponent } from './shared/chat-view/chat-view.component';
import { HomeComponent } from './shared/home/home.component';
import { LoginComponent } from './shared/login/login.component';

const routes: Routes = [

  {path:'channel/:channel_identifier', component: ChatViewComponent, canActivate: [LoginGuard]},
  {path:'channels', component: ChannelsListComponent, canActivate: [LoginGuard]},
  {path:'home', component: HomeComponent, canActivate: [LoginGuard]},
  {path: 'login', pathMatch: 'full', component: LoginComponent},
  {path: '**', redirectTo: 'home'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
