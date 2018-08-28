/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.common;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Florent
 * @param <V>
 */
public abstract class Result<V> implements Serializable{
    
    private Result() {
        
    }
    
    public abstract <U> Result<U> map(Function<V, U> f); 
    public abstract V getOrElse(V defaultValue);
    public abstract V getOrElse(Supplier<V> defaultValue);
    
    public Result<V> orElse(Supplier<Result<V>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }
    
    private static class Failure<V> extends Result<V> {
        
        private final RuntimeException exception;

        public Failure(String message) {
            super();
            this.exception = new IllegalStateException(message);
        }

        public Failure(Exception e) {
            super();
            this.exception = new IllegalArgumentException(e.getMessage(), e);
        }
        
        @Override
        public V getOrElse(V defaultValue) {
            return defaultValue;
        }
        
        @Override
        public V getOrElse(Supplier<V> defaultValue) {
            return defaultValue.get();

        }
        
        @Override
        public <U> Result<U> map(Function<V, U> f) {
            return failure(exception);
        }
        
        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            return failure(exception);
        }

        @Override
        public String toString() {
            return String.format("Failure(%s)", exception.getMessage());
        }
    }
    
    private static class Success<V> extends Result<V> {
        
        private final V value;
        
        private Success(V value) {
            super();
            this.value = value;
        }
        
        @Override
        public V getOrElse(V defaultValue) {
            return value;
        }
        
        @Override
        public V getOrElse(Supplier<V> defaultValue) {
            return value;
        }
        
        @Override
        public <U> Result<U> map(Function<V, U> f) {
            try {
                return success(f.apply(this.value));
            } catch (Exception e) {
                return failure(e);
            }
        }
        
        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            try {
                return f.apply(this.value);
            } catch (Exception e) {
                return failure(e.getMessage());
            }
        }

        @Override
        public String toString() {
            return String.format("Success(%s)", value.toString());
        }
    }
    
    public static <V> Result<V> failure(String message) {
        return new Failure<>(message);
    }
    
    public static <V> Result<V> failure(Exception e) {
        return new Failure<>(e);
    }
    
    public static <V> Result<V> failure(RuntimeException e) {
        return new Failure<>(e);
    }
    
    public static <V> Result<V> success(V value) {
        return new Success<>(value);
    }
    
}
