/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

import quantum.mutex.util.functional.Result;


/**
 *
 * @author Florent
 */
public class SizeRangeCriterion implements SearchCriterion{
    
    private final long minSize;
    private final long maxSize;

    private SizeRangeCriterion(long minSize, long maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    
    public static Result<SizeRangeCriterion> of(long minSize, long maxSize){
        return isValid(minSize, maxSize) ? 
                Result.success(new SizeRangeCriterion(minSize,maxSize)) :
                Result.empty();
    }

    private static boolean isValid(long min,long max) {
        return (min > 0) && (min <= max);
    }

    public long minSize() {
        return minSize;
    }

    public long maxSize() {
        return maxSize;
    }
    
}
