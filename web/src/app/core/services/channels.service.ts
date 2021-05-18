import { Injectable, OnDestroy } from '@angular/core';
import { PubNubAngular } from 'pubnub-angular2';
import { BehaviorSubject, Observable } from 'rxjs';
import { Channel, ChannelCommand, ChannelCommandType, ChannelType } from '../models/channel.model';

@Injectable({
  providedIn: 'root'
})
export class ChannelsService implements OnDestroy{
  private publishkey = 'pub-c-6662ea57-6bf8-4f52-88f8-8cc4aa36f23a';
  private subscribekey = 'sub-c-5c8eb4aa-b1da-11eb-b48e-0ae489c2794e';
  private userChannels = new BehaviorSubject<Channel[]>([]);



  constructor(private pubnub: PubNubAngular) {

    var channels: Channel[] = [
      {name: 'Broadcast', identifier:'bc-all',annoymus: true, type: [ChannelType.Broadcast]}
    ]

    this.userChannels.next(channels);
    this.pubnubInit();


   }

   ngOnDestroy(): void{
    var channelsList = this.userChannels.value.map(c => c.identifier);
    this.pubnub.unsubscribe({
    channels: channelsList,//['backend-session'],
    withPresence: true,
    triggerEvents: ['message', 'presence', 'status']
  });
   }

   pubnubInit() {

    this.pubnub.init({
      publishKey: this.publishkey,
      subscribeKey: this.subscribekey,
      ssl: true,
      uuid: "client"
    });



      this.pubnub.addListener({
        status: function (st) {

            // this.pubnub.publish({
            //   message: {sender: "Matan",text: 'asdfasf',id: '200'},
            //   channel: 'backend-session'
            // });

        },
        message: (channelCommand) => {
          this.handleCommand(channelCommand.message);
          //this.push({id: 1, username: message.message.sender, message: message.message.text, date: null})
          //this.msg= message.message.content;
          //console.log(this.msg);
        }
      });


      var channelsList = this.userChannels.value.map(c => c.identifier);
      this.pubnub.subscribe({
      channels: channelsList,//['backend-session'],
      withPresence: true,
      triggerEvents: ['message', 'presence', 'status']
    });
  }


   addListeningChannel(channelDetails: Channel): boolean{
     var channels = this.userChannels.value;
     let index  = channels.findIndex(c => c.identifier == channelDetails.identifier);
     if(index < 0){
      channelDetails.unreadMessages = 0; // no new messages
      channels.push(channelDetails);
      this.userChannels.next(channels);
      this.pubnub.subscribe({
        channels: [channelDetails.identifier],//['backend-session'],
        withPresence: true,
        triggerEvents: ['message', 'presence', 'status']
      });
     }
     else{
       console.log("Already listen to the channel");
     }
     return true;
   }

   removeListeningChannel(channelIdentifier: string) : boolean{
    var channels = this.userChannels.value;
    let index = channels.findIndex(c => c.identifier == channelIdentifier);
    if(index >= 0)
    {
      this.pubnub.unsubscribe({
          channel : channelIdentifier
      });
      channels.splice(index, 1);
      this.userChannels.next(channels);
    }
    return true;
   }


   getChannels(): Observable<Channel[]>{
    //  let channels = this.userChannels.value;
    //  let index = channels.findIndex(ch => ch.identifier == channelIdentifier);
    //  if(index >= 0)
    //   return this.userChannels.value[index].messages;
    // return [];
    return this.userChannels.asObservable();
   }




   createChannel(channelDetails: Channel){
     // first perform api call to create channel in database

     // than add listening to channel
      this.addListeningChannel(channelDetails);

     // send message to all potentional listeneers

   }



  private async  publishCommand(channelCommand: ChannelCommand){
    await this.pubnub.publish({
      message: channelCommand,//{sender: this.userName,text: this.myText,id: '200'},
      channel: channelCommand.channelIdentifier
    }).catch(error => {  // catch the errors
      console.log(error);
    });;

    //this.myText = "";
   }


   private async closeChannel(channelCommand: ChannelCommand){
    //publish to all users of the channel to close their channels
    await this.publishCommand(channelCommand);

    //after waiting for publisihng -> unsubsribe to the channel
    this.removeListeningChannel(channelCommand.channelIdentifier);


   }


   sendCommand(channelCommand: ChannelCommand): void{
    switch(channelCommand.commandType){
      case ChannelCommandType.MESSAGE:
        this.publishCommand(channelCommand);
        break;
      case ChannelCommandType.CLOSE_CHANNEL:
        this.closeChannel(channelCommand);
        break;
    }
   }

   markAsReadMessages(channelIdentifier: string){
    var channels = this.userChannels.value;
    var index = channels.findIndex(ch => ch.identifier == channelIdentifier);
    if(index >= 0){
      channels[index].unreadMessages = 0;
      this.userChannels.next(channels);
    }
   }

   private addMessage(channelCommand: ChannelCommand)
   {
    var channels = this.userChannels.value;
    var index = channels.findIndex(ch => ch.identifier == channelCommand.channelIdentifier);
    if(index >= 0){

      if(!channels[index].messages)
        channels[index].messages = [];
      channels[index].messages.push(channelCommand);
      if(channels[index].unreadMessages == null)
        channels[index].unreadMessages = 0;
        channels[index].unreadMessages++;
      this.userChannels.next(channels);
    }
   }

   handleCommand(channelCommand: ChannelCommand): void{

    switch(channelCommand.commandType){
      case ChannelCommandType.MESSAGE:
        this.addMessage(channelCommand);
        break;
      case ChannelCommandType.CLOSE_CHANNEL:
        this.removeListeningChannel(channelCommand.channelIdentifier);
        break;
    }

//console.log(this.userChannels.value);

   }
}
