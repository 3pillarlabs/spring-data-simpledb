package org.springframework.data.simpledb.logging;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingConfiguration {

	private static final String POINTCUT_EXECUTION_REPOSITORY = "execution(public * org.springframework.data.repository.Repository+.*(..))";
	private static final String EXCEPTION_MESSAGE = "Exception thrown: "
			+ CustomizableTraceInterceptor.PLACEHOLDER_EXCEPTION;
	private static final String EXIT_METHOD_MESSAGE = "Exiting method: " + CustomizableTraceInterceptor.PLACEHOLDER_METHOD_NAME
			+ " having return value " + CustomizableTraceInterceptor.PLACEHOLDER_RETURN_VALUE
			+ ", execution time: " + CustomizableTraceInterceptor.PLACEHOLDER_INVOCATION_TIME + " ms";
	private static final String ENTER_METHOD_MESSAGE = "Entering method: "
			+ CustomizableTraceInterceptor.PLACEHOLDER_METHOD_NAME + "("
			+ CustomizableTraceInterceptor.PLACEHOLDER_ARGUMENTS + ")";

	@Bean
	public CustomizableTraceInterceptor interceptor() {

		CustomizableTraceInterceptor interceptor = new CustomizableTraceInterceptor();
		interceptor.setEnterMessage(ENTER_METHOD_MESSAGE);
		interceptor.setExceptionMessage(EXCEPTION_MESSAGE);
		interceptor.setExitMessage(EXIT_METHOD_MESSAGE);

		return interceptor;
	}

	@Bean
	public Advisor traceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(POINTCUT_EXECUTION_REPOSITORY);

		return new DefaultPointcutAdvisor(pointcut, interceptor());
	}
}
