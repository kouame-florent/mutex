/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.io.Serializable;
import java.util.UUID;
import javax.ejb.Stateless;
import quantum.mutex.domain.Document;

/**
 *
 * @author Florent
 */
@Stateless
public class DocumentDAOImpl extends GenericDAOImpl<Document, UUID> implements DocumentDAO{
    
    public DocumentDAOImpl() {
        super(Document.class);
    }
    
}
