package com.example.controller;

import javax.naming.AuthenticationException;
import javax.persistence.NonUniqueResultException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceAlreadyExistsException;
import com.example.exception.UnauthorizedException;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    /*
     * As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. 
     * The body will contain a representation of a JSON Account, but will not contain an accountId.

    - If the registration is not successful due to a duplicate username, the response status should be 409. (Conflict)
    - If the registration is not successful for some other reason, the response status should be 400. (Client error)
     */
    @PostMapping(value = "register")
    public ResponseEntity<?>register(@RequestBody Account account) {
        try {
            Account addedAccount = accountService.addAccount(account);
            return ResponseEntity.status(200).body(addedAccount);
        } catch (ResourceAlreadyExistsException e) {
            return ResponseEntity.status(409).body("Conflict");
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body("Client");
            
        }
    }

    /*
     * As a user, I should be able to verify my login on the endpoint POST localhost:8080/login. The request body will contain a JSON representation of an Account.
 
    If successful, the response body should contain a JSON of the account in the response body, including its accountId. The response status should be 200 OK, 
    which is the default.
    - If the login is not successful, the response status should be 401. (Unauthorized)
     */

    @PostMapping(value = "login")
    public ResponseEntity<?>login(@RequestBody Account account) {
        try {
            Account loginAccount = accountService.verifyAccount(account);
            return ResponseEntity.status(200).body(loginAccount);
        } catch (UnauthorizedException e) {
            System.out.println("The start of a EXCEPTION");
            e.printStackTrace();
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    /*
     * As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. 
     * The request body will contain a JSON representation of a message, which should be persisted to the database, but will not contain a messageId.

    -  If successful, the response body should contain a JSON of the message, 
        including its messageId. The response status should be 200, which is the default. The new message should be persisted to the database.
    - If the creation of the message is not successful, the response status should be 400. (Client error)
     */
    @PostMapping(value = "messages")
    public ResponseEntity<?>createMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.addMessage(message);
            return ResponseEntity.status(200).body(createdMessage);
        } catch (BadRequestException e) {
            return ResponseEntity.status(400).body("Client error");
        }
    }

    /*
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.

    - The response body should contain a JSON representation of a list containing all messages retrieved from the database. 
    It is expected for the list to simply be empty if there are no messages. The response status should always be 200, which is the default.
     */
    @GetMapping(value = "messages")
    public ResponseEntity<?>getAllMessages() {
        return ResponseEntity.status(200).body(messageService.getAllMessages());
    }

    /*
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{messageId}.

    - The response body should contain a JSON representation of the message identified by the messageId. It is expected
    for the response body to simply be empty if there is no such message. The response status should always be 200, which is the default.
     */
    @GetMapping(value = "messages/{messageId}")
    public ResponseEntity<?>getMessageById(@PathVariable int messageId) {
        //System.out.println("In controller, this is messageId: " + messageId);
        Message message = messageService.getMessageById(messageId);
        //System.out.println("In controller, this is message: " + message);
        return ResponseEntity.status(200).body(message);
    }

    /*
     * As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{messageId}.

    - The deletion of an existing message should remove an existing message from the database. If the message existed, 
    the response body should contain the number of rows updated (1). The response status should be 200, which is the default.

    - If the message did not exist, the response status should be 200, but the response body should be empty. 
    This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should respond with the same type of response.
     */
    @DeleteMapping(value = "messages/{messageId}")
    public ResponseEntity<?>deleteMessageById(@PathVariable int messageId) {
        //System.out.println("In controller, this is messageId: " + messageId);
        Message message = messageService.getMessageById(messageId);
        if (message != null) {
            messageService.deleteMessageById(messageId);
            //System.out.println("Inside controller this is count: " + messageService.getCount());
            return ResponseEntity.status(200).body(1);
        }
        return ResponseEntity.status(200).body("");
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
    @PatchMapping(value = "messages/{messageId}")
    public ResponseEntity<?>patchMessageById(@PathVariable int messageId, @RequestBody Message updatedMessage) {
        System.out.println("In controller, this is messageId: " + messageId);
        System.out.println("In controller, this is updatedMessage: " + updatedMessage);

        try {
            Message message = messageService.getMessageById(messageId);
            if (message != null) {
                messageService.patchMessageById(messageId, updatedMessage);
                return ResponseEntity.status(200).body(1);
            }
            return ResponseEntity.status(400).body("Client error");
        } catch (BadRequestException e) {
            // TODO: handle exception
            return ResponseEntity.status(400).body("Client error");
        }
    }

    /*
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{accountId}/messages.

    - The response body should contain a JSON representation of a list containing all messages posted by a particular user, 
    which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. 
    The response status should always be 200, which is the default.
     */
    @GetMapping(value = "accounts/{accountId}/messages")
    public ResponseEntity<?>getAllMessagesByAccountId(@PathVariable int accountId) {
        System.out.println("In controller, this is account Id: " + accountId);

        return ResponseEntity.status(200).body(messageService.getAllMessagesByAccountId(accountId));
    }
}
