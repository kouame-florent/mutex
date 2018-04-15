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
        name = "Metadata.findByAttributeName",
        query = "SELECT f FROM Metadata f WHERE f.attributeName = :attributeName " 
    ),
    @NamedQuery(
        name = "Metadata.findByAttributeNameAndAttributeValue",
        query = "SELECT f FROM Metadata f WHERE f.attributeName = :attributeName AND f.attributeValue = :attributeValue " 
    ),
    
  
})
@Table(name = "metadata")
@Entity
public class Metadata extends BusinessEntity{
    
    @Column(name = "attribute_name")
    private String attributeName;
    
    @Column(name = "attribute_value")
    private String attributeValue;

    public Metadata() {
    }

    public Metadata(String attributeName, String attributeValue) {
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
