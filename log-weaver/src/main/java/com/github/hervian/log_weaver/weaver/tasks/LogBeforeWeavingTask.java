package com.github.hervian.log_weaver.weaver.tasks;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import com.github.hervian.log_weaver.LogBefore;

public class LogBeforeWeavingTask extends WeavingTask implements ArgumentsToLogProvider {

	private LogBefore log;
	
	public LogBeforeWeavingTask(String methodName, Map<String, VariableElement> params, LogBefore log, Element element){
		super(methodName, params, element);
		this.log = log;
	}

	@Override
	public boolean insertBefore() {
		return true;
	}

	@Override
	public boolean insertAfter() {
		return false;
	}
	
	LogBefore getLog() {
		return log;
	}
	
	@Override
	public String getArgsToLog() {
		return ArgumentsUtil.INSTANCE.getArgsToLog(getLog().value(), getParams());
	}

	@Override
	public Class<? extends Annotation> getLogAnnotationClass() {
		return LogBefore.class;
	}
	
}
