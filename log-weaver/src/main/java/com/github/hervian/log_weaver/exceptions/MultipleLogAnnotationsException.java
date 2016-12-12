package com.github.hervian.log_weaver.exceptions;

import java.lang.annotation.Annotation;

public class MultipleLogAnnotationsException extends LogAnnotationException {
	private static final long serialVersionUID = 2947625059865107469L;

	public MultipleLogAnnotationsException(Annotation annotation){
		super(annotation, "At most one @LogBefore/LogAround/LogAfter annotation allowed per method.");
	}

}
