/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viberbot.viberbot.messageMapper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.viber.bot.message.TextMessage;
import com.viber.bot.message.*;
import com.viberbot.viberbot.viaeaibotMessage.Message;
import com.viberbot.viberbot.viaeaibotMessageTypes.MessageType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *This is the messageMapper class. 
 * It maps individual messageType to the corresponding viaeaiMessage
 * @author TOMIDE
 */
public class MessageMapper {
    
    
    private final Logger log = LoggerFactory.getLogger(MessageMapper.class);
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * This method listens on incoming event messages from viber and
     * converts it to the equivalent viaeaiMessage type
     * @param msg viberMessage
     * @return viaeaiMessage
     */
    public Message mapEventMessageToViaeaiMessage(com.viber.bot.message.Message msg){
      
        Message message = new Message();//viaeaiMessage
        message.setMessage_time(new Date().getTime()+"");
        
        String messageType = msg.getType();
        
        Map map = msg.getMapRepresentation();
        switch(messageType){
            
            case "text":
                if(msg instanceof TextMessage){
                  TextMessage textMessage = (TextMessage)msg;
                  message.setMessageBody(textMessage.getText());
                }
                break;
            
//            case "rich_media":
//                 RichMediaMessage txtMsg =mapper.convertValue(map, RichMediaMessage.class);
//                break;
            
            case "video":
                
                if(msg instanceof VideoMessage){
                  VideoMessage videoMessage = (VideoMessage)msg;
                  message.setIsFile(true)
                        .setIsBot(true)
                        .setMessageType(MessageType.video)
                        .setFileUrl(videoMessage.getUrl())
                        .setMessageBody(videoMessage.getText());
                }
                
                break;
                
            case "picture":
                if(msg instanceof PictureMessage){
                  PictureMessage pictureMessage = (PictureMessage)msg;
                  message.setIsFile(true)
                        .setIsBot(true)
                        .setMessageType(MessageType.image)
                        .setFileUrl(pictureMessage.getUrl())
                        .setMessageBody(pictureMessage.getText());
                }
              
                break;
                
            case "file":
               if(msg instanceof FileMessage){
                  FileMessage fileMessage = (FileMessage)msg;
                  message.setIsFile(true)
                        .setIsBot(true)
                        .setMessageType(MessageType.document)
                        .setFileUrl(fileMessage.getUrl())
                        .setMessageBody(fileMessage.getFilename());
                }
              
               break;
            
            case "url":
                if(msg instanceof UrlMessage){
                  UrlMessage urlMessage = (UrlMessage)msg;
                  message.setIsFile(true)
                        .setIsBot(true)
                        .setMessageType(MessageType.site)
                        .setFileUrl(urlMessage.getUrl());
                        
                }
                break;
                
                
            case "location":
                 if(msg instanceof LocationMessage){
                  LocationMessage locationMessage = (LocationMessage)msg;
                  String loc = String.format("longitude=%s\nlatitude=%s",
                                locationMessage.getLocation().getLongitude()
                                ,locationMessage.getLocation().getLatitude());
                  message.setIsBot(true)
                        .setMessageType(MessageType.map)
                        .setMessageBody(loc);
                }
                
                break;
                
                
            case "contact":
                if(msg instanceof ContactMessage){
                  ContactMessage contactMessage = (ContactMessage)msg;
                  message.setIsBot(true)
                          .setMessageBody("Name: "+contactMessage.getContact().getName()+
                                          "\nPhone_number: "+contactMessage.getContact().getPhoneNumber());
                        
                }

                break;
            
            case "sticker":
                if(msg instanceof StickerMessage){
                  StickerMessage stickerMessage = (StickerMessage)msg;
                  message.setIsBot(true)
                          .setMessageBody(stickerMessage.toString());
                        
                }

                break;
        }
                
        return message;
    }
    
    
    public TextMessage viaeaiMessageToViberTextMessage(Message msg){
       return new TextMessage(msg.getBody());
    }
    
    public UrlMessage viaeaiMessageToViberUrlMessage(Message msg){
       return new UrlMessage(msg.getFileURL());
    }
    
    public FileMessage viaeaiMessageToViberFileMessage(Message msg){
        
       return new FileMessage(msg.getFileURL(),0,msg.getBody());
    }
    
    public PictureMessage viaeaiMessageToViberPictureMessage(Message msg){
        
       return new PictureMessage(msg.getFileURL(),msg.getBody(),null);
    }
    
    public VideoMessage viaeaiMessageToViberVideoMessage(Message msg){
        
       return new VideoMessage(msg.getFileURL(),0,msg.getBody(),null,null);
    }
}