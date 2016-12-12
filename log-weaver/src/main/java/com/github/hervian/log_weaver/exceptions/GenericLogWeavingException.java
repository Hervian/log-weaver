package com.github.hervian.log_weaver.exceptions;

import java.util.List;

import com.github.hervian.log_weaver.weaver.tasks.WeavingTask;

public class GenericLogWeavingException extends Exception {

	private static final long serialVersionUID = 7070299818411470863L;

	private List<WeavingTask> weavingTasks;
	
	public GenericLogWeavingException(List<WeavingTask> weavingTasks, String message) {
		super(message);
		this.weavingTasks = weavingTasks;
	}

	public List<WeavingTask> getWeavingTasks() {
		return weavingTasks;
	}

}
