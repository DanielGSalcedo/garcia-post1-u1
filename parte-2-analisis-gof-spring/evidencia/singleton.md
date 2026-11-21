# Singleton

La inserción condicional conserva la primera asociación bajo el nombre. Los pasos siguientes retiran las entradas de exposición temprana.

[DefaultSingletonBeanRegistry.java, método addSingleton, líneas 141–149](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java#L141-L149). Spring Framework v6.2.0. Copyright de los autores originales. [Licencia Apache 2.0](https://github.com/spring-projects/spring-framework/blob/v6.2.0/LICENSE.txt).

```java
	protected void addSingleton(String beanName, Object singletonObject) {
		Object oldObject = this.singletonObjects.putIfAbsent(beanName, singletonObject);
		if (oldObject != null) {
			throw new IllegalStateException("Could not register object [" + singletonObject +
					"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
		}
		this.singletonFactories.remove(beanName);
		this.earlySingletonObjects.remove(beanName);
		this.registeredSingletons.add(beanName);
```
