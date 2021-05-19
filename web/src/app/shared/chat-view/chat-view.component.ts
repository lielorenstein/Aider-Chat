
import { Component, OnInit } from '@angular/core';
import { PubNubAngular } from 'pubnub-angular2';
import { PubNumbMessage } from 'src/app/core/models/pubnub-message.model';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms'; //imports
import { Channel, ChannelCommand, ChannelCommandType, ChannelType } from 'src/app/core/models/channel.model';
import { ChannelsService } from 'src/app/core/services/channels.service';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';


@Component({
  selector: 'app-chat-view',
  templateUrl: './chat-view.component.html',
  styleUrls: ['./chat-view.component.scss']
})



export class ChatViewComponent implements OnInit {
  channelIdentifer: string;
  userName: string = "Matan";
  chatForm: FormGroup;

  currentChannel: Channel;
  userMessage: string;

  constructor(private channelsService: ChannelsService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder) {

  }

 ngOnInit(): void {


this.activatedRoute.params.subscribe(x => {
  if(!x.hasOwnProperty('channel_identifier'))
    this.router.navigate(['home']);
  this.channelIdentifer = x['channel_identifier'];
  this.channelsService.addListeningChannel({type: [ChannelType.User], identifier: this.channelIdentifer,annoymus: false,name: this.channelIdentifer});
});

    this.chatForm = this.formBuilder.group({
      userMessage:[this.userMessage, [Validators.required]]
    });


    this.channelsService.getChannels().subscribe(x => {
      let index = x.findIndex(c => c.identifier == this.channelIdentifer);
      if(index >= 0){
        this.currentChannel = x[index];


      }
      else
      {
        // no longer belongs to this channel -> navigate to home
        this.router.navigate(['home']);
        console.log("unsibscribe!");
      }
    });

    this.channelsService.markAsReadMessages(this.channelIdentifer);



  }

 async  onSend(): Promise<void>{
    if(this.chatForm.valid)
    {

      var message = this.chatForm.value;

      var channelCommand: ChannelCommand = {
        channelIdentifier:this.channelIdentifer,
        sender: this.userName,
        senderIdentifier: 'drysdgsdgsd',
        commandType: ChannelCommandType.MESSAGE,
         text: message.userMessage,
         id: '1',
         date: new Date()
      };

      await this.channelsService.sendCommand(channelCommand);
      //mark read messages
      this.channelsService.markAsReadMessages(this.channelIdentifer);
    }
  }

  async onClose(): Promise<void> {
    var channelCommand: ChannelCommand = {
      channelIdentifier:this.channelIdentifer,
      sender: this.userName,
      senderIdentifier: 'asfaw5aasfs',
      commandType: ChannelCommandType.CLOSE_CHANNEL,
       text: "",
       id: '1',
       date: new Date()
    };
    await this.channelsService.sendCommand(channelCommand);
    this.router.navigate(['home']);
  }


}
