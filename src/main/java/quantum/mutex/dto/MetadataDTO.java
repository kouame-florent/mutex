/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.dto;



import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import quantum.mutex.domain.BusinessEntity;


/**
 *
 * @author Florent
 */
//@NamedQueries({
//    @NamedQuery(
//        name = "Metadata.findByAttributeName",
//        query = "SELECT f FROM Metadata f WHERE f.attributeName = :attributeName " 
//    ),
//    @NamedQuery(
//        name = "Metadata.findByAttributeNameAndAttributeValue",
//        query = "SELECT f FROM Metadata f WHERE f.attributeName = :attributeName AND f.attributeValue = :attributeValue " 
//    ),
//    
//  
//})
//@Table(name = "mx_metadata")
//@Entity
@Getter @Setter  @ToString
public class MetadataDTO{
    
    private String uuid = UUID.randomUUID().toString();
    private String attributeName;
    private String attributeValue;
    private String mutexFileUUID;
    private String permissions;

    public MetadataDTO() {
    }

    public MetadataDTO(String name, String value) {
        this.attributeName = name;
        this.attributeValue = value;
        
    }
    
   
}
