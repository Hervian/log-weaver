package com.github.hervian.log_weaver.weaver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import com.github.hervian.log_weaver.ap.AnnotationUtil;
import com.github.hervian.log_weaver.exceptions.GenericLogWeavingException;
import com.github.hervian.log_weaver.weaver.tasks.ArgumentsToLogProvider;
import com.github.hervian.log_weaver.weaver.tasks.WeavingTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * This class is responsible for weaving the log-statements and related fields into the byte code.
 * 
 * @author Anders Granau Høfft
 *
 */
public class LogWeaver implements TaskListener {

	private static final String packagePrefix = "comGithubHervian"; //"Salt" to ensure that the class variable names will not collide with any other in the class
	private static final String METHODNAME = packagePrefix+"_METHODNAME";
	private static final String CLASSNAME = packagePrefix+"_CLASSNAME";
	private static final String LOGGER = packagePrefix+"_LOGGER";
	
	private static final String[] primitives = new String[]{"boolean", "char", "long", "double", "int", "float", "short", "byte"};
	private final Map<String, List<WeavingTask>> mapFromFqcnToWeavingTasks;
	private Messager messager;
	
	public LogWeaver(Map<String, List<WeavingTask>> mapFromFqcnToWeavingTasks, Messager messager){
		this.mapFromFqcnToWeavingTasks = mapFromFqcnToWeavingTasks;
		this.messager = messager;
		System.out.println("LogWeaver task listener has been constructed.");
	}
	
	@Override
	public void started(TaskEvent arg0) {}

	@Override
	public void finished(TaskEvent taskEvt) {
		if (taskEvt.getKind() == TaskEvent.Kind.GENERATE) {
			try {
				weaveAspectIntoClassFile(taskEvt);
			} catch (GenericLogWeavingException e) {
				for (WeavingTask weavingTask : e.getWeavingTasks()){
					AnnotationMirror annoMirror = AnnotationUtil.INSTANCE.findAnnotationMirror(weavingTask.getElement(), weavingTask.getLogAnnotationClass());
					messager.printMessage(Kind.ERROR, "Unexpected error: "+e.getMessage(), weavingTask.getElement(), annoMirror);
				}
			}
		}
	}
	
	//TODO: Is anonoymous classes with annotated methods supported?
	private void weaveAspectIntoClassFile(TaskEvent taskEvt) throws GenericLogWeavingException {
		List<WeavingTask> weavingTasks = mapFromFqcnToWeavingTasks.get(taskEvt.getTypeElement().toString());
		if (weavingTasks!=null){			
			try {
				boolean isInterface = taskEvt.getTypeElement().getKind().isInterface();
				
				ClassPool pool = ClassPool.getDefault();
				
				Class<?> clazz = getTargetClass(taskEvt.getTypeElement().toString());
				insertClassPathsIntoClassPool(pool, clazz);
			  
				String className = clazz.getName();		
				CtClass cc = pool.get(className); 
				cc.defrost(); //Perhaps unnecessary, but would it be necessary if another bytecode weaving project had already worked on the file?
				
				weaveFieldsIntoClass(isInterface, clazz, pool, cc);				
				weavingLogStatementsIntoMethods(weavingTasks, pool, cc, isInterface);
				
				cc.writeFile(getDirectory(cc));
			} catch (Throwable e) {
				String message = e.getClass().getSimpleName() + ", " + taskEvt.getTypeElement() + " does not have that method.";
				throw new GenericLogWeavingException(weavingTasks, message);
			}
		}
	}

	private void insertClassPathsIntoClassPool(ClassPool pool, Class<?> clazz) {
		pool.insertClassPath(new ClassClassPath(clazz));
	}

	/**
	 * Since taskEvt.getTypeElement().toString() will return a fully qualified class name with pure dot-separator
	 * we must handle inner/nested classes separately, as they should use $ insted of dot.
	 * This is because Class.forName expects this.
	 */
	private Class<?> getTargetClass(String fqcn) throws ClassNotFoundException {
		Class<?> clazz = null;
		try {			
			clazz = Class.forName(fqcn); //Lets first assume that the class is an outer class
		} catch (ClassNotFoundException e){ //Perhaps it is an inner/nested class, in which case we must replace the last dot(s) with $ (sub.package.OuterClass.InnerCLass should be sub.package.OuterClass$InnerClass) 
			List<Integer> indexes = getIndexesOfDots(fqcn);		
			for (int i=indexes.size()-1; i>=0; i--){
				fqcn = replaceCharAt(fqcn, indexes.get(i), '$');
				try {			
					clazz = Class.forName(fqcn); //Lets first assume that the class is an outer class
					break;
				} catch (ClassNotFoundException e2){
					if (i==0){
						throw e;
					}
				}
			}
		}
		return clazz;
	}
	
	private String replaceCharAt(String str, int i, char c) {
		String result = str;
		if (str!=null && i<=str.length()-1){
			result = str.substring(0, i) + c + str.substring(i+1);
		}
		return result;
	}

	private List<Integer> getIndexesOfDots(String fqcn){
		List<Integer> indexes = new ArrayList<>();
		for (int i=0; i<fqcn.length(); i++){
			if (fqcn.charAt(i)=='.'){
				indexes.add(i);
			}
		}
		return indexes;
	}

	private void weaveFieldsIntoClass(boolean isInterface, Class<?> clazz, ClassPool pool, CtClass cc) throws CannotCompileException, NotFoundException {
		CtField loggerField = new CtField(pool.get("java.util.logging.Logger"), LOGGER, cc);
		makePrivateStaticFinal(isInterface, loggerField);
		cc.addField(loggerField, "com.ibm.commerce.foundation.common.util.logging.LoggingHelper.getLogger("+clazz.getName()+".class)");
		
		CtField classNameField = new CtField(pool.get("java.lang.String"), CLASSNAME, cc);
		makePrivateStaticFinal(isInterface, classNameField);
		cc.addField(classNameField, clazz.getName()+".class.getName()");
	}

