package com.github.hervian.log_weaver.ap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.github.hervian.log_weaver.LogExiting;
import com.github.hervian.log_weaver.LogEnteringAndExiting;
import com.github.hervian.log_weaver.LogEntering;
import com.github.hervian.log_weaver.exceptions.InvalidLogParametersException;
import com.github.hervian.log_weaver.exceptions.LogAnnotationException;
import com.github.hervian.log_weaver.weaver.LogWeaver;
import com.github.hervian.log_weaver.weaver.WeavingTaskCreator;
import com.github.hervian.log_weaver.weaver.tasks.WeavingTask;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LogProcessor extends AbstractProcessor {
	
	Messager messager;
	public Map<String, List<WeavingTask>> mapFromFqcnToWeavingTasks = new HashMap<String, List<WeavingTask>>();
	
	@Override
	public void init(ProcessingEnvironment processingEnv) {
		System.out.println("init: "+processingEnv.getClass().getName());
		messager = processingEnv.getMessager();
		/*
		 * TODO: Check that JavacTask is accessible, and if not ( Maybe the compilation is done in Eclipse and no Checker Framework Plugin has been installed), 
		 * 			 set some flag and print a warning on all log annotations informing that no weaving will be performed.
		 */
		com.sun.source.util.JavacTask.instance(processingEnv).setTaskListener(new LogWeaver(mapFromFqcnToWeavingTasks, messager));
		System.out.println("TaskListener has been registered.");
	}
	
	@Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotataions = new LinkedHashSet<String>();
    annotataions.add(LogEntering.class.getCanonicalName());
    annotataions.add(LogEnteringAndExiting.class.getCanonicalName());
    annotataions.add(LogExiting.class.getCanonicalName());
    return annotataions;
  }

	//TODO: Test that the error msg is printed correct location
	/**
	 * Please note that anonymous methods are currently unsupported, i.e. no log statements will be weaved. 
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			Set<ExecutableElement> annotatedElements = getLogAnnotatedElements(roundEnv);
			System.out.println("Processing "+annotatedElements.size()+" log annotations (LogAround, LogBefore and LogAfter)..");
			traverseAnnotationsAndCreateWeavingTasks(annotatedElements);
		}
		return false;
	}

	private void traverseAnnotationsAndCreateWeavingTasks(Set<ExecutableElement> annotatedElements) {
		for (Element element : annotatedElements) {
			if (element.getKind() == ElementKind.METHOD) { //Redundant check just to be sure
				WeavingTask weavingTasks;
				try {
					weavingTasks = WeavingTaskCreator.INSTANCE.createWeavingTask(element);
				} catch (InvalidLogParametersException e) {
					AnnotationMirror annoMirror = AnnotationUtil.INSTANCE.findAnnotationMirror(element, e.getAnnotationClass());
					AnnotationValue annovalue = AnnotationUtil.INSTANCE.findAnnotationvalue(annoMirror, "value");
					messager.printMessage(Kind.ERROR, e.getMessage(), element, annoMirror, annovalue);
					continue;
				} catch (LogAnnotationException e) {
					AnnotationMirror annoMirror = AnnotationUtil.INSTANCE.findAnnotationMirror(element, e.getAnnotationClass());
					messager.printMessage(Kind.ERROR, e.getMessage(), element, annoMirror);
					continue;
				} 
				addWeavingTaskToMap((ExecutableElement) element, weavingTasks);
			} else {
				System.out.println("An non-method element was annotated with LogXXX annotations.");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Set<ExecutableElement> getLogAnnotatedElements(RoundEnvironment roundEnv) {
		Set<ExecutableElement> annotatedElements = (Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(LogEnteringAndExiting.class);
		annotatedElements.addAll((Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(LogEntering.class));
		annotatedElements.addAll((Set<ExecutableElement>) roundEnv.getElementsAnnotatedWith(LogExiting.class));
		return annotatedElements;
	}

	private void addWeavingTaskToMap(ExecutableElement element, WeavingTask weavingTask) {
		List<WeavingTask> weavingTasks = getListOfWeavingTasksForClass(element);
		weavingTasks.add(weavingTask);
	}
	
	private List<WeavingTask> getListOfWeavingTasksForClass(ExecutableElement element) {
		String fqcn = AnnotationUtil.INSTANCE.getFullyQualifiedClassName(element);
		List<WeavingTask> weavingTasks = mapFromFqcnToWeavingTasks.get(fqcn);
		if (weavingTasks==null){
			weavingTasks = new LinkedList<WeavingTask>();
			mapFromFqcnToWeavingTasks.put(fqcn, weavingTasks);
		}
		return weavingTasks;
	}
	

}
