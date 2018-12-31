/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import quantum.functional.api.Result;


/**
 *
 * @author Florent
 */
@Dependent
public class ApiClientUtils {

    private static final Logger LOG = Logger.getLogger(ApiClientUtils.class.getName());
    private Client client;
    
    @PostConstruct
    public void init(){
        client = ClientBuilder.newClient();
    }
    
    public Result<Response> get(String target,MediaType mediaType){
        Response response = client.target(target).request(mediaType).get();
        return Result.of(response);
    }
    
    public Result<Response> put(String target,Entity<?> entity,MultivaluedMap headers){
//        LOG.log(Level.INFO, "<<-- PUT TARGET: {0}", target);
        Response response = client.target(target).request()
                .headers(headers).put(entity);
        return Result.of(response);
    }
    
    public Result<Response> post(String target,Entity<?> entity, MultivaluedMap headers){
        LOG.log(Level.INFO, "|<<-- POST TARGET : {0}", target);
        Response response = client.target(target).request()
                .headers(headers).post(entity);
        LOG.log(Level.INFO, "|<<-- POST RESPONSE : {0}", response);
        return Result.of(response);
    }
    
    @PreDestroy
    public void close(){
        client.close();
    }
   
}
