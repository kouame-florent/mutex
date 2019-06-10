/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

import java.io.Serializable;

/**
 *
 * @author Florent
 */
public interface SearchCriteria  extends Serializable{
    boolean isValid();
    
}
