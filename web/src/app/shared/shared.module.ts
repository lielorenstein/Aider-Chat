import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatViewComponent } from './chat-view/chat-view.component';
import { LoginComponent } from './login/login.component';
import { MaterialModule } from '../core/modules/material/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { ChannelsListComponent } from './channels-list/channels-list.component';
import { HomeComponent } from './home/home.component';


@NgModule({
  declarations: [ChatViewComponent, LoginComponent, ChannelsListComponent, HomeComponent],
  imports: [
    CommonModule,
    MaterialModule,
    ReactiveFormsModule
    // MatFormFieldModule,
    // MatInputModule,
    // MatCardModule,
    // MatButtonModule,

  ],
  exports: [ChatViewComponent, LoginComponent, MaterialModule, ChannelsListComponent, HomeComponent]
})
export class SharedModule { }
