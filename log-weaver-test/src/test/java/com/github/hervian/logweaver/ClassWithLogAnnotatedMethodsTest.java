package com.github.hervian.logweaver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_log1ArgumentAndThis;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_log1ArgumentOf2;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_log1Argument_TwoReturnStatements;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_log1Argument_testPrimitives;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_log1Argument_void;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_log2Arguments;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogAround_logNoArguments;
import com.github.hervian.logweaver.ClassWithLogAnnotatedMethods.LogBefore_log1ArgumentAndThis;
import com.ibm.commerce.foundation.common.util.logging.LoggingHelper.LoggerForTest;


public class ClassWithLogAnnotatedMethodsTest  {

	private static final String STRING_ARG1 = "STRING_ARG1";
	private static final String STRING_ARG2 = "STRING_ARG2";
	private String methodName = "execute";
	
	@Test
	public void test_logBefore_log1ArgumentAndThis() {
		LogBefore_log1ArgumentAndThis classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogBefore_log1ArgumentAndThis();
		
		classWithLogAnnotatedMethods.execute("asdf");
		    
		LoggerForTest loggerForTest = LogBefore_log1ArgumentAndThis.logger;
		assertEntering(loggerForTest, methodName, LogBefore_log1ArgumentAndThis.class.getName(), "asdf", classWithLogAnnotatedMethods);
	}
	
	@Test
	public void test_logAround_log1ArgumentAndThis() {
		LogAround_log1ArgumentAndThis classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log1ArgumentAndThis();
		
		Object returnValue = classWithLogAnnotatedMethods.execute(STRING_ARG1);
		
		LoggerForTest loggerForTest = LogAround_log1ArgumentAndThis.logger;
		assertEntering(loggerForTest, methodName, LogAround_log1ArgumentAndThis.class.getName(), STRING_ARG1, classWithLogAnnotatedMethods);
		assertExiting(loggerForTest, methodName, LogAround_log1ArgumentAndThis.class.getName(), returnValue);
	}
	
	@Test
	public void test_logAround_logAround_log2Arguments() {
		LogAround_log2Arguments classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log2Arguments();
		
		Object returnValue = classWithLogAnnotatedMethods.execute(STRING_ARG1, STRING_ARG2);
		
		LoggerForTest loggerForTest = LogAround_log2Arguments.logger;
		assertEntering(loggerForTest, methodName, LogAround_log2Arguments.class.getName(), STRING_ARG1, STRING_ARG2);
		assertExiting(loggerForTest, methodName, LogAround_log2Arguments.class.getName(), returnValue);
	}
	
	@Test
	public void test_logAround_logNoArguments() {
		LogAround_logNoArguments classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_logNoArguments();
		
		Object returnValue = classWithLogAnnotatedMethods.execute(STRING_ARG1, STRING_ARG2);
		
		LoggerForTest loggerForTest = LogAround_logNoArguments.logger;
		assertEntering(loggerForTest, methodName, LogAround_logNoArguments.class.getName());
		assertExiting(loggerForTest, methodName, LogAround_logNoArguments.class.getName(), returnValue);
	}
	
	@Test
	public void test_logAround_logAround_log1ArgumentOf2() {
		LogAround_log1ArgumentOf2 classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log1ArgumentOf2();
		
		Object returnValue = classWithLogAnnotatedMethods.execute(STRING_ARG1, STRING_ARG2);
		
		LoggerForTest loggerForTest = LogAround_log1ArgumentOf2.logger;
		assertEntering(loggerForTest, methodName, LogAround_log1ArgumentOf2.class.getName(), STRING_ARG2);
		assertExiting(loggerForTest, methodName, LogAround_log1ArgumentOf2.class.getName(), returnValue);
	}
	
	@Test
	public void test_logAround_log1Argument_void() {
		LogAround_log1Argument_void classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log1Argument_void();
		
		classWithLogAnnotatedMethods.execute(STRING_ARG1);
		
		LoggerForTest loggerForTest = LogAround_log1Argument_void.logger;
		assertEntering(loggerForTest, methodName, LogAround_log1Argument_void.class.getName(), STRING_ARG1);
		assertExiting(loggerForTest, methodName, LogAround_log1Argument_void.class.getName(), null);
	}
	
	@Test
	public void test_LogAround_log1Argument_testPrimitives() {
		LogAround_log1Argument_testPrimitives classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log1Argument_testPrimitives();
		
		Object returnValue = classWithLogAnnotatedMethods.execute(1.0f, true, 'c', 3l,5d, (short)6, (byte)7, 8);
		
		LoggerForTest loggerForTest = LogAround_log1Argument_testPrimitives.logger;
		assertEntering(loggerForTest, methodName, LogAround_log1Argument_testPrimitives.class.getName(), 1.0f, true, 'c', 3l,5d, (short)6, (byte)7, 8);
		assertExiting(loggerForTest, methodName, LogAround_log1Argument_testPrimitives.class.getName(), returnValue);
	}
	
	@Test
	public void test_logAround_logAround_log1Argument_TwoReturnStatements_chooseInner() {
		LogAround_log1Argument_TwoReturnStatements classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log1Argument_TwoReturnStatements();
		Boolean expectedArgs = true;
		
		Object returnValue = classWithLogAnnotatedMethods.execute(expectedArgs);
		
		LoggerForTest loggerForTest = LogAround_log1Argument_TwoReturnStatements.logger;
		assertEntering(loggerForTest, methodName, LogAround_log1Argument_TwoReturnStatements.class.getName(), expectedArgs);
		assertExiting(loggerForTest, methodName, LogAround_log1Argument_TwoReturnStatements.class.getName(), returnValue);
	}
	
	@Test
	public void test_logAround_logAround_log1Argument_TwoReturnStatements_chooseOuter() {
		LogAround_log1Argument_TwoReturnStatements classWithLogAnnotatedMethods = new ClassWithLogAnnotatedMethods.LogAround_log1Argument_TwoReturnStatements();
		Boolean expectedArgs = false;
		
		Object returnValue = classWithLogAnnotatedMethods.execute(expectedArgs);
		
		LoggerForTest loggerForTest = LogAround_log1Argument_TwoReturnStatements.logger;
		assertEntering(loggerForTest, methodName, LogAround_log1Argument_TwoReturnStatements.class.getName(), expectedArgs);
		assertExiting(loggerForTest, methodName, LogAround_log1Argument_TwoReturnStatements.class.getName(), returnValue);
	}
	
	
	
	private void assertEntering(LoggerForTest loggerForTest, String methodName, String expectedClassName, Object... expectedLoggedArgs){
		assertNotNull(loggerForTest);
		assertEquals(expectedClassName, loggerForTest.entering_sourceClass);
		if (expectedLoggedArgs==null){
			expectedLoggedArgs = new Object[]{};
		}
		if (loggerForTest.arguments==null){
			loggerForTest.arguments = new Object[]{};
		}
		assertEquals(expectedLoggedArgs.length, loggerForTest.arguments.length);
		for (int i=0; i<loggerForTest.arguments.length; i++){
			assertEquals(expectedLoggedArgs[i], loggerForTest.arguments[i]);
		}
	}

	private void assertExiting(LoggerForTest loggerForTest, String methodName, String expectedClassName, Object returnValue) {
		assertNotNull(loggerForTest);
		assertEquals(expectedClassName, loggerForTest.exiting_sourceClass);
		assertEquals(methodName, loggerForTest.exiting_sourceMethod);
		assertEquals(returnValue, loggerForTest.result);
	}

	
}
