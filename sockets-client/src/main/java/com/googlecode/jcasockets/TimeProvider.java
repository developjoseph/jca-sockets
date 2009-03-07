package com.googlecode.jcasockets;

public interface TimeProvider {
	static TimeProvider DEFAULT = new TimeProvider(){
		@Override
		public long nanoTime() {
			return System.nanoTime();
		}};
	long nanoTime();
}
