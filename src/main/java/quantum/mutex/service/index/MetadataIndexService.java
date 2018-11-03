/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.index;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import quantum.mutex.dto.MetadataDTO;

/**
 *
 * @author Florent
 */
@Stateless
public class MetadataIndexService {
    
    public Map<String,Object> getJsonMap(@NotNull MetadataDTO metadataDTO){
        Map<String, Object> jsonMap = new HashMap<>();
        
        jsonMap.put("uuid", metadataDTO.getUuid());
        jsonMap.put("attribute_name", metadataDTO.getAttributeName());
        jsonMap.put("attribute_value", metadataDTO.getAttributeValue());
        jsonMap.put("file_uuid", metadataDTO.getMutexFileUUID());
        jsonMap.put("permissions", metadataDTO.getPermissions());
        
        return jsonMap;
    }
}
