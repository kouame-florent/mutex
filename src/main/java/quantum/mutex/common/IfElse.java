/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.common;

import java.util.function.Supplier;

/**
 *
 * @author Florent
 */
public class IfElse {
    
    public static <T> T ifElse(boolean b, Supplier<T> truecase, Supplier<T> falsecase) {
        return b ? truecase.get() : falsecase.get();
    }
}
