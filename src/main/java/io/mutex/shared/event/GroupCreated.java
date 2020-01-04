/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.shared.event;

import java.lang.annotation.ElementType;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

/**
 *
 * @author root
 */
@Qualifier
@Retention(RUNTIME)
@Target({ElementType.FIELD, PARAMETER})
public @interface GroupCreated {
    
}
