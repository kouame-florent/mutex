/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author root
 */
@ApplicationException(rollback=true)
public class AdminExistException extends Exception{

    public AdminExistException(String message) {
        super(message);
    }
    
}
