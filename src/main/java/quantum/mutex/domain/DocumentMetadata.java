/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "DocumentMetadata.findByAttributeName",
        query = "SELECT f FROM DocumentMetadata f WHERE f.attributeName = :attributeName " 
    ),
    @NamedQuery(
        name = "DocumentMetadata.findByAttributeNameAndAttributeValue",
        query = "SELECT f FROM DocumentMetadata f WHERE f.attributeName = :attributeName AND f.attributeValue = :attributeValue " 
    ),
    
  
})
@Table(name = "document_metadata",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"attribute_name","attribute_value"})}
)
@Entity
public class DocumentMetadata extends RootEntity{
    
    @Column(name = "attribute_name")
    private String attributeName;
    
    @Column(name = "attribute_value")
    private String attributeValue;

    public DocumentMetadata() {
    }

    public DocumentMetadata(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }
    

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

   
}
