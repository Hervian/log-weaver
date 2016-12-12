package com.github.hervian.log_weaver.exceptions;

import java.lang.annotation.Annotation;

public class AbstractMethodException extends LogAnnotationException {

	private static final long serialVersionUID = -382842190189140686L;

	public AbstractMethodException(Annotation annotation) {
		super(annotation, "Abstract methods cannot be LogXXX annotated.");
	}

}
