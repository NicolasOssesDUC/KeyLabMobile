# 🔄 Flujo de Datos - KeyLab Mobile

## 📐 Arquitectura: MVVM + Repository Pattern

La aplicación usa el patrón **MVVM (Model-View-ViewModel)** con **Repository Pattern** para separar responsabilidades y mantener el código organizado.

```
┌─────────────────────────────────────────────────────────────┐
│                         UI LAYER                            │
│  ┌──────────────┐        ┌──────────────┐                  │
│  │  Activity/   │◄───────│  ViewModel   │                  │
│  │  Fragment    │observe │              │                  │
│  └──────────────┘        └──────┬───────┘                  │
│                                 │calls                      │
└─────────────────────────────────┼─────────────────────────┘
                                  │
┌─────────────────────────────────┼─────────────────────────┐
│                        DATA LAYER│                         │
│                          ┌───────▼───────┐                 │
│                          │  Repository   │                 │
│                          │  (Mediador)   │                 │
│                          └───┬───────┬───┘                 │
│                              │       │                      │
│                    ┌─────────┘       └─────────┐           │
│                    │                            │           │
│            ┌───────▼────────┐         ┌────────▼────────┐  │
│            │  Room (Local)  │         │ Retrofit (API)  │  │
│            │    SQLite      │         │    Supabase     │  │
│            └────────────────┘         └─────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Capas de la Arquitectura

### 1️⃣ **UI Layer (Vista + ViewModel)**
- **Activity/Fragment**: Muestra datos al usuario
- **ViewModel**: Gestiona el estado de la UI y comunica con Repository
- **Comunicación**: Observa `Flow` y `LiveData` reactivos

### 2️⃣ **Data Layer (Repository + Fuentes de Datos)**
- **Repository**: Mediador entre ViewModel y fuentes de datos
- **Room (DAO)**: Base de datos local SQLite
- **Retrofit (API)**: Cliente HTTP para Supabase REST API

---

## 📊 Flujo Completo de Datos

### 🔽 **Flujo de Lectura (Mostrar Productos)**

```
┌─────────────────┐
│  MainActivity   │ 1. Usuario abre la app
└────────┬────────┘
         │ onCreate()
         │ viewModel.sincronizarProductos()
         ▼
┌─────────────────────┐
│ ProductoViewModel   │ 2. ViewModel recibe petición
└─────────┬───────────┘
          │ sincronizarProductos()
          │ viewModelScope.launch { ... }
          ▼
┌──────────────────────┐
│ ProductoRepository   │ 3. Repository coordina fuentes
└─────────┬────────────┘
          │
    ╔═════╩═════╗
    ║           ║
    ▼           ▼
┌────────┐  ┌──────────┐
│ Room   │  │ Retrofit │ 4. Consulta Room Y Supabase
│ (DAO)  │  │  (API)   │
└───┬────┘  └────┬─────┘
    │            │
    │            │ HTTP GET /productos
    │            │
    │            ▼
    │      ┌───────────┐
    │      │ Supabase  │ 5. Servidor responde JSON
    │      │   API     │
    │      └─────┬─────┘
    │            │ Response<List<Producto>>
    │            │
    │◄───────────┘
    │ dao.insertarTodos(productos) 6. Guarda en Room
    │
    ▼
┌──────────────────┐
│   Flow emit      │ 7. Flow emite nuevo valor
└────────┬─────────┘
         │ Flow<List<Producto>>
         │
         ▼
┌─────────────────────┐
│ ProductoViewModel   │ 8. ViewModel recibe datos
│   .productos        │
└─────────┬───────────┘
          │ Flow observado por UI
          │
          ▼
┌─────────────────┐
│  MainActivity   │ 9. UI se actualiza automáticamente
│  RecyclerView   │    (collect o observe)
└─────────────────┘
```

---

## 🔍 Explicación Detallada por Componentes

### 📱 **1. MainActivity (UI)**
**Ubicación**: `/app/src/main/java/com/keylab/mobile/ui/MainActivity.kt`

```kotlin
// 1️⃣ INICIALIZACIÓN: Crear ViewModel con dependencias
private val viewModel: ProductoViewModel by viewModels {
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = ProductoRepository(
        dao = database.productoDao(),
        api = RetrofitClient.apiService
    )
    ProductoViewModelFactory(repository)
}

