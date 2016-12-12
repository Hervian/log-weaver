package com.github.hervian.log_weaver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface LogAround {

	/**
	 * Zero or more Strings indicating which arguments shall be logged.
	 * Note that 'this' is also an allowed string (if and only if the method is non-static).
	 * Also, note that passing no values correspond to passing all values.
	 * As such, if one wishes to log all arguments, simply annotate your method with '@LogAround'.
	 * (A drawback of the current design is that it is not possible to log entering a method
	 * without also logging at least one argument. On the other hand, it is a nice shorthand.
	 * 
	 * TODO: Since the default value '$args' is a string, which has special meaning to Javassist
	 * this is a bad design. What if we want to change the bytecode weaving framework to something else?
	 * If would probably be better if the default was the empty string, meaning 'do not log any arguments, 
	 * only log that the method is being entered.' 
	 */
	String[] value() default {}; //$args 
	
}
