# Patrones GoF en Spring Framework

## 1. Portada

| Campo | Información |
|---|---|
| Nombre | Johan Daniel Garcia Salcedo |
| Código | 1152379 |
| Curso y grupo | Patrones de Diseño de Software · 1156701-A |
| Unidad | Unidad 1 · Fundamentos de Patrones de Diseño |
| Fecha | 20 de noviembre de 2026 |

## 2. Introducción

Spring Boot configura y pone en marcha aplicaciones que usan el contenedor y otros componentes de Spring Framework. En el código de Spring Framework 6.2.0 se estudian tres decisiones de diseño que suelen quedar detrás de la configuración de una aplicación Boot: cómo se comparte una instancia, cómo se intercepta una llamada y cómo se distribuye un evento. El objetivo es vincular cada decisión con un patrón GoF y explicar qué componente asume cada responsabilidad.

La investigación consideró cinco candidatos. Se seleccionaron los tres primeros porque permiten identificar los participantes, el flujo de ejecución y la extensión que aporta cada patrón a partir de un fragmento concreto. Se consideraron también dos alternativas de comportamiento para distinguir una estrategia intercambiable de un punto de extensión definido por herencia.

| Candidato | Categoría | Ubicación examinada | Decisión |
|---|---|---|---|
| Singleton | Creacional | [Registro de beans compartidos](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java#L141-L149) | Seleccionado |
| Proxy | Estructural | [Despacho de una llamada AOP](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java#L209-L224) | Seleccionado |
| Observer | Comportamiento | [Distribución de eventos a listeners](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java#L142-L158) | Seleccionado |
| Strategy | Comportamiento | [Implementaciones de HandlerMapping usadas por DispatcherServlet](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-webmvc/src/main/java/org/springframework/web/servlet/DispatcherServlet.java#L1280-L1290) | Alternativa. Cada mapeador aplica su propia política de resolución. |
| Template Method | Comportamiento | [refreshBeanFactory, paso que completan las subclases durante refresh](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java#L709-L720), [declaración abstracta](https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-context/src/main/java/org/springframework/context/support/AbstractApplicationContext.java#L1584-L1589) | Alternativa. El contexto base controla la secuencia de inicialización. |

## 3. Singleton — Creacional

Singleton controla la disponibilidad de una instancia compartida dentro de un ámbito definido y ofrece un mecanismo para obtenerla. En Spring Framework el concepto aparece en `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry`, del módulo `spring-beans`. Spring aplica el ámbito por defecto por definición de bean y por contenedor. Dos definiciones que usan la misma clase pueden tener objetos distintos. Dos contextos independientes pueden crear sus propias instancias de una misma definición (Spring, s. f.-a).

El problema se observa cuando varios componentes de una aplicación Boot reciben el mismo servicio al construir el grafo de dependencias. La fábrica de beans necesita entregarles una referencia estable, respetar la configuración elegida y gestionar el ciclo de vida de la instancia compartida. Con construcción manual en cada consumidor, el código cliente tendría que coordinar la reutilización de la instancia. Con una instancia estática por clase, dos contextos del mismo proceso podrían compartir una configuración que cada uno necesita controlar de forma independiente. El registro central sitúa la regla de unicidad en el contenedor. Esta regla tampoco sincroniza el estado mutable de un bean. Si varios hilos lo usan, su implementación debe proteger ese estado o evitarlo.

El [fragmento de `addSingleton`, líneas 141–149](evidencia/singleton.md), muestra que `putIfAbsent` intenta asociar la instancia al nombre y que el registro rechaza una segunda asociación existente. Después retira las referencias de exposición temprana. Al consultar el registro, la fábrica puede recuperar la referencia vinculada al nombre. Los participantes siguen la relación `Cliente → fábrica → registro[nombre] → instancia` (Spring, 2024a).

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

La implementación refuerza **SRP**. El registro conserva la política de identidad y reutilización. La clase del servicio se ocupa de su comportamiento y puede seguir siendo una clase Java ordinaria. Un cambio de ámbito se configura en la definición del bean, de forma que el servicio no necesita incorporar lógica para controlar cuántas veces se construye.

## 4. Proxy — Estructural

Proxy coloca un intermediario que expone el contrato esperado por el cliente y controla el acceso al objeto real. El ejemplo aparece en `org.springframework.aop.framework.JdkDynamicAopProxy`, del módulo `spring-aop`. La clase es el `InvocationHandler` que respalda al proxy dinámico de Java. En esta modalidad el objeto entregado al cliente implementa las interfaces configuradas para el proxy (Spring, s. f.-b).

En Spring Boot, cuando se configura la gestión declarativa de transacciones mediante proxies, una llamada externa a un método de servicio puede necesitar que se abra una transacción, que se invoque la operación y que se confirme o revierta según el resultado. Codificar esas acciones en cada servicio obliga a repetir el manejo de la transacción y dificulta cambiar su política de manera uniforme. El proxy puede aplicar un interceptor antes y después de la llamada sin incorporar ese flujo a la implementación del servicio. Spring también admite proxies basados en subclases. El fragmento estudiado corresponde al mecanismo basado en interfaces.

El [fragmento de `invoke`, líneas 211–224](evidencia/proxy.md), distingue dos rutas. Si la cadena de interceptores está vacía, se invoca el objetivo por reflexión. Cuando hay interceptores, `ReflectiveMethodInvocation` encapsula la llamada y `proceed()` da paso a la cadena. La relación es `Cliente → proxy → interceptores → objetivo` (Spring, 2024b). Una llamada interna mediante `this` evita el proxy, de modo que el interceptor requiere que la llamada cruce esa frontera (Spring, s. f.-b).

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

El patrón refuerza **OCP** en este contexto. Se puede incorporar un interceptor compatible o modificar el conjunto de métodos a los que se aplica sin editar la implementación del servicio. Refuerza también **SRP**, ya que el interceptor controla una política transversal mientras el objetivo realiza la operación de negocio.

## 5. Observer — Comportamiento

Observer permite que varios suscriptores reaccionen a un cambio comunicado mediante un evento. En Spring Framework la distribución se implementa en `org.springframework.context.event.SimpleApplicationEventMulticaster`, del módulo `spring-context`. `ApplicationEventPublisher` es el contrato de publicación y `ApplicationListener` representa al suscriptor. El multicaster ocupa la posición intermedia entre el sujeto que publica y los posibles observadores (Spring, s. f.-c).

En una aplicación Boot, cuando se registra un pedido, un componente puede publicar un evento del que dependen tareas independientes, como el registro de métricas o la escritura de una entrada de auditoría. Con llamadas directas, el servicio tendría que conocer cada destinatario y cambiar al añadir uno nuevo. La distribución por tipos de evento permite registrar otro listener compatible sin editar el servicio. El mecanismo opera dentro del contexto y no aporta por sí solo persistencia del evento ni garantías de entrega propias de un sistema de mensajería.

El [fragmento de `multicastEvent`, líneas 145–158](evidencia/observer.md), muestra que se recorren los listeners compatibles. El multicaster programa la ejecución si cuenta con un ejecutor y el listener admite ese modo. En los demás casos lo invoca en el hilo que publica, incluido el caso de rechazo del ejecutor mostrado en el fragmento (Spring, 2024c). El flujo es `Publicador → multicaster → observadores compatibles`. Por defecto, el despacho es síncrono. Si se habilita ejecución asíncrona, el código cliente debe revisar qué contexto de hilo necesita el listener (Spring, s. f.-c).

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

La solución refuerza **OCP** al admitir reacciones nuevas por registro. También refuerza **DIP**, porque la lógica que publica depende de `ApplicationEventPublisher` y del contrato del evento. El multicaster despacha mediante `ApplicationListener` y cada implementación decide cómo reaccionar. Los contratos conservan el acoplamiento necesario para compartir el significado del evento.

## 6. Conclusiones

Los tres patrones resuelven decisiones diferentes. Singleton permite compartir una instancia en el ámbito del contenedor. Proxy conserva el contrato del servicio mientras incorpora una política transversal. Observer permite añadir reacciones a un evento sin editar a su publicador. En el diseño de una aplicación propia, la lección es ubicar cada punto de cambio en el componente que puede controlarlo, con un contrato que haga visibles sus límites de identidad, interceptación o despacho.

## 7. Referencias

Spring. (2024a). *DefaultSingletonBeanRegistry.java* (Spring Framework 6.2.0) [Código fuente]. GitHub. https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-beans/src/main/java/org/springframework/beans/factory/support/DefaultSingletonBeanRegistry.java

Spring. (2024b). *JdkDynamicAopProxy.java* (Spring Framework 6.2.0) [Código fuente]. GitHub. https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-aop/src/main/java/org/springframework/aop/framework/JdkDynamicAopProxy.java

Spring. (2024c). *SimpleApplicationEventMulticaster.java* (Spring Framework 6.2.0) [Código fuente]. GitHub. https://github.com/spring-projects/spring-framework/blob/v6.2.0/spring-context/src/main/java/org/springframework/context/event/SimpleApplicationEventMulticaster.java

Spring. (s. f.-a). *Bean scopes*. Spring Framework 6.2. https://docs.spring.io/spring-framework/reference/6.2/core/beans/factory-scopes.html

Spring. (s. f.-b). *Proxying mechanisms*. Spring Framework 6.2. https://docs.spring.io/spring-framework/reference/6.2/core/aop/proxying.html

Spring. (s. f.-c). *Additional capabilities of the ApplicationContext*. Spring Framework 6.2. https://docs.spring.io/spring-framework/reference/6.2/core/beans/context-introduction.html
