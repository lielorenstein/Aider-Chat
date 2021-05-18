import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginGuard } from './core/guards/login.guard';
import { ChatViewComponent } from './shared/chat-view/chat-view.component';
import { HomeComponent } from './shared/home/home.component';
import { LoginComponent } from './shared/login/login.component';

const routes: Routes = [

  {path:'channel/:channel_identifier', component: ChatViewComponent, canActivate: [LoginGuard]},
  {path:'home', component: HomeComponent, canActivate: [LoginGuard]},
  {path: 'login', pathMatch: 'full', component: LoginComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
