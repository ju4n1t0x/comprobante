Se agregaron:
computos/ComputoCatalogService.java
computos/IComputoCatalogService.java
computos/UnitType.java
computos/config/ComputoConfig.java
computos/config/ParameterConfig.java
computos/config/ItemConfig.java
computos/config/QuantityRuleConfig.java
computos/result/ComputedItem.java
computos/result/ComputedQuote.java
computos/config/JacksonConfig.java
El catálogo ahora:
- Lee los JSON desde resources.
- Valida productos por productUID.
- Evalúa reglas de cantidad.
- Soporta dependencias entre items, como placa-std → tornillo-t2.
- Calcula subtotales, IVA y total.
- Permite cantidades con distinta precisión visual por item.
Eliminación del legacy
Se eliminaron:
ItemComputo/ItemComputoDTO.java
ItemComputo/ItemComputoModel.java
computos/ComputoDTO.java
computos/ComputoMapper.java
computos/ComputoModel.java
computos/ComputoRepository.java
computos/ComputoService.java
computos/IComputoService.java
computos/Verificado.java
ui/view/computos/ComputoFormDialog.java
UnitType fue movido desde ItemComputo a:
computos/UnitType.java
Cotización
CotizarController ahora:
- Usa IComputoCatalogService.
- Carga los computos desde JSON.
- Genera dinámicamente los parámetros.
- Evalúa las reglas del catálogo.
- Muestra resultados tabulares.
- Muestra subtotal, IVA y total.
- Usa cards, ActionBar y estilos propios de cotización.
Computos
ComputosController y ComputosSectionProvider fueron migrados a catálogo JSON:
- Listado read-only.
- Búsqueda por nombre.
- Paginación.
- Sin alta, edición ni eliminación desde UI.
Diálogos
Se agregaron:
ui/DialogService.java
ui/components/DialogTheme.java
Y se aplicó el tema común a:
- Usuarios.
- Productos.
- Clientes.
- Categorías.
- Ventas.
- Login.
- Computos.
- Cotización.
También se actualizaron los estilos de theme.css para usar .dialog-pane, combos y scroll panes.
Categorías, listados y sidebar
Se implementó:
- Búsqueda por nombre en categorías.
- Paginación de categorías.
- Orden alfabético de productos.
- Orden alfabético de categorías.
- Orden por apellido/nombre en clientes.
- Orden alfabético de usuarios.
- Sidebar scrolleable.
- Mejoras de scrollbar y estilos globales.
Dependencias y configuración
En pom.xml:
- Se eliminó spring-boot-starter-actuator.
- Se agregó jackson-databind.
- Se agregó jackson-datatype-jsr310.
En application.properties:
- Se agregó dialecto H2 explícito.
- Se cambió la URL H2 a una ruta absoluta.
