import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Channel } from 'src/app/core/models/channel.model';
import { ChannelsService } from 'src/app/core/services/channels.service';

@Component({
  selector: 'app-channels-list',
  templateUrl: './channels-list.component.html',
  styleUrls: ['./channels-list.component.scss']
})
export class ChannelsListComponent implements OnInit {

  channels: Channel[];

  constructor(
    private channelsService: ChannelsService,
    private router: Router) { }

  ngOnInit(): void {

  this.channelsService.getChannels().subscribe(ch => {
      this.channels = ch;
  });


  }


openChannel(index: number): void{
  this.router.navigate(['channel', this.channels[index].identifier]);
}

}
