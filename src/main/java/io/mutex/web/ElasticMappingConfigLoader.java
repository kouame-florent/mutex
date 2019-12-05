/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.apache.commons.io.IOUtils;




/**
 *
 * @author Florent
 */
@Stateless
public class ElasticMappingConfigLoader {

    private static final Logger LOG = Logger.getLogger(ElasticMappingConfigLoader.class.getName());
        
    public Optional<String> retrieveIndexMapping(String mapping){
        String json = getClassLoader()
                .flatMap(c -> getFileInput(c,mapping))
                .flatMap(in -> toString(in))
                .orElseGet(() -> "");
        return Optional.of(json);
    }
    
    private Optional<InputStream> getFileInput(ClassLoader classLoader,String resource){
        return Optional.of(classLoader.getResourceAsStream(resource));
        
    }
    
    private Optional<ClassLoader> getClassLoader(){
        return Optional.of(Thread.currentThread().getContextClassLoader());
    }
    
    Optional<String> toString(InputStream inputStream){
        try{
            return Optional.ofNullable(IOUtils.toString(inputStream, StandardCharsets.UTF_8.name()));
        }catch(IOException ex){
            LOG.log(Level.INFO, "....CLOSING INPUT.....");
            return Optional.empty();
        }finally{
                try{
                    try (inputStream) {
                        LOG.log(Level.INFO, "....CLOSING INPUT.....");
                    }
                }catch(IOException ex){
                    LOG.log(Level.SEVERE, "Error closing file: {0}", ex);
                }
         } 
    }
    
}
