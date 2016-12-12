package com.github.hervian.log_weaver.exceptions;

import java.lang.annotation.Annotation;

public class AnonynmousClassException extends LogAnnotationException {

	private static final long serialVersionUID = -4035266928800052339L;

	public AnonynmousClassException(Annotation annotation) {
		super(annotation, "Anonymous classes are not supported for @LogAround/-Before/-After annotations");
	}

}
