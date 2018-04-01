/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.common;

/**
 *
 * @author Florent
 * @param <T>
 * @param <U>
 */
public class Pair<T,U> {
    
    public final T _1;
    public final U _2;
    
    public Pair(T t, U u) {
        this._1 = t;
        this._2 = u;
    }
}
