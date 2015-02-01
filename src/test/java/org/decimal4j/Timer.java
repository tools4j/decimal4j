package org.decimal4j;

public class Timer {
	private final long[] times;
	private long start;
	private int index = 0;
	public Timer(int n) {
		times = new long[n];
	}
	
	public void start() {
		start(System.currentTimeMillis());
	}
	private void start(long start) {
		this.start = start;
	}
	public void stop() {
		stop(System.currentTimeMillis());
	}
	private void stop(long end) {
		times[index] += (end - start);
	}
	public void stopAndNext() {
		stop();
		index++;
	}
	public void stopAndNextStart() {
		final long time = System.currentTimeMillis();
		stop(time);
		index++;
		start(time);
	}
	public long getTimeMillis(int index) {
		return times[index];
	}
	public void first() {
		index = 0;
	}
	public void firstAndStart() {
		index = 0;
		start();
	}
	public void next() {
		index++;
	}
	public void nextAndStart() {
		index++;
		start();
	}
	
	public void reset() {
		for (int i = 0; i < times.length; i++) {
			times[i] = 0;
		}
		index = 0;
	}
	
}
