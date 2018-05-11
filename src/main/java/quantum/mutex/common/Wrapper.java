/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Florent
 * @param <T>
 */
public class Wrapper<T> { //not actually used
    
    private final String id = UUID.randomUUID().toString();
    
    private T ref;
    private final List<T> children = new ArrayList<>();

    public Wrapper(T ref) {
        this.ref = ref;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public List<T> getChildren() {
        return children;
    }

    
}
