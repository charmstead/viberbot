package com.viberbot.viberbot.EventListener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.viber.bot.api.ViberBot;
import com.viber.bot.message.TextMessage;
import com.viberbot.viberbot.messageMapper.MessageMapper;

import java.util.Optional;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class BotInitialization implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = Logger.getLogger(BotInitialization.class);

    private final ViberBot bot;
        
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public BotInitialization(ViberBot bot) {
        this.bot = bot;

    }

    @Value("${application.viber-bot.webhook-url}")
    private String webhookUrl;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        
        try {
            bot.setWebhook(webhookUrl).get();
        } catch (Exception e) {
            logger.error("Bot webhook cannot be set.");
        }

       
       bot.onMessageReceived((event, message, response) ->{
           
           MessageMapper MAPPER = new MessageMapper();
           MAPPER.mapEventMessageToViaeaiMessage(message);
           
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(MAPPER);
            } catch (JsonProcessingException ex) {
                
                logger.debug(ex);
            }
           
           
           response.send(message);
        }); // echos everything back
       
        bot.onConversationStarted(event -> Futures.immediateFuture(Optional.of( // send 'Hi UserName' when conversation is started
                new TextMessage("Hi " + event.getUser().getName()))));
        
        
//        bot.onSubscribe((event, response) -> {
//            UserProfile userProfile = event.getUser();
//            
//            response.send(text("Hi " + userProfile.getName()));
//        });
//        bot.onUnsubscribe(event -> subscriberService.removeSubscriber(event.getUserId()));
    }

    private TextMessage text(String text) {
        return new TextMessage(text);
    }

}
