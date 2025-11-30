# 📁 Estructura del Proyecto KeyLab Mobile

## 🏗️ Arquitectura General: MVVM (Model-View-ViewModel)

```
app/src/main/java/com/keylab/mobile/
│
├── 📂 domain/          # CAPA DE DOMINIO (Modelos de negocio)
├── 📂 data/            # CAPA DE DATOS (Persistencia + Red)
├── 📂 ui/              # CAPA DE PRESENTACIÓN (Vistas + ViewModels)
└── 📂 utils/           # UTILIDADES (Helpers, extensiones)
```

---

## 📂 1. **domain/** - CAPA DE DOMINIO

**Propósito**: Entidades de negocio independientes de Android. Son las clases que representan los conceptos centrales de la app.

```
domain/
└── model/
    ├── Producto.kt        # Entidad principal de producto
    └── CarritoItem.kt     # Item del carrito de compras
```

### 📄 **Producto.kt**
**Qué hace**: Modelo central que representa un producto de teclado mecánico.

**Características**:
- Anotado con `@Entity` para Room (persistencia SQLite)
- Anotado con `@SerializedName` para Retrofit (API Supabase)
- Mapea snake_case (backend) a camelCase (Kotlin)

**Campos**:
```kotlin
id: Int                    // ID único del producto
nombre: String             // Nombre (ej: "Keychron K8 Pro")
descripcion: String?       // Descripción detallada
precio: Double             // Precio en CLP
categoria: String          // "Teclados", "Keycaps", "Switches", "Cases"
subcategoria: String?      // "60%", "75%", "Full Size"
imagenUrl: String?         // URL Supabase Storage
stock: Int                 // Cantidad disponible
createdAt: String?         // Timestamp creación
updatedAt: String?         // Timestamp actualización
```

**Usado por**: Room (local), Retrofit (red), ViewModels (lógica), Adapters (UI)

---

### 📄 **CarritoItem.kt**
**Qué hace**: Representa un producto agregado al carrito con cantidad seleccionada.

**Características**:
- Tabla separada en Room para persistir carrito
- Se crea cuando usuario agrega producto
- Persiste incluso si cierras la app

**Campos**:
```kotlin
productoId: Int            // ID del producto (primary key)
nombre: String             // Nombre del producto
precio: Double             // Precio unitario
categoria: String          // Categoría del producto
imagenUrl: String?         // URL imagen
cantidad: Int              // Cantidad agregada (1, 2, 3...)
fechaAgregado: Long        // Timestamp cuando se agregó
```

**Usado por**: CarritoDao, CarritoRepository, CarritoViewModel, CartActivity

---

## 📂 2. **data/** - CAPA DE DATOS

**Propósito**: Manejo de TODAS las fuentes de datos (base de datos local + API remota).

```
data/
├── local/              # Persistencia local (Room SQLite)
├── remote/             # Comunicación con API (Retrofit)
└── repository/         # Patrón Repository (coordina local + remoto)
```

---

### 📂 **data/local/** - Persistencia Local (Room)

```
local/
├── AppDatabase.kt      # Configuración de la BD SQLite
├── ProductoDao.kt      # Operaciones CRUD de productos
└── CarritoDao.kt       # Operaciones CRUD del carrito
```

#### 📄 **AppDatabase.kt**
**Qué hace**: Base de datos SQLite con Room. Es el punto de acceso a todas las tablas.

**Características**:
- Singleton (solo una instancia en toda la app)
- Define las entidades (tablas): `Producto`, `CarritoItem`
- Proporciona los DAOs (interfaces de queries)
- Versión actual: 3

**Código esencial**:
```kotlin
@Database(
    entities = [Producto::class, CarritoItem::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase { ... }
    }
}
```

**Usado por**: Repositories (ProductoRepository, CarritoRepository)

---

#### 📄 **ProductoDao.kt**
**Qué hace**: Define TODAS las operaciones SQL para la tabla `productos`.

**Operaciones disponibles** (17 funciones):

**Lectura (SELECT)**:
```kotlin
obtenerTodos(): Flow<List<Producto>>               // Lista completa reactiva
obtenerPorId(id): Flow<Producto?>                   // Un producto específico
obtenerPorCategoria(cat): Flow<List<Producto>>      // Filtrar por categoría
buscarProductos(query): Flow<List<Producto>>        // Búsqueda por nombre/descripción
obtenerConStock(): Flow<List<Producto>>             // Solo con stock > 0
contarTodos(): Flow<Int>                            // Cantidad total de productos
existe(id): Flow<Boolean>                           // Verificar si existe
```

