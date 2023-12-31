public class MyExchanger<T> {
  private T value1 = null;
  private T value2 = null;
  private boolean isSecond; // False
  private boolean secondValueHere; // False
  private final Object lock = new Object();

  public T exchange(T value) throws InterruptedException {
    synchronized (lock) {
      if(!isSecond) {
        value1 = value;
        isSecond = true;

        // Always a while around a wait, or else it can be woke up for the wrong reason
        // Nothing else besides the wait inside the loop
        while(!secondValueHere) {
          lock.wait();
        }
        return value2;
      } else {
        value2 = value;
        secondValueHere = true;
        lock.notify();
        return value1;
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var exchanger = new MyExchanger<String>();

    Thread.ofPlatform().start(() -> {
      try {
        System.out.println("thread 1 " + exchanger.exchange("foo1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println("main " + exchanger.exchange(null));
  }
}
