package com.github.hervian.log_weaver.weaver.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.VariableElement;

public enum ArgumentsUtil {
	INSTANCE;
	
	public String getArgsToLog(String[] args, Map<String, VariableElement> map) {
		List<String> wrappedArguments = new ArrayList<>();
		for (int k = 0; k < args.length; k++) {
			if (args[k]!=null && !args[k].trim().isEmpty()){				
				String argument = wrapArgumentIfPrimitive(args[k], map);
				wrappedArguments.add(argument);
			}			
		}
		String argsToLog = wrappedArguments.stream().collect(Collectors.joining(","));
		return wrappedArguments.isEmpty() ? null : argsToLog;
	}
	
	/*
	 * This design is a bit ugly. The reason why we are wrapping the arguments,
	 * is because Javassist (i.e. the bytecode weaving framework used in LogWeaver) will
	 * not perform the weave correctly for primitives. The reason why it is ugly to 
	 * wrap the arguments here is because the knowledge of which byte code weaving framework
	 * is used ought to be unknown to all classes but LogWeaver. 
	 * I.e. it should be possible to change the byte code weaving framework without
	 * having to make changes too many strange places.
	 * TODO: The WeavingTask should instead of a Map<String, VariableElement>
	 * 			have a Map or List of some "Argument Bean", which can be fed to various service classes 
	 * 			to perform tasks like preparing the arguments for a specific byte code weaving framework.
	 */
	private String wrapArgumentIfPrimitive(String str, Map<String, VariableElement> map) {
		VariableElement variableElement = map.get(str);
		if (variableElement != null) {
			switch (variableElement.asType().toString()) {
			case "boolean":
				return "Boolean.valueOf(" + str + ")";
			case "char":
				return "Character.valueOf(" + str + ")";
			case "double":
				return "Double.valueOf(" + str + ")";
			case "float":
				return "Float.valueOf(" + str + ")";
			case "int":
				return "Integer.valueOf(" + str + ")";
			case "byte":
				return "Byte.valueOf(" + str + ")";
			case "short":
				return "Short.valueOf(" + str + ")";
			case "long":
				return "Long.valueOf(" + str + ")";
			default:
				return str;
			}
		}
		return str;
	}
	
}