**Escritura (INSERT/UPDATE/DELETE)**:
```kotlin
insertar(producto): Long                            // Agregar uno
insertarTodos(productos): List<Long>                // Agregar varios (sincronización)
actualizar(producto): Int                           // Modificar uno
eliminar(producto): Int                             // Borrar uno
eliminarPorId(id): Int                              // Borrar por ID
eliminarTodos()                                     // Limpiar tabla
```

**Características**:
- Flow<> para reactividad (UI se actualiza sola cuando cambian datos)
- OnConflictStrategy.REPLACE (sincronización idempotente)
- Queries optimizadas con índices

**Usado por**: ProductoRepository

---

#### 📄 **CarritoDao.kt**
**Qué hace**: Define operaciones SQL para la tabla `carrito_items`.

**Operaciones disponibles** (9 funciones):

**Lectura**:
```kotlin
obtenerItems(): Flow<List<CarritoItem>>             // Lista completa del carrito
contarItems(): Flow<Int>                            // Badge contador (ej: 3 items)
obtenerSubtotal(): Flow<Double?>                    // SUM(precio × cantidad)
obtenerPorId(id): Flow<CarritoItem?>                // Un item específico
```

**Escritura**:
```kotlin
insertar(item)                                      // Agregar al carrito
actualizarCantidad(productoId, cantidad)            // Cambiar cantidad (+/-)
eliminarPorId(id)                                   // Quitar item
vaciarCarrito()                                     // Limpiar todo (después de compra)
```

**Cálculos automáticos**:
- Subtotal: `SELECT SUM(precio * cantidad) FROM carrito_items`
- Contador: `SELECT COUNT(*) FROM carrito_items`

**Usado por**: CarritoRepository

---

### 📂 **data/remote/** - Comunicación con API

```
remote/
├── SupabaseApiService.kt     # Interface Retrofit (endpoints)
├── RetrofitClient.kt         # Configuración Retrofit (singleton)
└── ApiResponse.kt            # Wrapper de respuestas (Success/Error/Loading)
```

#### 📄 **SupabaseApiService.kt**
**Qué hace**: Define los endpoints de la API REST de Supabase.

**Endpoints implementados** (5 operaciones CRUD):
```kotlin
@GET("/rest/v1/productos?select=*")
suspend fun obtenerProductos(): List<Producto>

@GET("/rest/v1/productos?id=eq.{id}&select=*")
suspend fun obtenerProductoPorId(@Path("id") id: Int): List<Producto>

@POST("/rest/v1/productos")
suspend fun crearProducto(@Body producto: Producto): Producto

@PATCH("/rest/v1/productos?id=eq.{id}")
suspend fun actualizarProducto(@Path("id") id: Int, @Body producto: Producto): Producto

@DELETE("/rest/v1/productos?id=eq.{id}")
suspend fun eliminarProducto(@Path("id") id: Int)
```

**Características**:
- Todas las funciones son `suspend` (coroutines)
- Headers de auth inyectados automáticamente por interceptor
- Formato JSON automático con Gson

**Usado por**: ProductoRepository

---

#### 📄 **RetrofitClient.kt**
**Qué hace**: Configura el cliente HTTP para comunicarse con Supabase.

**Responsabilidades**:
1. **Autenticación automática** (Interceptor agrega headers):
   ```kotlin
   apikey: BuildConfig.SUPABASE_KEY
   Authorization: Bearer BuildConfig.SUPABASE_KEY
   ```

2. **Logging** (solo en debug):
   ```kotlin
   HttpLoggingInterceptor → imprime requests/responses en logcat
   ```

3. **Timeouts**:
   ```kotlin
   connectTimeout: 30s
   readTimeout: 30s
   writeTimeout: 30s
   ```

4. **Conversor JSON**:
   ```kotlin
   GsonConverterFactory → serializa/deserializa automáticamente
   ```

**Singleton**: Solo una instancia de Retrofit en toda la app.

**Usado por**: ProductoRepository

---

#### 📄 **ApiResponse.kt**
**Qué hace**: Wrapper para manejar estados de red (Loading/Success/Error).

