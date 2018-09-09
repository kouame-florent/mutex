/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.common;

/**
 *
 * @author Florent
 * @param <A>
 */
public abstract class List<A>{
    
    public abstract A head();
    public abstract List<A> tail();
    public abstract boolean isEmpty();
    
    @SuppressWarnings("rawtypes")
    public static final List NIL = new Nil();
    
    public abstract List<A> setHead(A h);
    
    private List() {}
    
    
    private static class Nil<A> extends List<A> {
        
        private Nil() {}
        
        @Override
        public A head() {
            throw new IllegalStateException("head called en empty list");
        }
        
        @Override
        public List<A> tail() {
            throw new IllegalStateException("tail called en empty list");
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public List<A> setHead(A h) {
            throw new IllegalStateException("setHead called on empty list");
        }
    }
    
    private static class Cons<A> extends List<A> {
        
        private final A head;
        private final List<A> tail;
        
        private Cons(A head, List<A> tail) {
            this.head = head;
            this.tail = tail;
        }
        
        @Override
        public A head() {
            return head;
        }
        
        @Override
        public List<A> tail() {
            return tail;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public List<A> setHead(A h) {
            return new Cons<>(h, tail());
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static <A> List<A> list() {
        return NIL;
    }
    
    @SafeVarargs
    public static <A> List<A> list(A... a) {
        
        List<A> n = list();
        
        for (int i = a.length - 1; i >= 0; i--) {
            n = new Cons<>(a[i], n);
        }
        return n;
    }
    
    public List<A> cons(A a) {
        return new Cons<>(a, this);
    }
}
