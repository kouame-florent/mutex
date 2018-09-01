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
    public abstract <U> Result<U> flatMap(Function<V, Result<U>> f);
    public abstract V getOrElse(V defaultValue);
    public abstract V getOrElse(Supplier<V> defaultValue);
    public abstract void forEach(Effect<V> ef);
    public abstract void forEachOrThrow(Effect<V> ef);
    
    public Result<V> orElse(Supplier<Result<V>> defaultValue) {
        return map(x -> this).getOrElse(defaultValue);
    }
    
    @SuppressWarnings("rawtypes")
    private static Result empty = new Empty();
    
    
     
    private static class Empty<V> extends Result<V> {
        
        public Empty() {
            super();
        }
        
        @Override
        public V getOrElse(final V defaultValue) {
            return defaultValue;
        }
        
        @Override
        public <U> Result<U> map(Function<V, U> f) {
            return empty();
        }
        
        @Override
        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            return empty();
        }
        
        @Override
        public String toString() {
            return "Empty()";
        }
        
        @Override
        public V getOrElse(Supplier<V> defaultValue) {
            return defaultValue.get();
        }

        @Override
        public void forEach(Effect<V> ef) {
           // Empty. Do nothing.
        }

        @Override
        public void forEachOrThrow(Effect<V> ef) {
             // Empty. Do nothing.
        }
    }

    
    private static class Failure<V> extends Empty<V> {
        
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
        
        @Override
        public <U> Result<U> flatMap(Function<V, Result<U>> f) {
            return failure(exception);
        }
        
        @Override
        public void forEachOrThrow(Effect<V> ef) {
            throw exception;
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
        
        @Override
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

        @Override
        public void forEach(Effect<V> ef) {
            ef.apply(value);
        }

        @Override
        public void forEachOrThrow(Effect<V> ef) {
            ef.apply(value);
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
    
    @SuppressWarnings("unchecked")
    public static <V> Result<V> empty() {
        return empty;
    }
    
    public Result<V> filter(Function<V, Boolean> p) {
        return flatMap(x -> p.apply(x) ? this : failure("Condition not matched"));
    }
    
    public Result<V> filter(Function<V, Boolean> p, String message) {
        return flatMap(x -> p.apply(x) ? this : failure(message));
    }
    
    public static <V> Result<V> of(V value) {
        return value != null ? success(value) : Result.failure("Null value");
    }
    
    public static <V> Result<V> of(V value, String message) {
        return value != null ? success(value) : failure(message);
    }
    
    public static <V> Result<V> of(Function<V, Boolean> predicate, V value) {
        try {
            return predicate.apply(value) ? success(value) : empty();
        } catch (Exception e) {
            String errMessage = String.format("Exception while evaluating predicate: %s", value);
            return Result.failure(new IllegalStateException(errMessage, e));
        }
    }
    
    public static <V> Result<V> of(Function<V, Boolean> predicate, 
            V value, String message) {
        try{
            return predicate.apply(value) ? Result.success(value) 
                    : Result.failure(String.format(message, value));
        } catch (Exception e) {
            String errMessage = String.format("Exception while evaluating predicate: %s",
            String.format(message, value));
            return Result.failure(new IllegalStateException(errMessage, e));
        }
    }
    
}
