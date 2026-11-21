# Post-contenido — Unidad 1

Este repositorio reúne una refactorización SOLID de `OrderProcessor` y un análisis de tres patrones GoF en Spring Framework para el curso Patrones de Diseño de Software.

## Parte 1 — Refactorización SOLID

### Análisis de Violaciones — Parte 1

| Principio | Método o sección | Descripción |
|---|---|---|
| SRP | `calculateTotal`, `applyDiscount`, `saveOrder`, `sendEmail`, `printReport` | Las reglas tributarias, los descuentos, el almacenamiento, la comunicación y la presentación cambian por motivos independientes. Una modificación del formato del reporte obliga a editar la misma clase que calcula y guarda el pedido. |
| OCP | `applyDiscount` | Los tipos VIP y Regular están codificados en comparaciones dentro del método. Introducir una nueva política requiere editar el algoritmo existente y volver a validar sus ramas anteriores. |
| DIP | `saveOrder`, `sendEmail` y el flujo de negocio | La política de procesamiento está acoplada a una colección creada dentro de la clase y a la salida directa por consola. Cambiar el almacenamiento o el canal de confirmación obliga a editar el mismo componente que contiene las reglas de negocio. |
