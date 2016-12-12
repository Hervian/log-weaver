# log-weaver
Introduces an annotation processor that will weave log statements into the byte code of methods 
annotated with `@LogEntering`, `@LogExiting` or `@LogEnteringAndExiting`.  

The annotations correponds to those from `java.util.logging.Logger`, in that those are the log-statements 
that will get weaved into the code. *Note* that the annotation takes "arguments", namely a list of those arguments that shall be logged.  

This project serves 2 purposes:
  1. It can be used as a template for anyone, who wants to simplify their source code by replacing trivial logic (such as log-statements)
  with an annotation.
  2. It was constructed specifically for replacing log-statements in IBM's WebSphere Commerce.  

## Requirements
lambda-factory requires Java 1.8 or later.  

*Caveat*:   
Currently only Javac is supported. If you are using Eclipse (which is not based on Javac) you will get errors. 
This is because the logic makes use of the class ´TaskListener´, which is part of Javac's internal API. 
To get this project to work in Eclipse one will probably have to create something a la The Checker Framework's Eclipse Plugin 
(as I imagine that that plugin has been created for the same reasons, since the Checker Framework also makes use of the Javac's TaskListener.

## Using log-weaver
log-weaver was created to add logging-logic for entering and exiting a method.
Simple annotate a method with one of the log methods (`@LogEntering`, `@LogExiting` or `@LogEnteringAndExiting`).  
Say we have the following method, which we have annotated with ´@LogEnteringAndExiting´:  

```java
@LogEnteringAndExiting(value={"arg1", "this"})
public String execute(String arg1) {
    /*Some logic*/
    return "done";
}
```

The project, that defines the log-annotations, also define an AbstractProcessor ( - an annotation processor), 
which is automatically triggered, when you compile your project. It will weave log-statements in to the annotated method.  
The source code will remain as is, but the byte code will look *as if* the source code had been written like this:  

```java
@LogEnteringAndExiting(value={"arg1", "this"})
public String execute(String arg1) {
    if (LoggingHelper.isEntryExitTraceEnabled((Logger)comGithubHervian_LOGGER)) {
        comGithubHervian_LOGGER.entering(comGithubHervian_CLASSNAME, "execute", new Object[]{arg1, this});
    }
    String string = "done";
    if (LoggingHelper.isEntryExitTraceEnabled((Logger)comGithubHervian_LOGGER)) {
        comGithubHervian_LOGGER.exiting(comGithubHervian_CLASSNAME, "execute", string);
    }
    return string;
}
```  

Notice that it it possible to specify exactly which arguments to log, and that it is also possible to log `this`, i.e. the object itself (off course, the object should override `toString()` for this to be useful.  
In the code above the `LoggingHelper` is a special class from IBM's WebpShere Commerce for which this _proof of concept_ was developed. 
You can relatively easily define what statements are weaved by modifying the logic in the class ´LogWeaver´.  
Notice that the weaver also weaves in any fields, which is needed. In our case those fields are some some constants, fx the class name.
    
