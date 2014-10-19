package net.batkin.rabbitmq.util;

public class Stopwatch {
	private long startTime;
	private long lastTime;

	public Stopwatch() {
		startTime = System.currentTimeMillis();
		lastTime = startTime;
	}

	public void click(String message) {
		long now = System.currentTimeMillis();
		long fromStart = now - startTime;
		long fromLast = now - lastTime;
		lastTime = now;

		System.err.println("Stopwatch [" + message + "] from Last [" + fromLast + "] from Start [" + fromStart + "]");
	}

	public void adjust(long amount) {
		startTime += amount;
		lastTime += amount;
	}
}