override fun onCreate(savedInstanceState: Bundle?) {
    // ...
    setupObservers()              // 2️⃣ Configurar observadores
    viewModel.sincronizarProductos() // 3️⃣ Disparar sincronización
}

// 4️⃣ OBSERVAR DATOS (reactivo)
private fun setupObservers() {
    lifecycleScope.launch {
        viewModel.productos.collect { productos ->
            // ✅ UI se actualiza automáticamente cuando cambian los datos
            productoAdapter.submitList(productos)
        }
    }
}
```

**¿Qué hace?**
1. Crea el ViewModel con sus dependencias (Database + API)
2. Solicita sincronizar productos al iniciar
3. Observa el `Flow` de productos reactivamente
4. Cuando llegan nuevos datos, actualiza el RecyclerView

---

### 🎯 **2. ProductoViewModel (Lógica UI)**
**Ubicación**: `/app/src/main/java/com/keylab/mobile/ui/viewmodel/ProductoViewModel.kt`

```kotlin
class ProductoViewModel(
    private val repository: ProductoRepository
) : ViewModel() {
    
    // ✅ Flow reactivo: UI se actualiza automáticamente
    val productos: Flow<List<Producto>> = repository.obtenerProductos()
    
    // 🔄 Estado de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    // ⚠️ Mensajes de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    // 🚀 SINCRONIZAR PRODUCTOS
    fun sincronizarProductos() {
        viewModelScope.launch {
            repository.sincronizarProductos().collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        _isLoading.value = true  // Mostrar loading
                    }
                    is ApiResponse.Success -> {
                        _isLoading.value = false
                        // ✅ Datos ya están en Room, Flow se actualiza solo
                    }
                    is ApiResponse.Error -> {
                        _isLoading.value = false
                        _error.value = response.message
                    }
                }
            }
        }
    }
}
```

**¿Qué hace?**
1. **Expone datos** a la UI mediante `Flow` (productos)
2. **Gestiona estados** (loading, errores) mediante `LiveData`
3. **Coordina operaciones** llamando al Repository
4. **Sobrevive a rotaciones** de pantalla (no se pierde el estado)

---

### 🗂️ **3. ProductoRepository (Mediador)**
**Ubicación**: `/app/src/main/java/com/keylab/mobile/data/repository/ProductoRepository.kt`

```kotlin
class ProductoRepository(
    private val dao: ProductoDao,        // Room (local)
    private val api: SupabaseApiService   // Retrofit (remoto)
) {
    
    // ═══ ESTRATEGIA: OFFLINE-FIRST ═══
    
    // 📖 LECTURA: Siempre desde Room (rápido, funciona sin internet)
    fun obtenerProductos(): Flow<List<Producto>> = dao.obtenerTodos()
    
    // 🔄 SINCRONIZACIÓN: API → Room
    fun sincronizarProductos(): Flow<ApiResponse<List<Producto>>> = flow {
        emit(ApiResponse.Loading)
        
        try {
            // 1️⃣ Request HTTP a Supabase
            val response = api.obtenerProductos()
            
            if (response.isSuccessful) {
                val productos = response.body() ?: emptyList()
                
                // 2️⃣ Guardar en Room (reemplaza si existen)
                dao.insertarTodos(productos)
                
                // 3️⃣ Flow de dao.obtenerTodos() emite automáticamente
                //    nuevo valor → UI se actualiza
                
                emit(ApiResponse.Success(productos))
            } else {
                emit(ApiResponse.Error("Error ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Error"))
        }
    }.flowOn(Dispatchers.IO)  // Ejecutar en hilo IO
    
    // ✏️ CREAR: API → Room
    suspend fun crearProducto(producto: Producto): ApiResponse<Producto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.crearProducto(producto)
                
                if (response.isSuccessful) {
                    val productoCreado = response.body()?.firstOrNull()
                    
                    if (productoCreado != null) {
                        dao.insertar(productoCreado)  // Room se actualiza
                        ApiResponse.Success(productoCreado)
                    } else {
                        ApiResponse.Error("Respuesta vacía")
                    }
                } else {
                    ApiResponse.Error("Error ${response.code()}")
                }
            } catch (e: Exception) {
                ApiResponse.Error(e.message ?: "Error")
            }
        }
}
```

**¿Qué hace?**
1. **Decide de dónde leer datos**: Room (local) o API (remoto)
2. **Sincroniza datos**: API → Room para tener caché actualizada
3. **Maneja errores**: Devuelve `ApiResponse` con Success/Error/Loading
4. **Ejecuta en background**: Usa `Dispatchers.IO` y `withContext`

**🎯 Estrategia OFFLINE-FIRST:**
- ✅ Lectura: Siempre desde Room (rápido, funciona sin internet)
- 🔄 Escritura: API primero, luego actualiza Room
- 📡 Sincronización: Periódica en background

---

### 🌐 **4. Retrofit (Cliente HTTP)**
**Ubicación**: `/app/src/main/java/com/keylab/mobile/data/remote/`

#### **RetrofitClient.kt** (Configuración)
```kotlin
object RetrofitClient {
    private const val BASE_URL = "${BuildConfig.SUPABASE_URL}/rest/v1/"
    
