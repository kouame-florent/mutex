/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;

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
    
    public static SizeRangeCriterion of(long minSize, long maxSize){
       return new SizeRangeCriterion(minSize,maxSize);
    }
   
    public static SizeRangeCriterion getDefault(){
        return new SizeRangeCriterion(0, 0);
    }
    
    @Override
    public boolean isValid() {
        return (minSize > 0) && (minSize <= maxSize);
    }

    public long minSize() {
        return minSize;
    }

    public long maxSize() {
        return maxSize;
    }
    
}
