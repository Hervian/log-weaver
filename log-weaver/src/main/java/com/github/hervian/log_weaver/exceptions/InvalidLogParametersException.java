package com.github.hervian.log_weaver.exceptions;

import java.lang.annotation.Annotation;

public class InvalidLogParametersException extends LogAnnotationException {

	private static final long serialVersionUID = -79139014862542623L;

	public InvalidLogParametersException(Annotation annotation){
		super(annotation, "Can only log arguments and 'this'.");
	}

	
}
