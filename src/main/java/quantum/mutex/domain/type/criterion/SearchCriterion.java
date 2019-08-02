/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

import java.io.Serializable;

/**
 *
 * @author Florent
 */
public interface SearchCriterion  extends Serializable{
    boolean isValid();
//    SearchCriterion of();
//    SearchCriterion getDefault();
    
}
