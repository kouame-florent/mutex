package quantum.mutex.common;

public interface Effect<T> {
  void apply(T t);
}