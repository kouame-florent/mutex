package quantum.mutex.util.functional;

public interface Effect<T> {
  void apply(T t);
}