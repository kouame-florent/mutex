/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutex.util;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import org.apache.commons.collections4.ListUtils;



/**
 *
 * @author Florent
 */
@RequestScoped
public class TextService {

    private static final Logger LOG = Logger.getLogger(TextService.class.getName());
     
    public List<List<String>> partition(String text,int size){
        return ListUtils.partition(distinct(toList(text)), size) ;
    }
    
    public List<String> toList(String text){
        String[] split = text.split("\\s+");
        LOG.log(Level.INFO, "--> SPLIT SIZE: {0}", split.length);
        return Arrays.stream(split).map(t -> t.toLowerCase())
                .collect(Collectors.toList());
    }     
    
    public List<String> distinct( List<String> texts){
        List<String> distincs = texts.stream().distinct().collect(Collectors.toList());
        LOG.log(Level.INFO, "--> DISTINCT WORLD: {0}", distincs.size());
        return distincs; 
    }
    
    public Optional<String> toText(List<String> texts){
        String finalText = texts.stream().collect(Collectors.joining(" "));
        return Optional.of(finalText)
                .or(() -> Optional.empty());
                //mapFailure(new Exception("Cannot create text."));
    }
}
