package com.github.hervian.log_weaver.exceptions;

import java.lang.annotation.Annotation;

public abstract class LogAnnotationException extends Exception {
	
	private static final long serialVersionUID = 2288570582177370298L;
	
	private Annotation annotation;
	
	public LogAnnotationException(Annotation annotation, String msg){
		super(msg);
		this.annotation = annotation;
	}
	
	
	public Class<? extends Annotation> getAnnotationClass() {
		return annotation.getClass();
	}
	
	
}
