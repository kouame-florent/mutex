/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.type.criterion;



import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;


/**
 *
 * @author Florent
 */
public class DateRangeCriterion implements SearchCriterion{
    private final long startDate ;
    private final long endDate ;

    private DateRangeCriterion(long startEpochSecond, long endEpochSecond) {
        this.startDate = startEpochSecond;
        this.endDate = endEpochSecond;
    }
    
    public static Optional<DateRangeCriterion> of(LocalDateTime start, LocalDateTime end){
        long st = Optional.of(start).map(s -> s.toEpochSecond(ZoneOffset.UTC)).orElseGet(() -> Long.valueOf(0));
        long ed = Optional.of(end).map(e -> e.toEpochSecond(ZoneOffset.UTC)).orElseGet(() -> Long.valueOf(0));
        return isValid(st, ed) ? Optional.ofNullable(new DateRangeCriterion(st, ed)) :
                 Optional.empty();

    }

    private static boolean isValid(long start, long end) {
        return (start != 0) && (end != 0) && ((start <= end));
    }
    
    public long startDate(){
        return startDate;
    }
    
    public long endDate(){
        return endDate;
    }
}
