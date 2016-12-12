package com.github.hervian.log_weaver.weaver.tasks;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * Each use of one of the Log-annotations ({@link LogAround}, {@link LogBefore}, {@link LogAfter})
 * will result in a WeavingTask.
 *   
 * @author Anders Granau Høfft
 *
 */
public abstract class WeavingTask {

//	private String fullyQualifiedClassName;
	private final String methodName;
	private final Map<String, VariableElement> params; //VariableElement.asType().toString()==Fully Qualified Class Name.
	private Element element;
	
	WeavingTask(String methodName, Map<String, VariableElement> params, Element element){
		this.methodName = methodName;
		this.params = params;
		this.element = element;
	}
	
	public abstract Class<? extends Annotation> getLogAnnotationClass();
	
	public String getMethodName(){
		return methodName;
	}
	
	public Map<String, VariableElement> getParams(){
		return params;
	}

	public abstract boolean insertBefore();

	public abstract boolean insertAfter();


	public Element getElement() {
		return element;
	}

	
}
