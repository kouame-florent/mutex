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
    
    private final long startSize;
    private final long endSize;

    public SizeRangeCriteria(long startSize, long endSize) {
        this.startSize = startSize;
        this.endSize = endSize;
    }
    
    @Override
    public boolean isValid() {
        return startSize <= endSize;
    }

    public long startSize() {
        return startSize;
    }

    public long endSize() {
        return endSize;
    }
    
    
    
}
