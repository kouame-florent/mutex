/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 *
 * @author Florent
 */
@Stateless
public class TextService {
    
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
