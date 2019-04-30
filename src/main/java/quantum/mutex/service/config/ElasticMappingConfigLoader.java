/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.apache.commons.io.IOUtils;
import quantum.functional.api.Result;


/**
 *
 * @author Florent
 */
@Stateless
public class ElasticMappingConfigLoader {

    private static final Logger LOG = Logger.getLogger(ElasticMappingConfigLoader.class.getName());
        
    public Result<String> retrieveIndexMapping(String mapping){
        String json = getClassLoader()
                .flatMap(c -> getFileInput(c,mapping))
                .flatMap(in -> toString(in))
                .successValue();
        return Result.of(json);
    }
    
    private Result<InputStream> getFileInput(ClassLoader classLoader,String resource){
        return Result.of(classLoader.getResourceAsStream(resource));
        
    }
    
    private Result<ClassLoader> getClassLoader(){
        return Result.of(Thread.currentThread().getContextClassLoader());
    }
    
    Result<String> toString(InputStream inputStream){
        try{
            return Result.success(IOUtils.toString(inputStream, StandardCharsets.UTF_8.name()));
        }catch(IOException ex){
            return Result.failure(ex);
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
