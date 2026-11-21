# Proxy

La presencia de interceptores determina si la llamada al objetivo es directa o pasa por la cadena.

[JdkDynamicAopProxy.java, método invoke, líneas 211–224](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java#L211-L224). Spring Framework v6.2.0. Copyright de los autores originales. [Licencia Apache 2.0](https://github.com/spring-projects/spring-framework/blob/v6.2.0/LICENSE.txt).

```java
			if (chain.isEmpty()) {
				// We can skip creating a MethodInvocation: just invoke the target directly
				// Note that the final invoker must be an InvokerInterceptor so we know it does
				// nothing but a reflective operation on the target, and no hot swapping or fancy proxying.
				Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
				retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
			}
			else {
				// We need to create a method invocation...
				MethodInvocation invocation =
						new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
				// Proceed to the joinpoint through the interceptor chain.
				retVal = invocation.proceed();
			}
```
