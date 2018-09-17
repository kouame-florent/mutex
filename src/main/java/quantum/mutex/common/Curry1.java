/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.common;

import java.util.function.Function;

/**
 *
 * @author Florent
 * @param <T>
 * @param <U>
 * @param <V>
 */
public interface Curry1<T,U,V> extends Function<T,Function<U,V>>{
    
}
