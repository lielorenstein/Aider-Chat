import { Injectable, OnDestroy } from '@angular/core';
import { PubNubAngular } from 'pubnub-angular2';
import { BehaviorSubject, Observable } from 'rxjs';
import { Channel, ChannelCommand, ChannelCommandType, ChannelType } from '../models/channel.model';
import { AngularFirestore } from '@angular/fire/firestore';
import { stringify } from '@angular/compiler/src/util';

@Injectable({
  providedIn: 'root'
})
export class ChannelsService implements OnDestroy{
  
  private publishkey = 'pub-c-6662ea57-6bf8-4f52-88f8-8cc4aa36f23a';
  private subscribekey = 'sub-c-5c8eb4aa-b1da-11eb-b48e-0ae489c2794e';
  private userChannels = new BehaviorSubject<Channel[]>([]);
  private waitingChannels = new BehaviorSubject<Channel[]>([]);
  //private waitingChannels = new BehaviorSubject<Channel[]>([]);
  userID: string = Math.random().toString(36).substr(2, 9);


  constructor(private pubnub: PubNubAngular, private AngularFire: AngularFirestore) {

    var channels: Channel[] = [
      {name: 'Broadcast', identifier:'BroadcastAll',id: 'BroadcastAll',annoymus: true, type: [ChannelType.Broadcast]}
    ]
    
    this.userChannels.next(channels);
    this.pubnubInit();

    //AngularFire.collection('items').valueChanges();
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
        message: (channelCommand) =>  {
          console.log(channelCommand);
          this.handleCommand(channelCommand.channel,channelCommand.message);
          //this.push({id: 1, username: message.message.sender, message: message.message.text, date: null})
          //this.msg= message.message.content;
          //console.log(this.msg);
        }
      });


      var channelsList = this.userChannels.value.map(c => c.id);
      //console.log(channelsList);
      this.pubnub.subscribe({
      channels: channelsList,//['backend-session'],
      withPresence: true,
      triggerEvents: ['message', 'presence', 'status']
    });
  }


   addListeningChannel(channelDetails: Channel): boolean{
     var channels = this.userChannels.value;
     //console.log("subscribe:");
     //console.log(channelDetails);
     let index  = channels.findIndex(c => c.id == channelDetails.id);
     if(index < 0){
      channelDetails.unreadMessages = 0; // no new messages
      console.log("SUBSCRIBING");
      console.log(channelDetails);
      this.pubnub.subscribe({
        channels: ['M-' + channelDetails.id + '-CHANNEL'],//['backend-session'],
        withPresence: true,
        triggerEvents: ['message', 'presence', 'status']
      });
      //channelDetails.id = channelDetails.identifier;
      channels.push(channelDetails);
      this.userChannels.next(channels);


     }
     else{
       console.log("Already listen to the channel");
     }
     return true;
   }

   removeListeningChannel(channelIdentifier: string) : boolean{
    var channels = this.userChannels.value;
    let index = channels.findIndex(c => c.id == channelIdentifier);
    if(index >= 0)
    {
      this.pubnub.unsubscribe({
          channel : 'M-' + channelIdentifier + '-CHANNEL'
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

   getWaitingChannels(): Observable<Channel[]>{
    return this.waitingChannels.asObservable();
   }

   getChannel(id: string) : Channel{
    let channels = this.userChannels.value;
    let index = channels.findIndex(ch => ch.id == id);
    return channels[index];
   }

   isChannelExists(id: string): boolean {
    let channels = this.userChannels.value;
     let index = channels.findIndex(ch => ch.id == id);
     return index >=0;
  }

  isWaitingChannelExists(id: string): boolean{
    let channels = this.waitingChannels.value;
     let index = channels.findIndex(ch => ch.id == id);
     return index >=0;
  }

   addChannel(id: string){
     //console.log("test me");
    let channels = this.userChannels.value;
     let index = channels.findIndex(ch => ch.id == id);
     //console.log("index: " + index);
     if(index < 0){
      channels.push({id: id, identifier: id, type: [ChannelType.User], name: id, annoymus: false });
      this.userChannels.next(channels);
     }
    
   }

   addWaitingChannel(id: string){
    //console.log("test me");
   let channels = this.waitingChannels.value;
    let index = channels.findIndex(ch => ch.id == id);
    //console.log("index: " + index);
    if(index < 0){
     channels.push({id: id, identifier: id, type: [ChannelType.User], name: id, annoymus: false });
     this.waitingChannels.next(channels);
    }
   
  }

   removeWaitingChannel(id: string){
    let channels = this.waitingChannels.value;
    let index = channels.findIndex(ch => ch.id == id);
    //console.log("index: " + index);
    if(index >= 0){
      channels.splice(index, 1);
      //this.userChannels.next(channels);
     this.waitingChannels.next(channels);
    }
   }

   createChannel(channelDetails: Channel){
     // first perform api call to create channel in database
    //this.addChannel(channelDetails.id);

     // than add listening to channel
      this.addListeningChannel(channelDetails);
      //console.log("waiting channels");
      //console.log(this.userChannels.value);
      let cannelCommand : ChannelCommand = {
        commandType: ChannelCommandType.OPEN_CHANNEL,
        typeCommand: "message",
        sender: channelDetails.id,
        senderIdentifier: channelDetails.id,
        text: "",
        id: channelDetails.id,
        date: new Date()
      }
     // send message to all potentional listeneers
      this.publishBroadCast(cannelCommand);
   }

   private async  publishBroadCast(channelCommand: ChannelCommand){
    //console.log("publish:");
    //console.log(channelCommand);
    await this.pubnub.publish({
      message: channelCommand,//{sender: this.userName,text: this.myText,id: '200'},
      channel: "BroadcastAll"
    }).catch(error => {  // catch the errors
      console.log(error);
    });;

    //this.myText = "";
   }

  private async  publishCommand(channelCommand: ChannelCommand){
    //console.log("publish:");
    //console.log(channelCommand);
    
    await this.pubnub.publish({
      message: channelCommand,//{sender: this.userName,text: this.myText,id: '200'},
      channel:  'M-' + channelCommand.channelIdentifier + '-CHANNEL'
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

    switch(channelCommand.typeCommand){
      case "message":
      case 'logIn':
        this.publishCommand(channelCommand);
        break;
      case 'getOut':
        this.closeChannel(channelCommand);
        break;
    }

    // switch(channelCommand.commandType){
    //   case ChannelCommandType.MESSAGE:
    //     console.log("MESSAGE");
    //     console.log(channelCommand);
    //     this.publishCommand(channelCommand);
    //     break;
    //   case ChannelCommandType.CLOSE_CHANNEL:
    //     this.closeChannel(channelCommand);
    //     break;
    // }
   }

   markAsReadMessages(channelIdentifier: string){
    var channels = this.userChannels.value;
    var index = channels.findIndex(ch => ch.id == channelIdentifier);
    if(index >= 0){
      channels[index].unreadMessages = 0;
      this.userChannels.next(channels);
    }
   }

   private addMessage(channelCommand: ChannelCommand)
   {
     console.log("HELLO");
     console.log(channelCommand);
     var channels = this.userChannels.value;
     console.log(channels);
    var index = channels.findIndex(ch => ch.id == channelCommand.channelIdentifier);
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

   addUser(id: string){
     let channels = this.userChannels.value;
     let index = channels.findIndex(ch => ch.id == this.userID);
     if(index >= 0){
       channels[index].users.add(id);
       this.userChannels.next(channels);
     }
   }

   handleCommand(channelID: string,channelCommand: ChannelCommand): void{
//console.log("HANDELING");
//console.log(channelCommand);
    if(channelID == "BroadcastAll")
    {
      if(channelCommand.id != this.userID)
      {
        // if(!channelCommand.id.startsWith('M-') && !channelCommand.id.endsWith('-CHANNEL'))
        //   channelCommand.id = 'M-' + channelCommand.id + '-CHANNEL';
        this.addWaitingChannel(channelCommand.id);
      }
      return;
    }

    switch(channelCommand.typeCommand){
      case "message":
        console.log("MESSAG ME@");
        this.addMessage(channelCommand);
         break;
      case "logIn":
        if(channelCommand.id != this.userID)
      this.addUser(channelCommand.id);
      break;
      case "getOut":
        this.removeListeningChannel(channelCommand.id);
        break;
    }

    // switch(channelCommand.commandType){
    //   case ChannelCommandType.MESSAGE:
    //     this.addMessage(channelCommand);
    //     break;
    //   case ChannelCommandType.CLOSE_CHANNEL:
    //     this.removeListeningChannel(channelCommand.id);
    //     break;
    //   case ChannelCommandType.OPEN_CHANNEL:
    //     if(channelCommand.id == this.userID)
    //       return;
    //     console.log("trying to add to waiting");
    //     this.addWaitingChannel(channelCommand.id);
    //     break;
    // }

//console.log(this.userChannels.value);

   }
}
