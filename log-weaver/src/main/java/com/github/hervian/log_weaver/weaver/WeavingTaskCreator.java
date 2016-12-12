package com.github.hervian.log_weaver.weaver;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.github.hervian.log_weaver.LogAfter;
import com.github.hervian.log_weaver.LogAround;
import com.github.hervian.log_weaver.LogBefore;import com.github.hervian.log_weaver.ap.AnnotationUtil;
import com.github.hervian.log_weaver.exceptions.AbstractMethodException;
import com.github.hervian.log_weaver.exceptions.AnonynmousClassException;
import com.github.hervian.log_weaver.exceptions.InvalidLogParametersException;
import com.github.hervian.log_weaver.exceptions.MultipleLogAnnotationsException;
import com.github.hervian.log_weaver.weaver.tasks.LogAfterWeavingTask;
import com.github.hervian.log_weaver.weaver.tasks.LogAroundWeavingTask;
import com.github.hervian.log_weaver.weaver.tasks.LogBeforeWeavingTask;
import com.github.hervian.log_weaver.weaver.tasks.WeavingTask;

public enum WeavingTaskCreator {
	INSTANCE;
	
  //Unfortunately annotations cannot implement interfaces or form inheritance, for which reason we need code like below
	public WeavingTask createWeavingTask(Element element) throws MultipleLogAnnotationsException, InvalidLogParametersException, AbstractMethodException, AnonynmousClassException{
		Annotation logAnnotation = getLogAnnotation(element);
		if (isAnonymous(element)){ //Currently this does not seem necessary, as annotated methods inside anonoymous classes are not visible/not given to the AbstractProcessor?!
			throw new AnonynmousClassException(logAnnotation);
		}
		if (isAbstract(element)){
			throw new AbstractMethodException(logAnnotation);
		}
		if (logAnnotation instanceof LogAround){
			return createLogAroundWeavingTask((ExecutableElement)element, (LogAround) logAnnotation);
		}
		if (logAnnotation instanceof LogBefore){
			return createLogBeforeWeavingTask((ExecutableElement)element, (LogBefore) logAnnotation);
		}
		return createLogAfterWeavingTask((ExecutableElement)element, (LogAfter) logAnnotation);
	}
	
	private boolean isAnonymous(Element element) {
		String fqcn = AnnotationUtil.INSTANCE.getFullyQualifiedClassName(element);
		System.out.println("fqcn='"+fqcn+"'");
		return fqcn==null || fqcn.trim().isEmpty();
	}

	private boolean isAbstract(Element element) {
		boolean isAbstract = false;
		for (javax.lang.model.element.Modifier modifier : element.getModifiers()){
			if (modifier.equals(javax.lang.model.element.Modifier.ABSTRACT)){
				isAbstract = true;
				break;
			}
		}
		return isAbstract;
	}
	
	private Annotation getLogAnnotation(Element element) throws MultipleLogAnnotationsException{
		LogAround logAround = element.getAnnotation(LogAround.class);
		LogBefore logBefore = element.getAnnotation(LogBefore.class);
		LogAfter logAfter = element.getAnnotation(LogAfter.class);
		
		Annotation logAnnotation = null;
		if (logAround!=null || logBefore!=null){
			if (logAfter!=null || (logAround!=null && logBefore!=null)){
				throw new MultipleLogAnnotationsException(logAfter==null ? logBefore : logAfter);
			}
			logAnnotation = logAround==null ? logBefore : logAround;
		} else {
			logAnnotation = logAfter;
		}
		return logAnnotation;
	}

	private LogAroundWeavingTask createLogAroundWeavingTask(ExecutableElement element, LogAround log) throws InvalidLogParametersException {
		Map<String, VariableElement> mapOfParams = getArgumentsToLogAndValidateAgainstAvailableParameters(element, log.value(), log);
		return new LogAroundWeavingTask(element.getSimpleName().toString(), mapOfParams, log, element);
	}
	
	private LogBeforeWeavingTask createLogBeforeWeavingTask(ExecutableElement element, LogBefore log) throws InvalidLogParametersException {
		Map<String, VariableElement> mapOfParams = getArgumentsToLogAndValidateAgainstAvailableParameters(element, log.value(), log);
		return new LogBeforeWeavingTask(element.getSimpleName().toString(), mapOfParams, log, element);
	}
	
	private LogAfterWeavingTask createLogAfterWeavingTask(ExecutableElement element, LogAfter log) {
		return new LogAfterWeavingTask(element.getSimpleName().toString(), element);
	}

	private Map<String, VariableElement> getArgumentsToLogAndValidateAgainstAvailableParameters(ExecutableElement element, String[] arguments, Annotation annotation) throws InvalidLogParametersException {
		List<? extends VariableElement> params = element.getParameters();
		Map<String, VariableElement> mapOfParams = new LinkedHashMap<String, VariableElement>();
		for (VariableElement param : params){
			mapOfParams.put(param.getSimpleName().toString(), param);
		}
		validateArgsToLogAgainstAvailableParameters(mapOfParams, arguments, element, annotation);
		return mapOfParams;
	}

	private void validateArgsToLogAgainstAvailableParameters(Map<String, VariableElement> setOfParams, String[] arguments, ExecutableElement element, Annotation annotation) throws InvalidLogParametersException {
		for (String str : arguments){
			if (!setOfParams.containsKey(str)){
				if (str.trim().isEmpty() || (str.equals("this") && !element.getModifiers().contains(javax.lang.model.element.Modifier.STATIC))){
					continue;
				}
				System.out.println("setOfParams does not contain arg: '"+str+"'");
				throw new InvalidLogParametersException(annotation);
			}
		}
	}

	
}
