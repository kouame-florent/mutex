/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

/**
 *
 * @author Florent
 */
@Stateless
public class ConfigService {

    private static final Logger LOG = Logger.getLogger(ConfigService.class.getName());
    
    
    @PersistenceContext
    protected EntityManager em;
    
    public void buildIndex(){
        try {
            LOG.log(Level.INFO, "... BUILDING INDEX ...");
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            fullTextEntityManager.createIndexer().startAndWait();
            LOG.log(Level.INFO, "... BUILT ENDED SUCCESSFULLY ...");
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
