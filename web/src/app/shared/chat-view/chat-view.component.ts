
import { Component, OnInit } from '@angular/core';
import { PubNubAngular } from 'pubnub-angular2';
import { PubNumbMessage } from 'src/app/core/models/pubnub-message.model';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms'; //imports
import { Channel, ChannelCommand, ChannelCommandType, ChannelType } from 'src/app/core/models/channel.model';
import { ChannelsService } from 'src/app/core/services/channels.service';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { APIService } from 'src/app/core/services/api.service';


@Component({
  selector: 'app-chat-view',
  templateUrl: './chat-view.component.html',
  styleUrls: ['./chat-view.component.scss']
})



export class ChatViewComponent implements OnInit {
  channelIdentifer: string;
  userName: string = "Matan";
  chatForm: FormGroup;
  speakForm: FormGroup;

  currentChannel: Channel;
  userMessage: string;

  voices = ["Matthew", "Joanna", "Ivy", "Justin"];
  selectedVoice = "Mattew";
textSpeak: string;
isAllowed: boolean = false;

  constructor(public channelsService: ChannelsService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private formBuilder: FormBuilder,
              private api: APIService) {

  }

 ngOnInit(): void {


this.activatedRoute.params.subscribe(x => {
  if(!x.hasOwnProperty('channel_identifier'))
    this.router.navigate(['home']);

  this.channelIdentifer = x['channel_identifier'];

  // if(!this.channelsService.isWaitingChannelExists(this.channelIdentifer) && !this.isAllowed)
  //   this.router.navigate(['home']);


    
  this.channelsService.removeWaitingChannel(this.channelIdentifer);
  this.channelsService.addListeningChannel({type: [ChannelType.User], id:  this.channelIdentifer, identifier: this.channelIdentifer,annoymus: false,name: this.channelIdentifer});
  
  //let command: ChannelCommand = {id: ''};
  //this.channelsService.createChannel({type: [ChannelType.User], id:  this.channelIdentifer, identifier: this.channelIdentifer,annoymus: false,name: this.channelIdentifer});
});

    this.chatForm = this.formBuilder.group({
      userMessage:[this.userMessage, [Validators.required]]
    });

    this.speakForm = this.formBuilder.group({
      selectVoices: [this.selectedVoice],
      textToSpeak: [this.textSpeak]
    });

    this.channelsService.getChannels().subscribe(x => {
      console.log(x);
      let index = x.findIndex(c => c.id == this.channelIdentifer);
      if(index >= 0){
        this.currentChannel = x[index];


      }
      else
      {
        // no longer belongs to this channel -> navigate to home
        console.log("unsibscribe!");
        this.router.navigate(['home']);
      }

    });

    this.channelsService.markAsReadMessages(this.channelIdentifer);

    if(!this.isAllowed)
      {
        let cannelCommand : ChannelCommand = {
          channelIdentifier: this.channelIdentifer,
          commandType: ChannelCommandType.OPEN_CHANNEL,
          typeCommand: "logIn",
          sender: this.channelIdentifer,
          senderIdentifier: this.channelIdentifer,
          text: "",
          id: this.channelIdentifer,
          date: new Date()
        }
       // send message to all potentional listeneers
        this.channelsService.sendCommand(cannelCommand);
      }
      this.isAllowed = true;

  }

 async  onSend(): Promise<void>{
    if(this.chatForm.valid)
    {

      var message = this.chatForm.value;

      var channelCommand: ChannelCommand = {
        channelIdentifier: this.channelIdentifer,
        sender: this.userName,
        senderIdentifier: this.channelsService.userID,
        commandType: ChannelCommandType.MESSAGE,
        typeCommand: 'message',
         text: message.userMessage,
         id: this.channelsService.userID,
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
      senderIdentifier: this.channelsService.userID,
      commandType: ChannelCommandType.CLOSE_CHANNEL,
      typeCommand: 'getOut',
       text: "",
       id: this.channelIdentifer,
       date: new Date()
    };
    await this.channelsService.sendCommand(channelCommand);
    this.router.navigate(['home']);
  }


  playAudio(url){
    let audio = new Audio();
    audio.src = url;
    audio.load();
    audio.play();
  }
  speakNow(){
    let data = {
      text: this.textSpeak,
      voice: this.selectedVoice
    }
    this.api.speak(data).subscribe((result:any) => {
      this.playAudio(result.url);
    });
  }


}
