package com.example.service;

import com.example.repository.AccountRepository;
import com.example.entity.Account;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceAlreadyExistsException;
import com.example.exception.UnauthorizedException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;


    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /*
    - The registration will be successful if and only if the username is not blank, the password 
        is at least 4 characters long, and an Account with that username does not already exist. 
     */
    public Account addAccount (Account account) throws ResourceAlreadyExistsException, BadRequestException{
        System.out.println("This is account: " + account);
        System.out.println("We are printing out to see if there is a account with the username: " + accountRepository.findByUsername(account.getUsername()));
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new ResourceAlreadyExistsException();
        } 
        else if (account.getUsername().isBlank() && account.getPassword().length() < 4) {
            throw new BadRequestException();
        }
        else {
            Account newAccount = accountRepository.save(account);
            return newAccount;
        }
    }

    /*

    - The login will be successful if and only if the username and password provided in the request body JSON match a real account existing on the database. 
    If successful, the response body should contain a JSON of the account in the response body, including its accountId. The response status should be 200 OK, 
    which is the default.
     */
    public Account verifyAccount (Account account) throws UnauthorizedException  {
        Account verifyAccount = accountRepository.findByUsernameAndPassword(account.getUsername(), account.getPassword());
        System.out.println("This is account we are trying to verify: " + verifyAccount);
        if (verifyAccount == null) {
            throw new UnauthorizedException();
        }
        return verifyAccount;
    }
    
}
