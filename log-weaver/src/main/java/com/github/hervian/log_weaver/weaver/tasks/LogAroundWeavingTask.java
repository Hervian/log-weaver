package com.github.hervian.log_weaver.weaver.tasks;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.github.hervian.log_weaver.LogAround;

public class LogAroundWeavingTask extends WeavingTask implements ArgumentsToLogProvider {

	private final LogAround log;
	
	public LogAroundWeavingTask(String methodName, Map<String, VariableElement> params, LogAround log, Element element){
		super(methodName, params, element);
		this.log = log;
	}

	@Override
	public boolean insertBefore() {
		return true;
	}

	@Override
	public boolean insertAfter() {
		return true;
	}
	
	LogAround getLog() {
		return log;
	}
	
	@Override
	public String getArgsToLog() {
		return ArgumentsUtil.INSTANCE.getArgsToLog(getLog().value(), getParams());
	}

	@Override
	public Class<? extends Annotation> getLogAnnotationClass() {
		return LogAround.class;
	}
	
}