	private void weavingLogStatementsIntoMethods(List<WeavingTask> weavingTasks, ClassPool pool, CtClass cc, boolean isInterface) throws ClassNotFoundException, NotFoundException, CannotCompileException {
		for (int j=0; j<weavingTasks.size(); j++) {
			WeavingTask weavingTask = weavingTasks.get(j);
			List<CtClass> paramsAsCtClasses = getMethodParametersAsCtClasses(pool, weavingTask);		
			CtMethod m = paramsAsCtClasses == null ? cc.getDeclaredMethod(weavingTask.getMethodName()) : cc.getDeclaredMethod(weavingTask.getMethodName(), paramsAsCtClasses.toArray(new CtClass[paramsAsCtClasses.size()]));

			
			CtField methodNameField = new CtField(pool.get("java.lang.String"), METHODNAME+"_"+j, cc);
			makePrivateStaticFinal(isInterface, methodNameField);
			cc.addField(methodNameField, "\""+weavingTask.getMethodName()+"\"");
				
			if (weavingTask.insertBefore()){
				insertBefore(j, m, ((ArgumentsToLogProvider)weavingTask).getArgsToLog());
			}
			if (weavingTask.insertAfter()){			
				insertAfter(j, m);	
			}
		}
	}

	private void makePrivateStaticFinal(boolean isInterface, CtField methodNameField) {
		int publicOrPrivate = isInterface ? Modifier.PUBLIC : Modifier.PRIVATE;
		methodNameField.setModifiers(publicOrPrivate | Modifier.STATIC | Modifier.FINAL);
	}

	private List<CtClass> getMethodParametersAsCtClasses(ClassPool pool, WeavingTask weavingTask) throws ClassNotFoundException, NotFoundException {
		List<CtClass> paramsAsCtClasses = null;
		if (!weavingTask.getParams().isEmpty()) {
			paramsAsCtClasses = new ArrayList<CtClass>();
			for (Entry<String, VariableElement> paramEntry : weavingTask.getParams().entrySet()) {
				CtClass paramAsCtClass = getParameterAsCtClass(pool, paramEntry);
				paramsAsCtClasses.add(paramAsCtClass);
			}
		}
		return paramsAsCtClasses;
	}

	private CtClass getParameterAsCtClass(ClassPool pool, Entry<String, VariableElement> paramEntry) throws ClassNotFoundException, NotFoundException {
		String paramClassName = paramEntry.getValue().asType().toString();
		String fqcn = paramClassName.replace("[", "").replace("]", "");
		if (!paramEntry.getValue().asType().getKind().isPrimitive() && isNotPrimitive(fqcn)){			
			Class<?> paramClass = Class.forName(fqcn);
			insertClassPathsIntoClassPool(pool, paramClass);
		}
		return pool.get(paramClassName);
	}

	private boolean isNotPrimitive(String fqcn) {
		return !Arrays.stream(primitives).anyMatch(fqcn::equals);
	}

	/**
	 * TODO: If the method is void we should ideally call LOGGER.exiting(CLASSNAME, METHODNAME); i.e. without any return value
	 */
	private void insertAfter(int j, CtMethod m) throws CannotCompileException {
		m.insertAfter(
				"{"
				+ "if (com.ibm.commerce.foundation.common.util.logging.LoggingHelper.isEntryExitTraceEnabled("+LOGGER+")) { "
					+ LOGGER+".exiting("+CLASSNAME+","+METHODNAME+"_"+j+", ($w)$_);"
				+ "}"
			+ "}");
	}

	/**
	 * Please be aware that the arguments inserted into the Object array MUST be Objects.
	 * That is, in source code you can rely on automatic wrapping of primitives. 
	 * But if you create a String, where primitives are inserted into the Object array
	 * Javassist will for some reason create byte code, which when executed throws
	 * an error a la "VerifyError: Bad type of operand on stack". 
	 * In other words: The parameters argsToLog (which is a commaseparated string of arguments)
	 * Must only contain Objects, that is Float.valueOf(someFloat) instead of someFloat.
	 * Currently this normalization is handled in ArgumensUtil, but this is a bit ugly.
	 * See also comments in ArgumentsUtil.
	 */
	private void insertBefore(int j, CtMethod m, String argsToLog) throws CannotCompileException {
		if (argsToLog==null || argsToLog.trim().isEmpty()){
			m.insertBefore(
					"{"
						+ "if (com.ibm.commerce.foundation.common.util.logging.LoggingHelper.isEntryExitTraceEnabled("+LOGGER+")) { "
							+ LOGGER+".entering("+CLASSNAME+","+METHODNAME+"_"+j+");"
						+ "}"
				+ "}");
		} else {
			m.insertBefore(
					"{"
						+ "if (com.ibm.commerce.foundation.common.util.logging.LoggingHelper.isEntryExitTraceEnabled("+LOGGER+")) { "
							+ LOGGER+".entering("+CLASSNAME+","+METHODNAME+"_"+j+", new Object[]{"+argsToLog+"});"
						+ "}"
				+ "}");
		}
	}

	private String getDirectory(CtClass cc) throws NotFoundException {
		String pathWithClassName = cc.getURL().getPath();
		return pathWithClassName.substring(0, pathWithClassName.length() - cc.getName().length() - ".class".length());
	}
	
}

