# Observer

El multicaster selecciona los listeners compatibles y decide cómo invocar cada uno.

[SimpleApplicationEventMulticaster.java, método multicastEvent, líneas 145–158](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java#L145-L158). Spring Framework v6.2.0. Copyright de los autores originales. [Licencia Apache 2.0](https://github.com/spring-projects/spring-framework/blob/v6.2.0/LICENSE.txt).

```java
		for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
			if (executor != null && listener.supportsAsyncExecution()) {
				try {
					executor.execute(() -> invokeListener(listener, event));
				}
				catch (RejectedExecutionException ex) {
					// Probably on shutdown -> invoke listener locally instead
					invokeListener(listener, event);
				}
			}
			else {
				invokeListener(listener, event);
			}
		}
```
