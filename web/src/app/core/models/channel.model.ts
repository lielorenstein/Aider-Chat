export enum ChannelType {
  Broadcast, Group, User
}

export enum ChannelCommandType{
  OPEN_CHANNEL, CLOSE_CHANNEL,KICK,MESSAGE
}

export interface ChannelCommand{
  channelIdentifier?: string;
  senderIdentifier?: string;
  
  
  commandType?: ChannelCommandType;
  typeCommand: "message" | "getOut" | "logIn"
  sender: string;
  id: string;
  text: string;
  date?: Date;
}

export interface Channel{
  name: string;
  identifier: string;
  annoymus: boolean;
  id: string;
  users?: Set<string>,
  type: ChannelType[];
  messages?: ChannelCommand[];
  unreadMessages?: number;
}
