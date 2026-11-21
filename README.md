# Post-contenido — Unidad 1

Este repositorio reúne una refactorización SOLID de `OrderProcessor` y un análisis de tres patrones GoF en Spring Framework para el curso Patrones de Diseño de Software.

## Parte 1 — Refactorización SOLID

### Análisis de Violaciones — Parte 1

| Principio | Método o sección | Descripción |
|---|---|---|
| SRP | `calculateTotal`, `applyDiscount`, `saveOrder`, `sendEmail`, `printReport` | Las reglas tributarias, los descuentos, el almacenamiento, la comunicación y la presentación cambian por motivos independientes. Una modificación del formato del reporte obliga a editar la misma clase que calcula y guarda el pedido. |
| OCP | `applyDiscount` | Los tipos VIP y Regular están codificados en comparaciones dentro del método. Introducir una nueva política requiere editar el algoritmo existente y volver a validar sus ramas anteriores. |
| DIP | `saveOrder`, `sendEmail` y el flujo de negocio | La política de procesamiento está acoplada a una colección creada dentro de la clase y a la salida directa por consola. Cambiar el almacenamiento o el canal de confirmación obliga a editar el mismo componente que contiene las reglas de negocio. |

### Diseño y ejecución

`OrderService` coordina el cálculo con IVA, el descuento, el guardado y la confirmación. Recibe por constructor las interfaces `TotalCalculator`, `DiscountStrategy`, `OrderRepository` y `OrderNotifier`. `Main` configura las implementaciones y selecciona la estrategia de cada caso. `OrderReporter` formatea los resultados del repositorio en memoria. Las confirmaciones de correo son simuladas en consola.

Se requiere JDK 17 o superior y Maven 3.8 o superior. Desde la raíz del repositorio:

```sh
cd parte-1-refactorizacion-solid
mvn compile
mvn exec:java
```

| Orden | Subtotal | Total con IVA del 19 % | Descuento | Total mostrado |
|---|---:|---:|---:|---:|
| ORD-001 | 350.00 | 416.50 | 15 % | 354.03 |
| ORD-002 | 200.00 | 238.00 | 5 % | 226.10 |
| ORD-003 | 50.00 | 59.50 | 0 % | 59.50 |

Los totales se muestran con dos decimales. El historial conserva el `OrderProcessor` original y registra por separado la aplicación de SRP, OCP y DIP.

## Parte 2 — Análisis de patrones GoF en Spring

| Patrón | Categoría | Clase en Spring Framework 6.2.0 |
|---|---|---|
| Singleton | Creacional | `DefaultSingletonBeanRegistry` |
| Proxy | Estructural | `JdkDynamicAopProxy` |
| Observer | Comportamiento | `SimpleApplicationEventMulticaster` |

[Documento de análisis](parte-2-analisis-gof-spring/documento-analisis.md) · [Fragmentos originales de Spring](parte-2-analisis-gof-spring/evidencia/README.md)

## Herramientas utilizadas

Java con destino de compilación 17, Apache Maven, Git y código fuente de Spring Framework.

## Conclusiones

Separar las responsabilidades permitió modificar la política de descuento sin tocar el cálculo tributario ni el guardado. Las interfaces del servicio permiten sustituir la persistencia y el canal de confirmación desde el punto de configuración. Los casos de Spring muestran cómo aplicar esa misma idea al ciclo de vida de objetos, la interceptación de llamadas y la distribución de eventos.