**Sealed class** (solo puede ser uno de estos 3 estados):
```kotlin
sealed class ApiResponse<out T> {
    data object Loading : ApiResponse<Nothing>()               // Cargando
    data class Success<T>(val data: T) : ApiResponse<T>()      // Éxito
    data class Error(val message: String) : ApiResponse<Nothing>()  // Error
}
```

**Flujo típico**:
```kotlin
when (response) {
    is ApiResponse.Loading -> mostrarSpinner()
    is ApiResponse.Success -> adapter.submitList(response.data)
    is ApiResponse.Error -> Toast.makeText(response.message)
}
```

**Usado por**: Repository + ViewModel para manejo de estados

---

### 📂 **data/repository/** - Patrón Repository

```
repository/
├── ProductoRepository.kt     # Coordina productos (local + remoto)
└── CarritoRepository.kt      # Coordina carrito (solo local)
```

#### 📄 **ProductoRepository.kt**
**Qué hace**: Capa intermedia entre ViewModels y fuentes de datos. Implementa patrón **offline-first**.

**Filosofía offline-first**:
1. ViewModel pide datos → Repository
2. Repository devuelve Flow de Room (datos locales) → UI se actualiza RÁPIDO
3. Repository sincroniza con API en background
4. Nuevos datos se guardan en Room
5. Flow emite cambios → UI se actualiza automáticamente

**Funciones principales**:

**Lectura**:
```kotlin
obtenerProductos(): Flow<List<Producto>>            // Lista completa (desde Room)
buscarProductos(query): Flow<List<Producto>>        // Búsqueda local
filtrarPorCategoria(cat): Flow<List<Producto>>      // Filtro local
```

**Sincronización**:
```kotlin
suspend fun sincronizarProductos(): ApiResponse<Unit> {
    return try {
        val productosRemote = apiService.obtenerProductos()  // API
        productoDao.eliminarTodos()                          // Limpiar local
        productoDao.insertarTodos(productosRemote)           // Guardar nuevos
        ApiResponse.Success(Unit)
    } catch (e: Exception) {
        ApiResponse.Error(e.message)
    }
}
```

**Escritura**:
```kotlin
crearProducto(producto): ApiResponse<Producto>      // POST a API + guardar local
actualizarProducto(producto): ApiResponse<Producto> // PATCH a API + actualizar local
eliminarProducto(id): ApiResponse<Unit>             // DELETE a API + borrar local
```

**Ventajas**:
- App funciona sin internet (muestra datos locales)
- Sincronización en background transparente
- UI siempre responsiva (Room es instantáneo)

**Usado por**: ProductoViewModel

---

#### 📄 **CarritoRepository.kt**
**Qué hace**: Maneja el carrito de compras con persistencia local (solo Room, no API).

**Funciones principales**:
```kotlin
// Lectura reactiva
obtenerItems(): Flow<List<CarritoItem>>
contarItems(): Flow<Int>
calcularSubtotal(): Flow<Double>

// Agregar producto
suspend fun agregarProducto(producto: Producto) {
    val itemExistente = carritoDao.obtenerPorId(producto.id).first()
    if (itemExistente != null) {
        // Si ya existe, incrementar cantidad
        carritoDao.actualizarCantidad(producto.id, itemExistente.cantidad + 1)
    } else {
        // Si no existe, crear nuevo item
        val nuevoItem = CarritoItem(
            productoId = producto.id,
            nombre = producto.nombre,
            precio = producto.precio,
            cantidad = 1,
            ...
        )
        carritoDao.insertar(nuevoItem)
    }
}

// Modificar cantidad
incrementarCantidad(productoId)
decrementarCantidad(productoId)
actualizarCantidad(productoId, nuevaCantidad)

// Eliminar
eliminarItem(productoId)
vaciarCarrito()
```

**Lógica de envío**:
```kotlin
fun calcularCostoEnvio(subtotal: Double): Double {
    return if (subtotal >= 50000.0) 0.0 else 3990.0
}

fun calcularTotal(subtotal: Double): Double {
    return subtotal + calcularCostoEnvio(subtotal)
}
```

**Usado por**: CarritoViewModel

---

## 📂 3. **ui/** - CAPA DE PRESENTACIÓN

**Propósito**: Todo lo que ve e interactúa el usuario.