    // Interceptor: Agrega headers de autenticación
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", BuildConfig.SUPABASE_KEY)
            .addHeader("Authorization", "Bearer ${BuildConfig.SUPABASE_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }
    
    // Cliente OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)  // Logs en debug
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Instancia Retrofit
    val apiService: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}
```

#### **SupabaseApiService.kt** (Endpoints)
```kotlin
interface SupabaseApiService {
    
    // GET /productos
    @GET("productos")
    suspend fun obtenerProductos(
        @Query("select") select: String = "*"
    ): Response<List<Producto>>
    
    // GET /productos?id=eq.{id}
    @GET("productos")
    suspend fun obtenerProductoPorId(
        @Query("id") id: String  // "eq.5"
    ): Response<List<Producto>>
    
    // POST /productos
    @Headers("Prefer: return=representation")
    @POST("productos")
    suspend fun crearProducto(
        @Body producto: Producto
    ): Response<List<Producto>>
    
    // PATCH /productos?id=eq.{id}
    @Headers("Prefer: return=representation")
    @PATCH("productos")
    suspend fun actualizarProducto(
        @Query("id") id: String,  // "eq.5"
        @Body producto: Producto
    ): Response<List<Producto>>
    
    // DELETE /productos?id=eq.{id}
    @DELETE("productos")
    suspend fun eliminarProducto(
        @Query("id") id: String   // "eq.5"
    ): Response<Unit>
}
```

**¿Qué hace?**
1. **Configura conexión HTTP** con Supabase
2. **Agrega autenticación** automáticamente (interceptor)
3. **Define endpoints** como funciones Kotlin
4. **Convierte JSON** a objetos Producto (Gson)

---

### 💾 **5. Room (Base de Datos Local)**
**Ubicación**: `/app/src/main/java/com/keylab/mobile/data/local/`

#### **AppDatabase.kt** (Configuración)
```kotlin
@Database(
    entities = [Producto::class, CarritoItem::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keylab_database"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

#### **ProductoDao.kt** (Operaciones)
```kotlin
@Dao
interface ProductoDao {
    
    // Reactivo: Emite nuevos valores cuando cambia la tabla
    @Query("SELECT * FROM productos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Producto>>
    
    // Insertar múltiples (sincronización masiva)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<Producto>)
    
    // Buscar por nombre (parcial)
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :busqueda || '%'")
    fun buscarPorNombre(busqueda: String): Flow<List<Producto>>
    
    // Filtrar por categoría
    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>
}
```

**¿Qué hace?**
1. **Crea base de datos SQLite** en `/data/data/com.keylab.mobile/databases/`
2. **Define operaciones CRUD** con anotaciones (@Query, @Insert, etc.)
3. **Emite cambios reactivos** mediante `Flow` (observadores se actualizan solos)
4. **Ejecuta en background** (todas las funciones son `suspend`)

---

## 🔄 Tipos de Flujos de Datos

### **A. Sincronización Inicial (App Start)**

```
Usuario abre app
      │
      ▼
MainActivity.onCreate()
      │
      ▼
viewModel.sincronizarProductos()
      │
      ├─────► repository.sincronizarProductos()
      │              │
      │              ├─► api.obtenerProductos()
      │              │         │
      │              │         ▼
      │              │   Supabase responde JSON
      │              │         │
      │              │         ▼
      │              └─► dao.insertarTodos(productos)
      │                        │
      │                        ▼
      │                  Room actualiza tabla
      │                        │
      │                        ▼
      └──────◄────────── Flow emite nuevos datos
                               │
                               ▼
                         UI se actualiza
```

### **B. Búsqueda/Filtrado (Local)**

```
Usuario escribe en SearchBar
      │
      ▼
viewModel.buscarProductos(query)
      │
      ▼
repository.buscarProductos(query)
      │
      ▼
dao.buscarPorNombre(query)
      │ SQL: SELECT * WHERE nombre LIKE '%query%'
      │
      ▼
Flow emite resultados filtrados
      │
      ▼
UI actualiza RecyclerView
```

### **C. Agregar al Carrito (Solo Local)**

```
Usuario pulsa "Agregar al Carrito"
      │
      ▼
carritoViewModel.agregarProducto(producto)
      │
      ▼
carritoRepository.agregarProducto(producto)
      │
      ├─► dao.obtenerPorProductoId(id)
      │         │
      │         ├─► SI existe: dao.actualizarCantidad(id, cantidad+1)
      │         │
      │         └─► SI NO existe: dao.insertar(nuevoItem)
      │
      ▼
Room actualiza tabla carrito_items
      │
      ▼
Flow<List<CarritoItem>> emite nuevo valor
      │
      ▼
Badge del carrito se actualiza
```

### **D. Crear Producto (Requiere API)**

```
Usuario completa formulario y pulsa "Crear"
      │
      ▼
viewModel.crearProducto(producto)
      │
      ▼
repository.crearProducto(producto)
      │
      ├─► api.crearProducto(producto)
      │         │ POST /productos
      │         │
      │         ▼
      │   Supabase inserta y devuelve producto con ID
      │         │
      │         ▼
      └─► dao.insertar(productoCreado)
            │
            ▼
      Room actualiza tabla
            │
            ▼
      Flow emite nuevo valor
            │
            ▼
      UI muestra producto nuevo
```

---

## 🎭 Estados de la UI

### **ApiResponse (Sealed Class)**
```kotlin
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}
```

### **Manejo en ViewModel**
```kotlin
fun sincronizarProductos() {
    viewModelScope.launch {
        repository.sincronizarProductos().collect { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    _isLoading.value = true     // Mostrar ProgressBar
                    _error.value = null
                }
                is ApiResponse.Success -> {
                    _isLoading.value = false    // Ocultar ProgressBar
                    // Datos ya en Room, Flow los emite
                }
                is ApiResponse.Error -> {
                    _isLoading.value = false    // Ocultar ProgressBar
                    _error.value = response.message  // Mostrar Toast/Snackbar
                }
            }
        }
    }
}
```

### **Observación en UI**
```kotlin
// Observar estado de carga
viewModel.isLoading.observe(this) { isLoading ->
    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
}

// Observar errores
viewModel.error.observe(this) { error ->
    error?.let {
        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        viewModel.clearError()
    }
}
```

---

## 🔄 Flow vs LiveData

| Característica | Flow | LiveData |
|----------------|------|----------|
| **Reactivo** | ✅ Sí | ✅ Sí |
| **Lifecycle-aware** | ❌ No (manual) | ✅ Automático |
| **Transformaciones** | ✅ map, filter, combine | ⚠️ Limitadas |
| **Backpressure** | ✅ Soportado | ❌ No |
| **Operadores** | ✅ Muchos | ⚠️ Pocos |
| **Uso típico** | Room queries, APIs | Estados UI simples |

**En este proyecto:**
- **Flow**: Datos de Room (productos, carrito)
- **LiveData**: Estados UI (loading, errores, mensajes)

---

## 🚀 Ventajas de esta Arquitectura

### ✅ **Separación de Responsabilidades**
- UI solo muestra datos
- ViewModel maneja lógica de UI
- Repository coordina fuentes de datos
- DAOs/APIs solo acceden a datos

### ✅ **Testeable**
```kotlin
// Test de Repository (sin UI)
@Test
fun `sincronizar productos guarda en Room`() = runBlocking {
    val mockApi = mock<SupabaseApiService>()
    val mockDao = mock<ProductoDao>()
    
    val repository = ProductoRepository(mockDao, mockApi)
    repository.sincronizarProductos()
    
    verify(mockDao).insertarTodos(any())
}
```

### ✅ **Reactivo**
- Cambios en Room → UI se actualiza automáticamente
- No hay necesidad de refrescar manualmente

### ✅ **Offline-First**
- App funciona sin internet (lee de Room)
- Sincronización en background cuando hay conexión

### ✅ **Mantenible**
- Cambiar API no afecta UI (solo Repository)
- Cambiar UI no afecta lógica de datos

---

## 📦 Ejemplo Real: Carrito de Compras

### **CarritoRepository** (Solo Local)
```kotlin
class CarritoRepository(private val dao: CarritoDao) {
    
    // Flow reactivo
    fun obtenerItems(): Flow<List<CarritoItem>> = dao.obtenerItems()
    
    // Agregar producto
    suspend fun agregarProducto(producto: Producto) {
        withContext(Dispatchers.IO) {
            val existente = dao.obtenerPorProductoId(producto.id)
            
            if (existente != null) {
                // Incrementar cantidad
                dao.actualizarCantidad(producto.id, existente.cantidad + 1)
            } else {
                // Nuevo item
                val item = CarritoItem(
                    productoId = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    categoria = producto.categoria,
                    imagenUrl = producto.imagenUrl,
                    cantidad = 1
                )
                dao.insertar(item)
            }
        }
    }
}
```

### **CarritoViewModel**
```kotlin
class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {
    
    // Flow automático desde Room
    val items: Flow<List<CarritoItem>> = repository.obtenerItems()
    
    // Cálculos reactivos
    val subtotal: Flow<Double> = repository.obtenerSubtotal().map { it ?: 0.0 }
    
    val costoEnvio: Flow<Double> = subtotal.map { subtotal ->
        if (subtotal > 50000) 0.0 else 3990.0
    }
    
    val total: Flow<Double> = subtotal.map { subtotalValue ->
        val envio = if (subtotalValue > 50000) 0.0 else 3990.0
        subtotalValue + envio
    }
    
    // Operaciones
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.agregarProducto(producto)
        }
    }
}
```

### **CartActivity** (UI)
```kotlin
// Observar items
lifecycleScope.launch {
    viewModel.items.collect { items ->
        adapter.submitList(items)
        binding.emptyView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }
}

// Observar total (reactivo)
lifecycleScope.launch {
    viewModel.total.collect { total ->
        binding.tvTotal.text = "Total: ${NumberFormat.getCurrencyInstance().format(total)}"
    }
}

// Agregar producto
binding.btnAdd.setOnClickListener {
    viewModel.agregarProducto(producto)
    Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show()
}
```

**🎯 Magia del Flow:**
- Usuario agrega producto → Room se actualiza
- Flow emite nuevo valor → Total se recalcula automáticamente
- UI se actualiza sin código adicional

---

## 🎓 Resumen Visual

```
┌─────────────────────────────────────────────────────────────┐
│                  FLUJO DE DATOS COMPLETO                     │
└─────────────────────────────────────────────────────────────┘

1️⃣ Usuario interactúa con UI
         │
         ▼
2️⃣ Activity/Fragment llama a ViewModel
         │
         ▼
3️⃣ ViewModel ejecuta operación en viewModelScope
         │
         ▼
4️⃣ Repository decide: ¿Local o Remoto?
         │
         ├─────► LOCAL (Room): Lectura/escritura directa
         │                     Flow emite cambios
         │
         └─────► REMOTO (API): HTTP request a Supabase
                                 │
                                 ▼
                           Response JSON
                                 │
                                 ▼
                           Guardar en Room
                                 │
                                 ▼
                           Flow emite cambios
         │
         ▼
5️⃣ Flow/LiveData notifica cambios
         │
         ▼
6️⃣ UI se actualiza automáticamente (collect/observe)
```

---

## 📚 Archivos Importantes

| Archivo | Responsabilidad |
|---------|----------------|
| `MainActivity.kt` | UI principal, observa datos |
| `ProductoViewModel.kt` | Lógica UI productos |
| `CarritoViewModel.kt` | Lógica UI carrito |
| `ProductoRepository.kt` | Mediador productos (Room + API) |
| `CarritoRepository.kt` | Mediador carrito (solo Room) |
| `RetrofitClient.kt` | Configuración HTTP |
| `SupabaseApiService.kt` | Endpoints API |
| `AppDatabase.kt` | Configuración Room |
| `ProductoDao.kt` | Queries productos |
| `CarritoDao.kt` | Queries carrito |

---

*Documento creado el 2025-11-10*
