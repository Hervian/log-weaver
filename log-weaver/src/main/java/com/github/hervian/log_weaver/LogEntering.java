package com.github.hervian.log_weaver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface LogEntering {

	/**
	 * Zero or more Strings indicating which arguments shall be logged.
	 * Note that 'this' is also an allowed string (if and only if the method is non-static).
	 * @return
	 */
	String[] value();
	
}
