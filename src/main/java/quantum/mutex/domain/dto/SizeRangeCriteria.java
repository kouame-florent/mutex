/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.dto;

/**
 *
 * @author Florent
 */
public class SizeRangeCriteria implements SearchCriteria{
    
    private final long minSize;
    private final long maxSize;

    private SizeRangeCriteria(long minSize, long maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    
    public static SizeRangeCriteria of(long startSize, long endSize){
       return new SizeRangeCriteria(startSize, endSize);
    }
    
    public static SizeRangeCriteria getDefault(){
        return new SizeRangeCriteria(0, 0);
    }
    
    @Override
    public boolean isValid() {
        return (minSize > 0) && (minSize <= maxSize);
    }

    public long minSize() {
        return minSize;
    }

    public long maxSize() {
        return minSize;
    }
    
}
