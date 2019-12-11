/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import io.mutex.index.valueobject.Constants;

/**
 *
 * @author Florent
 */
@Stateless
public class TextHandlingService {

    private static final Logger LOG = Logger.getLogger(TextHandlingService.class.getName());
    
    PolicyFactory policyFactory;
    
    @PostConstruct
    public void init(){
        policyFactory = new HtmlPolicyBuilder()
                .allowElements("em")
                .allowAttributes("style").onElements("em").toFactory();
    }
    
    public String sanitize(String text){
       return policyFactory.sanitize(text);
    }
    
   
}
