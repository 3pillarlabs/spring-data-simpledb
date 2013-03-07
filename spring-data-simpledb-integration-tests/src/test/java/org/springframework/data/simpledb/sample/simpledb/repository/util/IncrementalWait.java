package org.springframework.data.simpledb.sample.simpledb.repository.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IncrementalWait<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IncrementalWait.class);

	public static final int MAX_RETRIES = 50;
	public static final int INCREEMENT_WAIT_MILLIS = 500;

	public T execute() {
		return null;
	}

	public boolean condition(T t) {
		return true;
	}

	public boolean condition() {
		return false;
	}

	public void untilResponseNull() {
		T ret = execute();
		int retries = 0;
		while(ret != null && retries < MAX_RETRIES) {
			ret = execute();
			retries++;
			try {
				Thread.currentThread().sleep(INCREEMENT_WAIT_MILLIS);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		LOGGER.debug("Retries: {}", retries);
	}

	public void untilResponseNotNull() {
		T ret = null;
		int retries = 0;
		while(ret == null && retries < MAX_RETRIES) {
			ret = execute();
			retries++;
			try {
				Thread.currentThread().sleep(INCREEMENT_WAIT_MILLIS);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void untilResponseSatisfiesCondition() {
		T ret = null;
		int retries = 0;
		while((ret == null || !condition(ret)) && retries < MAX_RETRIES) {
			ret = execute();
			retries++;
			try {
				Thread.currentThread().sleep(INCREEMENT_WAIT_MILLIS);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void untilCondition() {
		int retries = 0;
		while(condition() && retries < MAX_RETRIES) {
			retries++;
			try {
				Thread.currentThread().sleep(INCREEMENT_WAIT_MILLIS);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
