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

    public Result<String> retrieveVirtualPageMapping(){
        String json = getClassLoader().flatMap(c -> getVirtualPageFileInput(c)).flatMap(in -> toString(in))
                .successValue();
        return Result.of(json);
    }
    
    public Result<String> retrieveMetadataMapping(){
        String json = getClassLoader()
                .flatMap(c -> getMetadataFileInput(c)).flatMap(in -> toString(in))
                .successValue();
       
        return Result.of(json);
    }
    
    public Result<String> retrieveUtilMapping(){
        String json = getClassLoader()
                .flatMap(c -> getUtilFileInput(c)).flatMap(in -> toString(in))
                .successValue();
       
        return Result.of(json);
    }
    
    private Result<InputStream> getUtilFileInput(ClassLoader classLoader){
        return Result.of(classLoader.getResourceAsStream("template/util_mapping.json"));
        
    }
    
    private Result<InputStream> getVirtualPageFileInput(ClassLoader classLoader){
        return Result.of(classLoader.getResourceAsStream("template/virtual_page_mapping.json"));
    }
    
    private Result<InputStream> getMetadataFileInput(ClassLoader classLoader){
        return Result.of(classLoader.getResourceAsStream("template/metadata_mapping.json"));
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
