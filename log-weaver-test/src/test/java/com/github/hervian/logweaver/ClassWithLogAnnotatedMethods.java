package com.github.hervian.logweaver;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.github.hervian.log_weaver.LogAfter;
import com.github.hervian.log_weaver.LogAround;
import com.github.hervian.log_weaver.LogBefore;
import com.ibm.commerce.foundation.common.util.logging.LoggingHelper.LoggerForTest;


/**
 * In this class we will use some of the Log-annotations from the log-weaver project.
 * In the jUnit test classes these methods will be called, and it will be asserted that
 * the various Log-annotations have log-statements weaved into their bytecode.
 */
public class ClassWithLogAnnotatedMethods {

	/*
	 * The following constants are printed at central points in the Log-annotated methods.
	 * jUnit tests are set up to assert that the weaved log-statements and the print outs
	 * appear in the expected order, i.e. that the log statements have been weaved into
	 * the correct places in the code.
	 */
	
	public static final String FIRST_LINE_IN_METHOD_IN_SOURCE_CODE = "FIRST_LINE_IN_METHOD_IN_SOURCE_CODE";
	public static final String LINE_BEFORE_RETURN_STATEMENT = "LAST_LINE_IN_METHOD_IN_SOURCE_CODE";
	public static final String LINE_BEFORE_RETURN_STATEMENT_WITHIN_CONDITION = "LINE_BEFORE_RETURN_STATEMENT_WITHIN_CONDITION";
	public static final String STRING_REPRESENTATION_OF_THIS_OBJECT = "STRING_REPRESENTATION_OF_THIS_OBJECT";
	public static final String RETURN_VALUE_FROM_WITHIN_CONDITION = "RETURN_VALUE_FROM_WITHIN_CONDITION";
	public static final String RETURN_VALUE_AT_END_OF_METHOD = "RETURN_VALUE_AT_END_OF_METHOD";
	
	//This should probably be the format for the methods under test, such that each one can be assigned a unique Logger (by reflection in LoggingHelper using the passed Class argument)
		
	public static class LogBefore_log1ArgumentAndThis {
		public static LoggerForTest logger;
		@LogBefore({ "arg1", "this" })
		public String execute(String arg1) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}

	public static class LogAfter_NoArgsSpecified {
		public static LoggerForTest logger;
		@LogAfter
		public String execute(String arg1) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	
	public static class LogAround_log1ArgumentAndThis {
		public static LoggerForTest logger;
		@LogAround({ "arg1", "this" })
		public String execute(String arg1) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	
	public static class LogAround_log2Arguments {
		public static LoggerForTest logger;
		@LogAround({ "arg1", "arg2" })
		public String execute(String arg1, String arg2) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	
	public static class LogAround_logNoArguments {
		public static LoggerForTest logger;
		@LogAround
		public String execute(String arg1, String arg2) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	
	public static class LogAround_log1ArgumentOf2 {
		public static LoggerForTest logger;
		@LogAround({ "arg2" })
		public String execute(String arg1, String arg2) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	
	public static class LogAround_log1Argument_void {
		public static LoggerForTest logger;
		@LogAround("arg1")
		public void execute(String arg1) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
		}
	}
	
	public static class LogAround_log1Argument_testPrimitives {
		public static LoggerForTest logger;
		@LogAround({"arg1", "arg2", "arg3", "arg4", "arg5", "arg6", "arg7", "arg8"})
		public String execute(float arg1, boolean arg2, char arg3, long arg4, double arg5, short arg6, byte arg7, int arg8) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	
	public static class LogAround_log1Argument_TwoReturnStatements {
		public static LoggerForTest logger;
		@LogAround({"chooseConditionalReturn"})
		public String execute(boolean chooseConditionalReturn) {
			print(FIRST_LINE_IN_METHOD_IN_SOURCE_CODE);
			if (chooseConditionalReturn) {
				print(LINE_BEFORE_RETURN_STATEMENT_WITHIN_CONDITION);
				return RETURN_VALUE_FROM_WITHIN_CONDITION;
			}
			print(LINE_BEFORE_RETURN_STATEMENT);
			return RETURN_VALUE_AT_END_OF_METHOD;
		}
	}
	


	public Object main2(String[] args) throws Exception {
		print("first line in outer method");
		return new Callable<String>(){
			@LogAround
			public String call() throws Exception {
				return "in Callable";
			}
		}.call();
	}
	
	private static void print(String output){
		System.out.println(output);
	}
	
	
}
