package com.example.service;

import org.springframework.stereotype.Service;


import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.exception.BadRequestException;


import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private MessageRepository messageRepository;

    public MessageService (MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /*
        - The creation of the message will be successful if and only if the messageText is not blank, is not over 
        255 characters, and postedBy refers to a real, existing user. 
     */
    public Message addMessage(Message message) throws BadRequestException{
        System.out.println("This is message to be added: " + message);
        if (!message.getMessageText().isBlank() && message.getMessageText().length() < 255 && messageRepository.existsById(message.getPostedBy()) != false) {
            Message addedMessage = messageRepository.save(message);
            return addedMessage;
        }
        throw new BadRequestException();
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById (int id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            System.out.println("In messageService, this is the message: " + message);
            return message;
        }
        return null;
    }

    public void deleteMessageById (int id) {
        Optional<Message> optionalMessage = messageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            Message message = optionalMessage.get();
            System.out.println("Inside Service, this is the number of rows before delete: " + messageRepository.count());
            messageRepository.delete(message);
            System.out.println("Inside Service, this is the number of rows after delete: " + messageRepository.count());
            //return message;
            //messageRepository.count();
        }
    }

    /*
     * As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{messageId}. 
     * The request body should contain a new messageText values to replace the message identified by messageId. 
     * The request body can not be guaranteed to contain any other information.

    - The update of a message should be successful if and only if the message id already exists and the new messageText
     is not blank and is not over 255 characters. If the update is successful, the response body should contain the number
      of rows updated (1), and the response status should be 200, which is the default. The message existing on the database should have the updated messageText.
    - If the update of the message is not successful for any reason, the response status should be 400. (Client error)
     */

    public Message patchMessageById (int id, Message updatedMessage) throws BadRequestException{
        Optional<Message> optionalMessage = messageRepository.findById(id);
        //System.out.println("Inside Service, this is the updated Messaged passed in param: " + updatedMessage.getMessageText());
        //System.out.println("Inside Service, this is the Messaged found by the Id: " + optionalMessage.get());
        //System.out.println("Inside Service, this is to check if message is present: " + optionalMessage.isPresent());
        //System.out.println("Inside Service, this is to check if updated Message text is blank: " + updatedMessage.getMessageText().isBlank());
        if (optionalMessage.isPresent() && !updatedMessage.getMessageText().isBlank() && updatedMessage.getMessageText().length() <= 255) {
            Message message = optionalMessage.get();
            message.setMessageText(updatedMessage.getMessageText());
            System.out.println("Inside Service, this is the updated Messaged after checks: " + message.getMessageText());
            messageRepository.save(message);
            return message;
        }
        throw new BadRequestException();
    }

    /*
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{accountId}/messages.

    - The response body should contain a JSON representation of a list containing all messages posted by a particular user, 
    which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. 
    The response status should always be 200, which is the default.
     */
    public List<Message>getAllMessagesByAccountId(int id) {
        //Optional<Account> optionalAccount = accountRepository.
        List<Message> messageList = messageRepository.findByPostedBy(id);
        System.out.println("Inside controller, this is the message list: " + messageList);
        return messageList;
        
    }
}
