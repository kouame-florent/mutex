/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.apache.commons.io.IOUtils;
import quantum.functional.api.Nothing;
import quantum.functional.api.Result;


/**
 *
 * @author Florent
 */
@Stateless
public class ElasticMappingConfigLoader {

    private static final Logger LOG = Logger.getLogger(ElasticMappingConfigLoader.class.getName());
  
    private final Function<Nothing,Result<ClassLoader>> getClassLoader = n -> 
            Result.of(Thread.currentThread().getContextClassLoader());
    
    private final Function<ClassLoader,Result<InputStream>> getVirtualPageFileInput = c ->
            Result.of(c.getResourceAsStream("template/virtual_page_mapping.json"));
    
    private final Function<ClassLoader,Result<InputStream>> getMetadataFileInput = c ->
            Result.of(c.getResourceAsStream("template/metadata_mapping.json"));
   
    
    private final Function<InputStream,Result<String>> toString = in -> {
        try{
            return Result.success(IOUtils.toString(in, StandardCharsets.UTF_8.name()));
        }catch(IOException ex){
            return Result.failure(ex);
        }finally{
                try{
                    LOG.log(Level.SEVERE, "....CLOSING INPUT.....");
                    in.close();
                }catch(IOException ex){
                    LOG.log(Level.SEVERE, "Error closing file: {0}", ex);
                }
         } 
       
    };
     

    public Result<String> retrieveVirtualPageMapping(){
       String json = getClassLoader.apply(Nothing.instance)
                .flatMap(c -> getVirtualPageFileInput.apply(c)).flatMap(in -> toString.apply(in))
                .successValue();
       LOG.log(Level.INFO, "--> JSON BODY {0}", json);
       
        return Result.of(json);
    }
    
    public Result<String> retrieveMetadataMapping(){
       String json = getClassLoader.apply(Nothing.instance)
                .flatMap(c -> getMetadataFileInput.apply(c)).flatMap(in -> toString.apply(in))
                .successValue();
       LOG.log(Level.INFO, "--> JSON BODY {0}", json);
       
        return Result.of(json);
    }
}
