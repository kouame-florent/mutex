package quantum.mutex.common;

public interface RNG {

  Tuple<Integer, RNG> nextInt();
}
