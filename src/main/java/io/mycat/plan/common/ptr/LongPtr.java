package io.mycat.plan.common.ptr;

public class LongPtr {
	private long l;

	public LongPtr(long l) {
		this.l = l;
	}

	public long get() {
		return l;
	}

	public void set(long l) {
		this.l = l;
	}

	public long decre() {
		return l--;
	}

	public long incre() {
		return l++;
	}
}