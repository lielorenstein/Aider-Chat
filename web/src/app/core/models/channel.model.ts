export enum ChannelType {
  Broadcast, Group, User
}

export enum ChannelCommandType{
  OPEN_CHANNEL, CLOSE_CHANNEL,KICK,MESSAGE
}

export interface ChannelCommand{
  channelIdentifier?: string;
  commandType: ChannelCommandType;
  sender: string;
  senderIdentifier?: string;
  text: string;
  id: string;
  date: Date;
}

export interface Channel{
  name: string;
  identifier: string;
  annoymus: boolean;
  type: ChannelType[];
  messages?: ChannelCommand[];
  unreadMessages?: number;
}
