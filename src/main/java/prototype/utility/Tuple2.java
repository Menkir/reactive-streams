package prototype.utility;

public class Tuple2<T,E> {
	private T t;
	private E e;

	public Tuple2(T t, E e) {
		this.t = t;
		this.e = e;
	}

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	public E getE() {
		return e;
	}

	public void setE(E e) {
		this.e = e;
	}
}
