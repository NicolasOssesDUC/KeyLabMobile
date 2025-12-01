# Sesión de Desarrollo: 29 de Noviembre de 2025
**Objetivo Principal:** Implementación de flujo de compras Offline-First, historial de pedidos y gestión de usuario.

## Resumen de Funcionalidades Implementadas

### 1. Pasarela de Pago y Órdenes (Local)
- **Entidades:** Se creó `OrdenRepository` para gestionar la transacción de compra localmente.
- **Lógica de Negocio:** 
  - Simulación de pago basada en el último dígito de la tarjeta (Par=Aprobada / Impar=Rechazada).
  - Al aprobarse, se crea una orden (`Orden` + `OrdenItem`) en Room y se vacía el carrito.
- **UI:**
  - `PaymentBottomSheetFragment`: Formulario para ingresar datos de tarjeta.
  - `OrderReceiptActivity`: Pantalla de recibo con detalles de la compra recién hecha.

### 2. Historial de Pedidos ("Mis Pedidos")
- **Funcionalidad:** Listado de todas las compras realizadas por el usuario logueado.
- **Componentes:**
  - `OrderHistoryActivity`: RecyclerView con historial.
  - `OrderHistoryAdapter`: Tarjetas con resumen de orden (fecha, monto, estado).
  - Navegación desde el Perfil.

### 3. Gestión de Direcciones
- **Funcionalidad:** CRUD local de direcciones de envío.
- **Base de Datos:** Nueva tabla `direcciones` y `DireccionDao`.
- **UI:**
  - `AddressListActivity`: Lista de direcciones guardadas.
  - `AddAddressActivity`: Formulario para agregar nuevas direcciones.

### 4. Correcciones y Mejoras de Usuario
- **Autenticación:**
  - Se corrigió el flujo de inicio. Ahora, al cerrar sesión, se borra la preferencia `is_logged_in`, obligando a loguearse de nuevo al reiniciar la app.
  - El Perfil ahora muestra el nombre y correo real del usuario logueado (desde DB).
- **Limpieza de UI:** Se eliminaron botones no funcionales ("Favoritos", "Métodos de Pago") del Perfil.
- **Recursos:** Se consolidó y reparó el archivo `colors.xml` para solucionar errores de compilación y unificar temas.

---

# Sesión de Desarrollo: 30 de Noviembre de 2025
**Objetivo Principal:** Implementación del Panel de Administración (Gestión de Productos).

## Resumen de Funcionalidades Implementadas

### 1. Panel de Administración
- **Acceso Diferenciado:** 
  - Detección automática de correos con dominio `@keylab.com`.
  - Redirección a `AdminDashboardActivity` para administradores.
  - Persistencia de rol `is_admin` en `PreferencesManager`.
- **Dashboard:**
  - Visualización de productos en grid (similar al Home pero con acciones).
  - Botones rápidos de **Editar** y **Eliminar** en cada tarjeta.
  - Botón flotante para agregar nuevos productos.

### 2. Gestión de Productos (CRUD)
- **Formulario (`AddEditProductActivity`):**
  - Interfaz unificada para Crear y Editar.
  - Validación de campos (Nombre, Precio, Stock, Categoría).
  - **Carga de Imágenes:** Campo para URL manual con vista previa automática (Preview).
- **Lógica de Datos:**
  - Integración con `ProductoRepository` para operaciones POST, PATCH y DELETE.
  - Manejo de respuestas de Supabase.

### 3. Ajustes Técnicos
- **Mapas de Datos:** Se modificó el repositorio para enviar datos como `Map<String, Any>` en la creación, evitando conflictos con IDs autogenerados (error 400 pendiente de depuración final).
- **Recursos:** Traducción completa de strings faltantes y corrección de layouts.

## Estado del Proyecto
La aplicación ahora distingue entre usuarios clientes y administradores. El flujo de administración (UI/UX) está 100% funcional.
- **Pendiente:** Resolver error 400 (Bad Request) al enviar el POST de creación a Supabase.

## Próximos Pasos Sugeridos
- Depurar payload JSON para creación de productos.
- Sincronización de Órdenes y Direcciones con Supabase (Backend).