```
ui/
├── adapter/            # RecyclerView adapters (listas)
├── viewmodel/          # Lógica de presentación
├── LoginActivity.kt    # Pantalla de inicio de sesión
├── MainActivity.kt     # Hub principal (categorías + productos)
├── CategoryActivity.kt # Lista productos por categoría
├── CartActivity.kt     # Carrito de compras
├── ProfileActivity.kt  # Perfil de usuario
├── CameraActivity.kt   # Captura de fotos (CameraX)
└── ProductDetailActivity.kt  # Detalle de producto
```

---

### 📂 **ui/adapter/** - Adaptadores de RecyclerView

#### 📄 **ProductoAdapter.kt**
**Qué hace**: Convierte `List<Producto>` en tarjetas visuales en un grid 2 columnas.

**Responsabilidades**:
- Inflar layout `item_producto.xml`
- Llenar datos (nombre, precio, imagen, categoría)
- Cargar imágenes con Glide
- Formatear precio CLP ($89.990)
- Manejar clicks (ver detalle, agregar al carrito)
- DiffUtil para actualizaciones eficientes

**Usado en**: MainActivity, CategoryActivity

---

#### 📄 **CartAdapter.kt**
**Qué hace**: Convierte `List<CarritoItem>` en lista vertical de items del carrito.

**Responsabilidades**:
- Inflar layout `item_cart.xml`
- Mostrar producto + cantidad + precio total
- Controles +/- para cambiar cantidad
- Botón eliminar item
- Cálculo dinámico (precio × cantidad)

**Usado en**: CartActivity

---

#### 📄 **CategoryAdapter.kt**
**Qué hace**: Muestra chips horizontales de categorías (Todos, Teclados, Keycaps, etc).

**Responsabilidades**:
- Inflar layout `item_category.xml`
- Estados visuales (seleccionado/no seleccionado)
- Manejar click para filtrar productos

**Usado en**: MainActivity

---

### 📂 **ui/viewmodel/** - ViewModels

#### 📄 **ProductoViewModel.kt**
**Qué hace**: Cerebro de la lógica de productos. Intermedia entre UI y Repository.

**Datos expuestos** (observables por Activities):
```kotlin
val productos: Flow<List<Producto>>                 // Lista reactiva desde Room
val isLoading: LiveData<Boolean>                    // Estado de carga (spinner)
val error: LiveData<String?>                        // Mensajes de error (Toast)
val successMessage: LiveData<String?>               // Mensajes de éxito (Toast)
```

**Funciones**:
```kotlin
sincronizarProductos()              // Pull-to-refresh
buscarProductos(query)              // Búsqueda en tiempo real
filtrarPorCategoria(categoria)      // Filtro de categorías
crearProducto(producto)             // Agregar nuevo producto
actualizarProducto(producto)        // Editar producto existente
eliminarProducto(id)                // Borrar producto
```

**Características**:
- `viewModelScope` (coroutines automáticas, se cancelan al destruir)
- Sobrevive a rotaciones de pantalla
- No conoce la UI (no tiene referencias a Activity/Fragment)

**Usado por**: MainActivity, CategoryActivity, ProductDetailActivity

---

#### 📄 **CarritoViewModel.kt**
**Qué hace**: Cerebro de la lógica del carrito.

**Datos expuestos**:
```kotlin
val items: Flow<List<CarritoItem>>                  // Items del carrito
val totalItems: Flow<Int>                           // Cantidad total (badge)
val subtotal: Flow<Double>                          // Suma precios
val costoEnvio: Flow<Double>                        // Gratis si > $50.000
val total: Flow<Double>                             // Subtotal + envío
```

**Funciones**:
```kotlin
agregarProducto(producto)           // Agregar al carrito
incrementarCantidad(productoId)     // Botón "+"
decrementarCantidad(productoId)     // Botón "-"
eliminarItem(productoId)            // Quitar del carrito
vaciarCarrito()                     // Limpiar todo
```

**Usado por**: MainActivity, CategoryActivity, CartActivity, ProductDetailActivity

---

### 📂 **ui/** - Activities (Pantallas)

#### 📄 **LoginActivity.kt**
**Qué hace**: Pantalla de inicio de sesión (primera pantalla que ve el usuario).

**UI**: Opal-style dark theme minimalista.

**Funcionalidades**:
- Formulario email + password
- Validaciones en español:
  - Email requerido + formato válido
  - Password requerida + mínimo 6 caracteres
- Loading spinner durante login
- Links para recuperar contraseña y registro (placeholder)
- Navegación a MainActivity tras login exitoso

**Estado actual**: Mock (sin Supabase Auth real todavía).

---

