/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.web;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.primefaces.PrimeFaces;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class CoreBacking<T> extends BaseBacking{
    
    List<T> entities = Collections.EMPTY_LIST;
//    Supplier<List<T>> initSupplier;
//    
//    protected void initEntities(){
//        entities = getEntities(supplier)
//    }
    
    protected List<T> getEntities(Supplier<List<T>> supplier){
      return  supplier.get();
    }
    
    protected void openAddEntityDialog(){
        Map<String,Object> options = provideDialogOptions();
        openDialog(options);
    }
    
    abstract protected Map<String,Object> provideDialogOptions();
    
    abstract protected void openDialog(Map<String,Object> options);
}
