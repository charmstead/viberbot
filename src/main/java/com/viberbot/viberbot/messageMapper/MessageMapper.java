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
                 
                message.setMessageBody(map.get("text").toString());
                break;
            
//            case "rich_media":
//                 RichMediaMessage txtMsg =mapper.convertValue(map, RichMediaMessage.class);
//                break;
            
            case "video":
                
                message.setIsFile(true)
                        .setIsBot(true)
                        .setMessageType(MessageType.video)
                        .setFileUrl(map.get("media").toString())
                        .setMessageBody(map.get("text").toString());
                break;
                
            case "picture":
                
                message.setIsFile(true)
                        .setIsBot(true)
                        .setMessageType(MessageType.document)
                        .setFileUrl(map.get("media").toString())
                        .setMessageBody(map.get("text").toString());
                break;
                
            case "file":
               
               message .setIsBot(true)
                       .setIsFile(true)
                       .setMessageType(MessageType.document)
                       .setFileUrl(map.get("media").toString())
                       .setMessageBody(map.get("file_name").toString());
                       
               break;
            
            case "url":
              
                message .setIsBot(true)
                        .setMessageType(MessageType.site)
                        .setFileUrl(map.get("media").toString());
                        
                break;
                
                
            case "location":
                HashMap<String,Object> location= (HashMap<String,Object>) map.get("location");
                String loc = String.format("longitude=%s\nlatitude=%s",
                                location.get("lon").toString(),location.get("lat").toString());
                message.setIsBot(true)
                       .setMessageType(MessageType.map)
                       .setMessageBody(loc);
                        
                break;
                
                
            case "contact":
                HashMap<String,Object> contact= (HashMap<String,Object>) map.get("location");
                String con = String.format("name=%s\nphoneNumber=%s",
                                contact.get("name").toString(),contact.get("phone_number").toString());
                message.setIsBot(true)
                       .setMessageType(MessageType.text)
                       .setMessageBody(con);
                break;
            
            case "sticker":
                
                message.setIsBot(true)
                       .setMessageType(MessageType.text)
                       .setMessageBody(map.get("sticker_id")+"");
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