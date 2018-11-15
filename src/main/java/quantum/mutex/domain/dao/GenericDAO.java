/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dao;

import java.util.List;
import quantum.functional.api.Result;


/**
 *
 * @author florent
 * @param <E>
 * @param <ID>
 * 
 */
public interface GenericDAO<E,ID>  {
    
    Result<E> makePersistent(E entity); 
    Result<E> makeTransient(E entity);
    Result<E> findById(ID id);
    Result<E> findReferenceById(ID id);
    List<E> findAll();
    Long getCount();
}
