package com.godlike.algorithm.exception;

public class MyException extends Exception {
	public static enum Type{
		NO_BLANK_POSITON,
		NO_THIS_TYPE_CBLOCK_IN_STORAGE
	}

	public MyException(String arg0) {
		super(arg0);
	}
	
}
