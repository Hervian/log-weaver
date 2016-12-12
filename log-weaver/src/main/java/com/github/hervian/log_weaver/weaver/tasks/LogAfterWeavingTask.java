package com.github.hervian.log_weaver.weaver.tasks;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.github.hervian.log_weaver.LogAfter;

public class LogAfterWeavingTask extends WeavingTask {

	public LogAfterWeavingTask(String methodName, Element element){
		super(methodName, new HashMap<String, VariableElement>(), element);
	}

	@Override
	public boolean insertBefore() {
		return false;
	}

	@Override
	public boolean insertAfter() {
		return true;
	}

	@Override
	public Class<? extends Annotation> getLogAnnotationClass() {
		return LogAfter.class;
	}
	
}
