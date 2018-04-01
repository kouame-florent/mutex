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
 * @param <V>
 */
public class Triplet<T,U,V>{
    public final T _1;
    public final U _2;
    public final V _3;
    
    public Triplet(T t, U u,V v) {
        this._1 = t;
        this._2 = u;
        this._3 = v;
    }
}
