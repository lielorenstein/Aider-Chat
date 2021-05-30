import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Channel, ChannelType } from 'src/app/core/models/channel.model';
import { ChannelsService } from 'src/app/core/services/channels.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(private router: Router, private channelsService: ChannelsService,
    private _snackBar: MatSnackBar) { }

  ngOnInit(): void {

   // this.openSnackBar();
  }

  showChannels():void{
    //console.log("asfasf");
    this.router.navigate(['channels']);
  }

  createChannel():void{
    
    let channel: Channel = {type: [ChannelType.User], id:  this.channelsService.userID, identifier: this.channelsService.userID,annoymus: false,name: this.channelsService.userID};
    //this.channelsService.addListeningChannel(channel);
    this.channelsService.createChannel(channel);
    this.router.navigate(['channel', this.channelsService.userID]);
  
  }

  openSnackBar() {
    this._snackBar.open('Cannonball!!', 'Splash', {
      horizontalPosition: 'start',
      verticalPosition: 'bottom',
    });
  }

}