#### 📄 **MainActivity.kt**
**Qué hace**: Hub principal de la app. Primera pantalla tras login.

**Componentes**:
1. **Logo KeyLab** (120dp, superior izquierda)
2. **Chips de categorías** (horizontal scroll):
   - Todos, Teclados, Keycaps, Switches, Cases
3. **Grid de productos** (2 columnas):
   - Tarjetas con imagen, nombre, precio, categoría
   - Botón "Agregar al carrito"
4. **Bottom Navigation** (3 botones):
   - Inicio | Carrito | Perfil

**Funcionalidades**:
- Sincroniza productos desde Supabase al iniciar
- Pull-to-refresh para actualizar
- Filtrado por categoría instantáneo
- Navegación a:
  - CategoryActivity (ver categoría completa)
  - CartActivity (carrito)
  - ProfileActivity (perfil)
  - ProductDetailActivity (detalle de producto)

---

#### 📄 **CategoryActivity.kt**
**Qué hace**: Muestra todos los productos de una categoría específica.

**Navegación**: `MainActivity → Click categoría → CategoryActivity`

**Componentes**:
- Toolbar con título de categoría
- Grid 2 columnas con productos filtrados
- SwipeRefreshLayout para actualizar

**Funcionalidades**:
- Recibe categoría por Intent
- Filtra productos automáticamente
- Click en producto → ProductDetailActivity

---

#### 📄 **CartActivity.kt**
**Qué hace**: Pantalla del carrito de compras con resumen.

**Componentes**:
1. **RecyclerView** (lista vertical):
   - Imagen, nombre, precio unitario
   - Controles +/- de cantidad
   - Precio total por item
   - Botón eliminar
2. **Card resumen inferior** (fixed):
   - Subtotal
   - Costo envío (gratis si > $50.000)
   - Total
   - Botón "Proceder al pago"

**Funcionalidades**:
- Persistencia automática (Room)
- Cálculo dinámico de totales
- Empty state con emoji 🛒
- Modificar cantidades en tiempo real

---

#### 📄 **ProfileActivity.kt**
**Qué hace**: Pantalla de perfil de usuario.

**Componentes**:
1. **Avatar circular** (clickeable para cambiar foto):
   - Cámara
   - Galería
2. **Card superior**: Nombre + email
3. **Secciones**:
   - Mi cuenta: 📦 Pedidos, 📍 Direcciones, 💳 Pagos, ❤️ Favoritos
   - Configuración: ⚙️ Settings, ❓ Ayuda
4. **Botón "Cerrar sesión"** (outlined red)

**Funcionalidades**:
- Sistema de cambio de foto con BottomSheet
- FileProvider para compartir imágenes
- ActivityResultContracts (APIs modernas)
- Foto temporal en cache

**Estado actual**: Foto local (pendiente subir a Supabase Storage).

---

#### 📄 **CameraActivity.kt**
**Qué hace**: Captura de fotos con cámara nativa (elemento nativo requerido).

**Componentes**:
- PreviewView (vista previa en tiempo real)
- Botones: Capturar | Cancelar | Voltear

**Funcionalidades**:
- CameraX con Preview + ImageCapture
- Solicitud de permiso de cámara en runtime
- Guardado en almacenamiento interno
- Nombre con timestamp (IMG_20251110_153045.jpg)
- Retorna URI de foto capturada

**Usado potencialmente por**: Creación/edición de productos (futuro).

---

#### 📄 **ProductDetailActivity.kt**
**Qué hace**: Pantalla de detalle completo de un producto.

**Componentes**:
- Imagen grande (320dp)
- Nombre + categoría + subcategoría
- Precio destacado
- Descripción completa
- Stock disponible
- Controles de cantidad (+/-)
- Precio total calculado
- Botón "Agregar al Carrito"

**Funcionalidades**:
- Carga producto por ID desde Room
- Validación de stock (máximo = stock disponible)
- Agregado al carrito con cantidad personalizada
- Estados visuales (sin stock, loading)

---

## 📂 4. **res/** - Recursos

```
res/
├── drawable/           # Iconos, imágenes, shapes
├── layout/             # Archivos XML de diseño de pantallas
├── values/             # Strings, colores, dimensiones, temas
├── menu/               # Menús (Bottom Navigation)
└── xml/                # Configuraciones (FileProvider)
```

### **res/values/**

#### 📄 **strings.xml**
Todos los textos de la app en español (48 líneas).

