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
 */
public interface Effect<T> {
    void apply(T t);
}
