/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.api;


import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import quantum.functional.api.Result;


/**
 *
 * @author Florent
 */
@Stateless
public class ApiClientUtils {
    
    public Result<Response> get(String target,MediaType mediaType){
        Client client = ClientBuilder.newClient();
        Response response = client.target(target).request(mediaType).get();
        return Result.of(response);
    }
    
    public Result<Response> put(String target,Entity<?> entity){
        Client client = ClientBuilder.newClient();
        Response response = client.target(target).request().put(entity);
        return Result.of(response);
    }
    
    public Result<Response> post(String target,Entity<?> entity, MediaType mediaType){
        Client client = ClientBuilder.newClient();
        Response response = client.target(target).request(mediaType).post(entity);
        return Result.of(response);
    }
    
   
}
