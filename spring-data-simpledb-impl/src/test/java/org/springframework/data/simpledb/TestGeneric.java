package org.springframework.data.simpledb;

import java.util.List;

public class TestGeneric {

	static abstract class X {
		public abstract <T> List<T> generate();
	}
	
}
