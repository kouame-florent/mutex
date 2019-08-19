package quantum.mutex.util.functional;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import static quantum.mutex.util.functional.TailCall.ret;
import static quantum.mutex.util.functional.TailCall.sus;



public abstract class List<A> {

  protected abstract A head();
  protected abstract List<A> tail();
  protected abstract List<A> take(int n);
  public abstract boolean isEmpty();
  public abstract List<A> setHead(A h);
  public abstract List<A> drop(int n);
  public abstract List<A> dropWhile(Function<A, Boolean> f);
  public abstract List<A> reverse();
  public abstract List<A> init();
  public abstract int length();
  public abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f);
  public abstract <B> Tuple<B, List<A>> foldLeft(B identity, B zero, Function<B, Function<A, B>> f);
  public abstract <B> B foldRight(B identity, Function<A, Function<B, B>> f);
  public abstract A reduce(Function<A, Function<A, A>> f);
  public abstract Result<A> headOption();
  public abstract Result<List<A>> tailOption();
  public abstract String mkStr(String sep);
  public abstract <B> Result<List<B>> sequence(Function<A, Result<B>> f);
  public abstract List<A> takeAtMost(int n);
  public abstract List<A> takeWhile(Function<A, Boolean> p);
  public abstract List<List<A>> subLists();
  public abstract List<List<A>> interleave(A a);
  public abstract List<List<A>> perms();
  public abstract List<Tuple<List<A>, List<A>>> split();
  public abstract Result<Tuple<A, List<A>>> headAndTail();
  public abstract Stream<A> toStream();

  public <B> List<B> map(Function<A, B> f) {
    return foldRight(list(), h -> t -> new Cons<>(f.apply(h),t));
  }

  public List<A> filter(Function<A, Boolean> f) {
    return foldRight(list(), h -> t -> f.apply(h) ? new Cons<>(h,t) : t);
  }

  public <B> List<B> flatMap(Function<A, List<B>> f) {
    return foldRight(list(), h -> t -> concat(f.apply(h), t));
  }

  public List<List<A>> choices() {
    return subLists().flatMap(List::perms);
  }

  public boolean elem(A a) {
    return exists(x -> x.equals(a));
  }

  public Result<List<Tuple<A, Integer>>> zipWithPositionResult() {
    return zip(iterate(0, x -> x + 1, length()));
  }

  public List<Tuple<A, Integer>> zipWithPosition() {
    return zipWithPositionResult().getOrElse(List.list());
  }

  public static <B> List<B> iterate(B seed, Function<B, B> f, int n) {
    List<B> result = list();
    B temp = seed;
    for (int i = 0; i < n; i++) {
      result = List.cons(temp, result);
      temp = f.apply(temp);
    }
    return result.reverse();
  }

  public <B> Result<List<Tuple<A, B>>> zip(List<B> listB) {
    return zip(this, listB);
  }

  public static <A, B> Result<List<Tuple<A, B>>> zip(List<A> listA, List<B> listB) {
    if (listA.length() != listB.length()) {
      return Result.failure("Can't zip lists of different lengths. Use zipAsPossible().");
    }
    List<Tuple<A, B>> list = list();
    List<A> workListT = listA;
    List<B> workListU = listB;
    while (!workListT.isEmpty()) {
      list = new Cons<>(new Tuple<>(workListT.head(), workListU.head()), list);
      workListT = workListT.tail();
      workListU = workListU.tail();
    }
    return Result.success(list.reverse());
  }

  public Result<A> lastOption() {
    return foldLeft(Result.empty(), x -> Result::success);
  }

  public List<A> cons(A a) {
    return new Cons<>(a, this);
  }

  public <A1, A2> Tuple<List<A1>, List<A2>> unzip(Function<A, Tuple<A1, A2>> f) {
    return this.foldRight(new Tuple<>(list(), list()), a -> tl -> {
      Tuple<A1, A2> t = f.apply(a);
      return new Tuple<>(tl._1.cons(t._1), tl._2.cons(t._2));
    });
  }

  public Result<A> getAt_(int index) {
    return index < 0 || index >= length()
        ? Result.failure("Index out of bound")
        : getAt(this, index).eval();
  }

  private static <A> TailCall<Result<A>> getAt(List<A> list, int index) {
    return index == 0
        ? TailCall.ret(Result.success(list.head()))
        : TailCall.sus(() -> getAt(list.tail(), index - 1));
  }

  public Result<A> getAt(int index) {
    Tuple<Result<A>, Integer> identity = new Tuple<>(Result.failure("Index out of bound"), index);
    Tuple<Result<A>, Integer> rt = index < 0 || index >= length()
        ? identity
        : foldLeft(identity, ta -> a -> ta._2 < 0 ? ta : new Tuple<>(Result.success(a), ta._2 - 1));
    return rt._1;
  }

  public Result<A> getAt__(int index) {
    class Tuple<T, U> {

      public final T _1;
      public final U _2;

      public Tuple(T t, U u) {
        this._1 = Objects.requireNonNull(t);
        this._2 = Objects.requireNonNull(u);
      }

      @Override
      public String toString() {
        return String.format("(%s,%s)", _1,  _2);
      }

      @Override
      public boolean equals(Object o) {
        if (!(o.getClass() == this.getClass()))
          return false;
        else {
          @SuppressWarnings("rawtypes")
          Tuple that = (Tuple) o;
          return _2.equals(that._2);
        }
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _1.hashCode();
        result = prime * result + _2.hashCode();
        return result;
      }
    }
    Tuple<Result<A>, Integer> zero = new Tuple<>(Result.failure("Index out of bound"), -1);
    Tuple<Result<A>, Integer> identity = new Tuple<>(Result.failure("Index out of bound"), index);
    Tuple<Result<A>, Integer> rt = index < 0 || index >= length()
        ? identity
        : foldLeft(identity, zero, ta -> a -> ta._2 < 0 ? ta : new Tuple<>(Result.success(a), ta._2 - 1))._1;
    return rt._1;
  }

  public Tuple<List<A>, List<A>> splitAt(int index) {
    return index < 0
        ? splitAt(0)
        : index > length()
            ? splitAt(length())
            : splitAt(list(), this.reverse(), this.length() - index).eval();
  }

  private TailCall<Tuple<List<A>, List<A>>> splitAt(List<A> acc, List<A> list, int i) {
    return i == 0 || list.isEmpty()
        ? ret(new Tuple<>(list.reverse(), acc))
        : sus(() -> splitAt(acc.cons(list.head()), list.tail(), i - 1));
  }

  public Tuple<List<A>, List<A>> splitAt_(int index) {
    int ii = index < 0 ? 0 : index >= length() ? length() : index;
    Tuple3<List<A>, List<A>, Integer> identity = new Tuple3<>(List.list(), List.list(), ii);
    Tuple3<List<A>, List<A>, Integer> rt = foldLeft(identity, ta -> a -> ta._3 == 0 ? new Tuple3<>(ta._1, ta._2.cons(a), ta._3) : new Tuple3<>(ta._1.cons(a), ta._2, ta._3 - 1));
    return new Tuple<>(rt._1.reverse(), rt._2.reverse());
  }

  public Tuple<List<A>, List<A>> splitAt__(int index) {
    class Tuple3<T, U, V> {

      public final T _1;
      public final U _2;
      public final V _3;

      public Tuple3(T t, U u, V v) {
        this._1 = Objects.requireNonNull(t);
        this._2 = Objects.requireNonNull(u);
        this._3 = Objects.requireNonNull(v);
      }

      @Override
      public String toString() {
        return String.format("(%s,%s,%s)", _1,  _2, _3);
      }

      @Override
      public boolean equals(Object o) {
        if (!(o.getClass() == this.getClass()))
          return false;
        else {
          @SuppressWarnings("rawtypes")
          Tuple3 that = (Tuple3) o;
          return _3.equals(that._3);
        }
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _1.hashCode();
        result = prime * result + _2.hashCode();
        result = prime * result + _3.hashCode();
        return result;
      }
    }
    Tuple3<List<A>, List<A>, Integer> zero = new Tuple3<>(list(), list(), 0);
    Tuple3<List<A>, List<A>, Integer> identity = new Tuple3<>(list(), list(), index);

    Tuple<Tuple3<List<A>, List<A>, Integer>, List<A>> rt = index <= 0
        ? new Tuple<>(identity, this)
        : foldLeft(identity, zero, ta -> a ->
            ta._3 < 0
                ? ta
                : new Tuple3<>(ta._1.cons(a), ta._2, ta._3 - 1));
    return new Tuple<>(rt._1._1.reverse(), rt._2);
  }

  public <B> Map<B, List<A>> groupByImperative(Function<A, B> f) {
    List<A> workList = this;
    Map<B, List<A>> m = Map.empty();
    while (!workList.isEmpty()) {
      final B k = f.apply(workList.head());
      List<A> rt = m.get(k).getOrElse(list()).cons(workList.head());
      m = m.put(k, rt);
      workList = workList.tail();
    }
    return m;
  }

  public <B> Map<B, List<A>> groupBy(Function<A, B> f) {
    return foldRight(Map.empty(), t -> mt -> {
      final B k = f.apply(t);
      return mt.put(k, mt.get(k).getOrElse(list()).cons(t));
    });
  }

  public boolean forAll(Function<A, Boolean> p) {
    Function<Boolean, Function<A, Boolean>> f = x -> y -> x && p.apply(y);
    return foldLeft(true, false, f)._1;
  }

  public boolean exists(Function<A, Boolean> p) {
    Function<Boolean, Function<A, Boolean>> f = x -> y -> x || p.apply(y);
    return foldLeft(false, true, f)._1;
  }

  public List<List<A>> splitListAt(int i) {
    return splitListAt(list(), this.reverse(), i).eval();
  }

  private TailCall<List<List<A>>> splitListAt(List<A> acc, List<A> list, int i) {
    return i == 0 || list.isEmpty()
        ? ret(List.list(list.reverse(), acc))
        : sus(() -> splitListAt(acc.cons(list.head()), list.tail(), i - 1));
  }

  public List<List<A>> divide(int depth) {
    return this.isEmpty()
        ? list(this)
        : divide(list(this), depth);
  }

  private List<List<A>> divide(List<List<A>> list, int depth) {
    return list.head().length() < depth || depth < 2
        ? list
        : divide(list.flatMap(x -> x.splitListAt(x.length() / 2)), depth / 2);
  }

  public <B> List<Tuple<Result<A>, Result<B>>> zipAll(List<B> s2) {
    return zipWithAll(s2, tuple -> new Tuple<>(tuple._1, tuple._2));
  }

  public <B, C> List<C> zipWithAll(List<B> s2, Function<Tuple<Result<A>, Result<B>>, C> f) {
    Function<Tuple<List<A>, List<B>>, Result<Tuple<C, Tuple<List<A>, List<B>>>>> g = x -> x._1.isEmpty() && x._2.isEmpty()
        ? Result.empty()
        : x._2.isEmpty()
            ? Result.success(new Tuple<>(f.apply(new Tuple<>(Result.success(x._1.head()), Result.empty())), new Tuple<>(x._1.tail(), List.<B> list())))
            : x._1.isEmpty()
                ? Result.success(new Tuple<>(f.apply(new Tuple<>(Result.empty(), Result.success(x._2.head()))), new Tuple<>(List.<A> list(), x._2.tail())))
                : Result.success(new Tuple<>(f.apply(new Tuple<>(Result.success(x._1.head()), Result.success(x._2.head()))), new Tuple<>(x._1.tail(), x._2.tail())));
    return unfold(new Tuple<>(this, s2), g);
  }

  public Result<A> first(Function<A, Boolean> p) {
    return first(this, p).eval().mapFailure(String.format("No element satisfying function %s in list %s", p, this));
  }

  private static <A> TailCall<Result<A>> first(final List<A> list, final Function<A, Boolean> f) {
    if (list.isEmpty()) {
      return ret(Result.<A> failure("Empty list"));
    }
    if (f.apply(list.head())) {
      return ret(Result.success(list.head()));
    } else {
      return sus(() -> first(list.tail(), f));
    }
  }

  public<B> Result<B> parFoldLeft(ExecutorService es, B identity, Function<B, Function<A, B>> f, Function<B, Function<B, B>> m) {
    final int chunks = 1024;
    final List<List<A>> dList = divide(chunks);
    try {
      List<B> result = dList.map(x -> es.submit(() -> x.foldLeft(identity, f))).map(x -> {
        try {
          return x.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      });
      return Result.success(result.foldLeft(identity, m));
    } catch (Exception e) {
      return Result.failure(e.getMessage(), e);
    }
  }

  public <B> Result<List<B>> parMap(ExecutorService es, Function<A, B> g) {
    try {
      return Result.success(this.map(x -> es.submit(() -> g.apply(x))).map(x -> {
        try {
          return x.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      }));
    } catch (Exception e) {
      return Result.failure(e.getMessage(), e);
    }
  }

  public List<A> concat(List<A> list) {
    return concat(this, list);
  }

  public void forEach(Consumer<A> effect) {
    List<A> workList = this;
    while (!workList.isEmpty()) {
      effect.accept(workList.head());
      workList = workList.tail();
    }
  }

  @SuppressWarnings("rawtypes")
  public static final List NIL = new Nil();

  private List() {}

  private static class Nil<A> extends List<A> {

    private Nil() {}

    public A head() {
      throw new IllegalStateException("head called en empty list");
    }

    public List<A> tail() {
      throw new IllegalStateException("tail called en empty list");
    }

    public boolean isEmpty() {
      return true;
    }

    @Override
    public List<A> setHead(A h) {
      throw new IllegalStateException("setHead called en empty list");
    }

    public String toString() {
      return "[NIL]";
    }

    @Override
    public List<A> drop(int n) {
      return this;
    }

    @Override
    public List<A> dropWhile(Function<A, Boolean> f) {
      return this;
    }

    @Override
    public List<A> reverse() {
      return this;
    }

    @Override
    public List<A> init() {
      throw new IllegalStateException("init called on an empty list");
    }

    @Override
    public int length() {
      return 0;
    }

    @Override
    public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
      return identity;
    }

    @Override
    public <B> Tuple<B, List<A>> foldLeft(B identity, B zero, Function<B, Function<A, B>> f) {
      return new Tuple<>(identity, list());
    }

    @Override
    public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
      return identity;
    }

    @Override
    public A reduce(Function<A, Function<A, A>> f) {
      throw new IllegalStateException(
          "Can't reduce and empty list without a zero");
    }

    @Override
    public Result<A> headOption() {
      return Result.empty();
    }

    @Override
    public Result<List<A>> tailOption() {
      return Result.empty();
    }

    @Override
    public String mkStr(String sep) {
      return "";
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Nil;
    }

    @Override
    public <B> Result<List<B>> sequence(Function<A, Result<B>> f) {
      return Result.empty();
    }

    @Override
    protected List<A> take(int n) {
      throw new IllegalStateException("take called on an empty list");
    }

    @Override
    public List<A> takeAtMost(int n) {
      return this;
    }

    @Override
    public List<A> takeWhile(Function<A, Boolean> p) {
      return this;
    }

    @Override
    public List<List<A>> subLists() {
      return list(list());
    }

    @Override
    public List<List<A>> interleave(A a) {
      return list(list(a));
    }

    @Override
    public List<List<A>> perms() {
      return list(list());
    }

    @Override
    public List<Tuple<List<A>, List<A>>> split() {
      return list();
    }

    @Override
    public Result<Tuple<A, List<A>>> headAndTail() {
      return Result.failure("Empty list");
    }

    @Override
    public Stream<A> toStream() {
      return Stream.empty();
    }
  }

  private static class Cons<A> extends List<A> {

    private final A head;
    private final List<A> tail;
    private final int length;

    private Cons(A head, List<A> tail) {
      this.head = head;
      this.tail = tail;
      this.length = tail.length() + 1;
    }

    public A head() {
      return head;
    }

    public List<A> tail() {
      return tail;
    }

    public boolean isEmpty() {
      return false;
    }

    @Override
    public List<A> setHead(A h) {
      return new Cons<>(h, tail());
    }

    public String toString() {
      return String.format("[%sNIL]", toString(new StringBuilder(), this).eval());
    }

    private TailCall<StringBuilder> toString(StringBuilder acc, List<A> list) {
      return list.isEmpty()
          ? ret(acc)
          : sus(() -> toString(acc.append(list.head()).append(", "), list.tail()));
    }

    @Override
    public List<A> drop(int n) {
      return n <= 0
          ? this
          : drop_(this, n).eval();
    }

    private TailCall<List<A>> drop_(List<A> list, int n) {
      return n <= 0 || list.isEmpty()
          ? ret(list)
          : sus(() -> drop_(list.tail(), n - 1));
    }

    @Override
    public List<A> dropWhile(Function<A, Boolean> f) {
      return dropWhile_(this, f).eval();
    }

    private TailCall<List<A>> dropWhile_(List<A> list, Function<A, Boolean> f) {
      return !list.isEmpty() && f.apply(list.head())
          ? sus(() -> dropWhile_(list.tail(), f))
          : ret(list);
    }

    @Override
    public List<A> reverse() {
      return reverse_(list(), this).eval();
    }

    private TailCall<List<A>> reverse_(List<A> acc, List<A> list) {
      return list.isEmpty()
          ? ret(acc)
          : sus(() -> reverse_(new Cons<>(list.head(), acc), list.tail()));
    }

    @Override
    public List<A> init() {
      return reverse().tail().reverse();
    }

    @Override
    public int length() {
      return length;
    }

    @Override
    public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
      return foldLeft(identity, this, f).eval();
    }

    private <B> TailCall<B> foldLeft(B acc, List<A> list, Function<B, Function<A, B>> f) {
      return list.isEmpty()
          ? ret(acc)
          : sus(() -> foldLeft(f.apply(acc).apply(list.head()), list.tail(), f));
    }

    @Override
    public <B> Tuple<B, List<A>> foldLeft(B identity, B zero, Function<B, Function<A, B>> f) {
      return foldLeft(identity, zero, this, f).eval();
    }

    private <B> TailCall<Tuple<B, List<A>>> foldLeft(B acc, B zero, List<A> list, Function<B, Function<A, B>> f) {
      return list.isEmpty() || acc.equals(zero)
          ? ret(new Tuple<>(acc, list))
          : sus(() -> foldLeft(f.apply(acc).apply(list.head()), zero, list.tail(), f));
    }

    @Override
    public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
      return reverse().foldLeft(identity, x -> y -> f.apply(y).apply(x));
    }

    @Override
    public A reduce(Function<A, Function<A, A>> f) {
      return this.tail().foldLeft(this.head(), f);
    }

    @Override
    public Result<A> headOption() {
      return Result.success(head);
    }

    @Override
    public Result<List<A>> tailOption() {
      return Result.success(tail);
    }

    @Override
    public String mkStr(String sep) {
      return head.toString() + tail.foldLeft("", s -> e -> s + sep + e.toString());
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Cons && isEquals((Cons<?>) o);
    }

    private boolean isEquals(Cons<?> o) {
      Function<Result<A>, Function<Result<?>, Boolean>> equals = x -> y -> x.flatMap(a -> y.map(a::equals)).getOrElse(() -> false);
      return zipAll(o).foldRight(true, x -> y -> equals.apply(x._1).apply(x._2));
    }

    public <B> Result<List<B>> sequence(Function<A, Result<B>> f) {
      return sequence(this, f);
    }

    @Override
    protected List<A> take(int n) {
      return this.isEmpty()
          ? this
          : n > 0
              ? new Cons<>(head(), tail().take(n - 1))
              : list();
    }

    @Override
    public List<A> takeAtMost(int n) {
      return n <= length
          ? take(n)
          : this;
    }

    @Override
    public List<A> takeWhile(Function<A, Boolean> p) {
      return isEmpty()
          ? this
          : p.apply(head())
              ? new Cons<>(head(), tail().takeWhile(p))
              : list();
    }

    @Override
    public List<List<A>> subLists() {
      List<List<A>> yss = tail.subLists();
      return yss.concat(yss.map(subList -> subList.cons(head)));
    }

    @Override
    public List<List<A>> interleave(A a) {
      List<List<A>> yss = tail.interleave(a);
      return yss.map(lst -> lst.cons(head)).cons(this.cons(a));
    }

    @Override
    public List<List<A>> perms() {
      return tail.perms().flatMap(lst -> lst.interleave(head));
    }

    @Override
    public List<Tuple<List<A>, List<A>>> split() {
      return tail.isEmpty()
          ? list()
          : split_(tail);
    }

    @Override
    public Result<Tuple<A, List<A>>> headAndTail() {
      return Result.success(new Tuple<>(head, tail));
    }

    @Override
    public Stream<A> toStream() {
      return Stream.cons(() -> head, tail::toStream);
    }

    private List<Tuple<List<A>, List<A>>> split_(List<A> list) {
      List<Tuple<List<A>, List<A>>> yss = list.tail().split();
      return yss.map(t -> new Tuple<>(t._1.cons(head), t._2)).cons(new Tuple<>(list(head), tail));
    }
  }

  @SuppressWarnings("unchecked")
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

  public static <A, B> B foldRight(List<A> list, B n, Function<A, Function<B, B>> f ) {
    return list.foldRight(n, f);
  }

  public static <T> List<T> cons(T t, List<T> list) {
    return list.cons(t);
  }

  public static <A> List<A> concat(List<A> list1, List<A> list2) {
    return foldRight(list1, list2, x -> y -> new Cons<>(x, y));
  }

  public static <A> List<A> flatten(List<List<A>> list) {
    return foldRight(list, List.<A>list(), x -> y -> concat(x, y));
  }

  public static <A> List<A> flattenResult(List<Result<A>> list) {
    return flatten(list.foldRight(list(), x -> y -> y.cons(x.map(List::list).getOrElse(list()))));
  }

  public static <A, B> Result<List<B>> traverse(List<A> list, Function<A, Result<B>> f) {
    return list.foldRight(Result.success(List.list()), x -> y -> Result.map2(f.apply(x), y, a -> b -> b.cons(a)));
  }

  public static <A> Result<List<A>> sequence(List<Result<A>> list) {
    return traverse(list, x -> x);
  }

  public static <U> Supplier<List<U>> lazySequence(final List<Supplier<U>> list) {
    return () -> list.map(Supplier::get);
  }

  public static <T, U> Result<List<U>> sequence(List<T> list, Function<T, Result<U>> f) {
    List<U> result = list();
    List<T> workList = list.reverse();
    while (!workList.isEmpty()) {
      Result<U> ru = f.apply(workList.head());
      if (ru.isSuccess()) {
        result = new Cons<>(ru.successValue(), result);
      } else {
        return Result.failure(ru.failureValue());
      }
      workList = workList.tail();
    }
    return Result.success(result);
  }

  public static <A, B, C> List<C> zipWith(List<A> list1, List<B> list2, Function<A, Function<B, C>> f) {
    return zipWith_(list(), list1, list2, f).eval().reverse();
  }

  private static <A, B, C> TailCall<List<C>> zipWith_(List<C> acc, List<A> list1, List<B> list2, Function<A, Function<B, C>> f) {
    return list1.isEmpty() || list2.isEmpty()
        ? ret(acc)
        : sus(() -> zipWith_(
            new Cons<>(f.apply(list1.head()).apply(list2.head()), acc),
            list1.tail(), list2.tail(), f));
  }

  public static <A, B, C> List<C> product(List<A> list1, List<B> list2, Function<A, Function<B, C>> f) {
    return list1.flatMap(a -> list2.map(b -> f.apply(a).apply(b)));
  }

  public static <A1, A2> Tuple<List<A1>, List<A2>> unzip(List<Tuple<A1, A2>> list) {
    return list.foldRight(new Tuple<>(list(), list()), t -> tl -> new Tuple<>(tl._1.cons(t._1), tl._2.cons(t._2)));
  }

  public static <A> boolean hasSubList(List<A> list, List<A> sub) {
    return hasSubList_(list, sub).eval();
  }

  public static <A> TailCall<Boolean> hasSubList_(List<A> list, List<A> sub) {
    return list.isEmpty()
        ? ret(sub.isEmpty())
        : startsWith(list, sub)
            ? ret(true)
            : sus(() -> hasSubList_(list.tail(), sub));
  }

  public static <A> Boolean startsWith(List<A> list, List<A> sub) {
    return startsWith_(list, sub).eval();
  }

  private static <A> TailCall<Boolean> startsWith_(List<A> list, List<A> sub) {
    return sub.isEmpty()
        ? ret(Boolean.TRUE)
        : list.isEmpty()
            ? ret(Boolean.FALSE)
            : list.head().equals(sub.head())
                ? sus(() -> startsWith_(list.tail(), sub.tail()))
                : ret(Boolean.FALSE);
  }

  /**
   * Caution: not stack safe
   */
  public static <A, S> List<A> unfold_(S z, Function<S, Result<Tuple<A, S>>> f) {
    return f.apply(z).map(x -> unfold_(x._2, f).cons(x._1)).getOrElse(list());
  }

  public static <A, S> List<A> unfold(S z, Function<S, Result<Tuple<A, S>>> f) {
    return unfold(list(), z, f).eval().reverse();
  }

  private static <A, S> TailCall<List<A>> unfold(List<A> acc, S z, Function<S, Result<Tuple<A, S>>> f) {
    Result<Tuple<A, S>> r = f.apply(z);
    Result<TailCall<List<A>>> result = r.map(rt -> sus(() -> unfold(acc.cons(rt._1), rt._2, f)));
    return result.getOrElse(ret(acc));
  }

  public static List<Integer> range(int start, int end) {
    return List.unfold(start, i -> i < end
        ? Result.success(new Tuple<>(i, i + 1))
        : Result.empty());
  }

  public static <A> List<A> fill(int n, Supplier<A> s) {
    return range(0, n).map(ignore -> s.get());
  }

  public static <T> List<T> fromCollection(Collection<T> ct) {
    List<T> lt = list();
    for (T t : ct) {
      lt = lt.cons(t);
    }
    return lt.reverse();
  }

  public static Result<Integer> maxOption(List<Integer> list) {
    return list.isEmpty()
        ? Result.empty()
        : Result.success(list.tail().foldRight(list.head(), x -> y -> x > y ? x : y));
  }

  public static Result<Integer> minOption(List<Integer> list) {
    return list.isEmpty()
        ? Result.empty()
        : Result.success(list.tail().foldRight(list.head(), x -> y -> x < y ? x : y));
  }

  /*
   * A special version of max, throwing an exception if the list is empty. This version
   * is used in chapter 10.
   */
  public static int max(List<Integer> list) {
    return list.tail().foldRight(list.head(), x -> y -> x > y ? x : y);
  }

  public static int min(List<Integer> list) {
    return list.tail().foldRight(list.head(), x -> y -> x < y ? x : y);
  }

  public static List<String> fromSeparatedString(String string, char separator) {
    return List.fromCollection(Arrays.asList(string.split("\\s*" + separator + "\\s*")));
  }

  public java.util.List<A> toJavaList() {
    java.util.List<A> s = new ArrayList<>();
    List<A> workList = this;
    while (!workList.isEmpty()) {
      s.add(workList.head());
      workList = workList.tail();
    }
    return s;
  }

  public static List<String> words(String s) {
    byte[] bytes = s.getBytes();
    StringBuffer sb = new StringBuffer();
    java.util.List<String> result = new ArrayList<>();
    for (byte aByte : bytes) {
      if (aByte == 32 && sb.length() != 0) {
        result.add(sb.toString());
        sb = new StringBuffer();
      } else {
        sb.append((char) aByte);
      }
    }
    result.add(sb.toString());
    return fromCollection(result);
  }

}
