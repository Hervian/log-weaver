package com.github.hervian.log_weaver.ap;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map.Entry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.sun.javadoc.Type;

public enum AnnotationUtil {
	INSTANCE;
	
	public String getFullyQualifiedClassName(Element element){
		Element typeElement = element;
		while (typeElement.getKind()!=ElementKind.CLASS && typeElement.getKind()!=ElementKind.INTERFACE){
			typeElement = element.getEnclosingElement();
		}
		return ((TypeElement)typeElement).getQualifiedName().toString();
	}
	
	AnnotationValue findAnnotationvalue(AnnotationMirror annoMirror, String annotatonTypeName) {
		AnnotationValue annoValue = null;
		if (annoMirror != null) {
			for (Entry<? extends ExecutableElement, ? extends AnnotationValue> e : annoMirror.getElementValues().entrySet()) {
				if (annotatonTypeName.equals(e.getKey().getSimpleName().toString())) {
					annoValue = e.getValue();
				}
			}
		}
		return annoValue;
	}

	public AnnotationMirror findAnnotationMirror(Element annotatedElement, Class<? extends Annotation> annotationClass){
	  List<? extends AnnotationMirror> annotationMirrors=annotatedElement.getAnnotationMirrors();
	  for (  AnnotationMirror annotationMirror : annotationMirrors) {
	    TypeElement annotationElement=(TypeElement)annotationMirror.getAnnotationType().asElement();
	    if (isAnnotation(annotationElement,annotationClass)) {
	      return annotationMirror;
	    }
	  }
	  return null;
	}
	
	public boolean isAnnotation(TypeElement annotation, Class<? extends Annotation> annotationClass) {
		return annotation.getQualifiedName().toString().equals(annotationClass.getName());
	}
	
}
