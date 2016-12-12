package com.ibm.commerce.foundation.common.util.logging;

import java.lang.reflect.Field;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.junit.Assert;

public class LoggingHelper {
	
	public static LoggerForTest loggerForTest;
	
	public static boolean isEntryExitTraceEnabled(java.util.logging.Logger logger){
		return true;
	}
	
	public static Logger getLogger(Class<?> clazz){
		loggerForTest = new LoggerForTest(clazz.getName());
		//Below logic would enable a multithreaded execution (it expects that each method under test, is in its own class and that the class has a LoggerHelper field called 'logger')
		try {
			Field loggerFieldForTest = clazz.getField("logger");
			loggerFieldForTest.set(clazz.newInstance(), loggerForTest);
		} catch (NoSuchFieldException | SecurityException e) {
			Assert.fail(e.getMessage());
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			Assert.fail(e.getMessage());
		}
		loggerForTest.setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(java.util.logging.Level.FINEST);
		handler.setFormatter(new SimpleFormatter());
		loggerForTest.addHandler(handler);
		return loggerForTest;
	}
	
	public static class LoggerForTest extends Logger {
		
		public String entering_sourceClass;
		public String entering_sourceMethod;
		public Object[] arguments = new Object[0];
		
		public String exiting_sourceClass;
		public String exiting_sourceMethod;
		public Object result;
		
		protected LoggerForTest(String name) {
			super(name, null);
		}
		
		@Override
		public void entering(String sourceClass, String sourceMethod){
			entering(sourceClass, sourceMethod, null);
		}
		
		@Override
		public void entering(String sourceClass, String sourceMethod, Object argument){
			entering(sourceClass, sourceMethod, argument==null ? new Object[]{} : new Object[]{argument});
		}
		
		@Override
		public void entering(String sourceClass, String sourceMethod, Object[] arguments){
			this.entering_sourceClass = sourceClass;
			this.entering_sourceMethod = sourceMethod;
			this.arguments = arguments;
		}
		
		@Override
		public void exiting(String sourceClass, String sourceMethod){
			exiting(sourceClass, sourceMethod, null);
		}
		
		@Override
		public void exiting(String sourceClass, String sourceMethod, Object result){
			this.exiting_sourceClass = sourceClass;
			this.exiting_sourceMethod = sourceMethod;
			this.result = result;
		}
		
	}
	
}
