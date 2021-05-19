package com.revature.aspects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

@Service
@Aspect
public class LoggingAspect {
	
	@Around("loggingAspectHook()")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		Logger logObject = LogManager.getLogger(pjp.getTarget().getClass());

		// object for result that advised method returns
		Object result = null;
		
		// log method name and arguments passed to it
		logObject.trace("Method {} is running", pjp.getSignature().getName());
		logObject.trace("Arguments: {}", pjp.getArgs());

		try {
			// run advised method
			result = pjp.proceed();
		} catch (Throwable t) {
			// if method threw an error, log it
			if (t.getCause() != null) {
				Throwable t2 = t.getCause();
				logObject.error("{} threw an exception: {}", pjp.getSignature(), t);
				for (StackTraceElement stackEl : t2.getStackTrace()) {
					logObject.warn(stackEl);
				}
			}
			// after logging, re-throw the error, we do not handle the errors in the log proxy.
			throw t;
		}
		// log successful run, and return the methods result
		logObject.trace("{} returning with: {}", pjp.getSignature().getName(), result);
		return result;
	}

	@Pointcut("execution( * com.revature..*(..) )")
	private void loggingAspectHook() {
		/* Empty Method For Hook */ }
}