#### 📄 **colors.xml**
50+ colores organizados (64 líneas):
- KeyLab Brand (#007BFF)
- Material3 Theme (30+ colores)
- Dark Theme (11 colores para login)
- Text Colors (primary, secondary, tertiary, disabled)
- State Colors (success, warning, error, info)

#### 📄 **dimens.xml**
25 dimensiones estandarizadas (34 líneas):
- Spacing (xs: 4dp → xl: 32dp)
- Card (radius: 12dp, elevation: 2dp)
- Product (image: 160dp, card: 320dp)
- Text Sizes (xs: 12sp → xxl: 32sp)
- Icons (sm: 20dp, md: 24dp)

#### 📄 **themes.xml**
Material Design 3 completo (53 líneas):
- Theme.Material3.DayNight.NoActionBar
- Custom Toolbar style
- Custom Card style
- Status bar color

---

## 🗂️ **utils/** - Utilidades

**Estado actual**: Carpeta vacía (reservada para futuras extensiones, helpers, constantes).

**Potenciales usos futuros**:
- Extensions.kt (funciones de extensión Kotlin)
- Constants.kt (valores constantes globales)
- DateUtils.kt (formateo de fechas)
- PriceUtils.kt (formateo de precios CLP)
- ImageUtils.kt (compresión de imágenes)

---

## 🔄 Flujo de Datos Completo (Ejemplo: Ver Productos)

```
1. Usuario abre MainActivity
    ↓
2. onCreate() → viewModel.sincronizarProductos()
    ↓
3. ProductoViewModel → repository.sincronizarProductos()
    ↓
4. ProductoRepository → apiService.obtenerProductos() [SUPABASE API]
    ↓
5. Productos descargados → productoDao.insertarTodos() [ROOM]
    ↓
6. Flow<List<Producto>> emite cambios
    ↓
7. ProductoViewModel observa Flow
    ↓
8. MainActivity observa LiveData/Flow
    ↓
9. adapter.submitList(productos)
    ↓
10. RecyclerView renderiza tarjetas
    ↓
11. Usuario ve productos en pantalla ✅
```

**Ventaja**: Si no hay internet, pasos 4-5 fallan pero paso 6 IGUAL funciona (muestra datos locales).

---

## 📊 Resumen de Archivos por Tipo

| Tipo | Cantidad | Ejemplos |
|------|----------|----------|
| **Activities** | 7 | Login, Main, Category, Cart, Profile, Camera, ProductDetail |
| **ViewModels** | 2 | ProductoViewModel, CarritoViewModel |
| **Repositories** | 2 | ProductoRepository, CarritoRepository |
| **DAOs** | 2 | ProductoDao, CarritoDao |
| **Entities** | 2 | Producto, CarritoItem |
| **Adapters** | 5 | ProductoAdapter, CartAdapter, CategoryAdapter, etc |
| **Layouts XML** | 14+ | Activities + items + bottom_sheet |
| **API Service** | 1 | SupabaseApiService |
| **Database** | 1 | AppDatabase (Room, versión 3) |

---

## 🎯 Principios Arquitectónicos Implementados

1. **MVVM**: Separación clara View - ViewModel - Model
2. **Offline-first**: App funciona sin internet (Room como caché)
3. **Single Source of Truth**: Room es la fuente de verdad, no la API
4. **Repository Pattern**: Abstrae fuentes de datos
5. **Dependency Injection**: Factory pattern para ViewModels
6. **Reactive Programming**: Flow + LiveData para UI reactiva
7. **Clean Architecture**: Capas domain → data → ui
8. **Material Design 3**: UI moderna y consistente

---

## ✅ Estado Actual del Proyecto

**Completado al 95%**:
- ✅ Arquitectura MVVM completa
- ✅ Persistencia local (Room v3)
- ✅ API remota (Retrofit + Supabase)
- ✅ 7 pantallas funcionales
- ✅ Carrito persistente
- ✅ Sistema de categorías
- ✅ Cámara nativa (CameraX)
- ✅ UI pulida (Material3, dark theme, español)

**Pendiente**:
- [ ] CRUD completo de productos en UI
- [ ] Subida de imágenes a Supabase Storage
- [ ] Autenticación real (Supabase Auth)
- [ ] Checkout flow completo

---

**Última actualización**: 2025-11-12  
**Versión Base de Datos**: 3  
**APK Size**: ~7.7 MB (Debug)
