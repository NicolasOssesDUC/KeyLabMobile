# Documentación de Dependencias, Permisos y Storage - KeyLab Mobile

## 📦 Dependencias del Proyecto

### Ubicación de Configuración
- **Archivo principal**: `/app/build.gradle.kts`
- **Archivo raíz**: `/build.gradle.kts`

### Dependencias por Categoría

#### 🔧 AndroidX Core
```kotlin
// Ubicación: app/build.gradle.kts líneas 51-54
implementation("androidx.core:core-ktx:1.12.0")              // Extensiones Kotlin para Android
implementation("androidx.appcompat:appcompat:1.6.1")          // Compatibilidad con versiones antiguas
implementation("com.google.android.material:material:1.11.0") // Material Design Components
implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Layouts responsivos
```

#### 🔄 Lifecycle & ViewModel (Arquitectura MVVM)
```kotlin
// Ubicación: app/build.gradle.kts líneas 57-61
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0") // ViewModels
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")   // LiveData reactivo
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")    // Ciclo de vida
implementation("androidx.activity:activity-ktx:1.8.2")              // Activity extensions
implementation("androidx.fragment:fragment-ktx:1.6.2")              // Fragment extensions
```

#### 🌐 Retrofit (Cliente HTTP para API REST)
```kotlin
// Ubicación: app/build.gradle.kts líneas 64-67
implementation("com.squareup.retrofit2:retrofit:2.9.0")              // Cliente HTTP
implementation("com.squareup.retrofit2:converter-gson:2.9.0")        // Convertidor JSON
implementation("com.squareup.okhttp3:okhttp:4.12.0")                 // Cliente HTTP base
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")    // Logs de red
```
**Uso**: Comunicación con Supabase API para productos y usuarios.

#### 💾 Room (Base de Datos Local SQLite)
```kotlin
// Ubicación: app/build.gradle.kts líneas 70-72
implementation("androidx.room:room-runtime:2.6.1")  // Runtime de Room
implementation("androidx.room:room-ktx:2.6.1")      // Extensiones Kotlin + Coroutines
kapt("androidx.room:room-compiler:2.6.1")           // Procesador de anotaciones
```
**Uso**: Persistencia local de productos y carrito de compras.

#### ⚡ Corrutinas (Programación Asíncrona)
```kotlin
// Ubicación: app/build.gradle.kts líneas 75-76
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Coroutines Android
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")    // Coroutines Core
```
**Uso**: Operaciones asíncronas (red, base de datos).

#### 🖼️ Glide (Carga de Imágenes)
```kotlin
// Ubicación: app/build.gradle.kts líneas 79-80
implementation("com.github.bumptech.glide:glide:4.16.0")      // Librería principal
kapt("com.github.bumptech.glide:compiler:4.16.0")             // Procesador de anotaciones
```
**Uso**: Carga eficiente de imágenes de productos desde URLs.

#### 📸 CameraX (Cámara Nativa)
```kotlin
// Ubicación: app/build.gradle.kts líneas 83-85
implementation("androidx.camera:camera-camera2:1.3.1")      // API Camera2
implementation("androidx.camera:camera-lifecycle:1.3.1")    // Integración con Lifecycle
implementation("androidx.camera:camera-view:1.3.1")         // Vista de cámara
```
**Uso**: Captura de fotos para búsqueda por imagen.

#### 📋 UI Components
```kotlin
// Ubicación: app/build.gradle.kts líneas 88-91
implementation("androidx.recyclerview:recyclerview:1.3.2")                 // Listas reciclables
implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")    // Pull-to-refresh
```

#### 🧪 Testing
```kotlin
// Ubicación: app/build.gradle.kts líneas 94-96
testImplementation("junit:junit:4.13.2")                              // Unit tests
androidTestImplementation("androidx.test.ext:junit:1.1.5")            // Android tests
androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // UI tests
```

---

## 🔐 Permisos de la Aplicación

### Ubicación
**Archivo**: `/app/src/main/AndroidManifest.xml` (líneas 3-8)

### Permisos Declarados

#### 1. INTERNET
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
- **Propósito**: Comunicación con Supabase API
- **Uso**: Obtener productos, autenticación, sincronización
- **Peligrosidad**: Normal (no requiere solicitud explícita)

#### 2. CAMERA
```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-feature android:name="android.hardware.camera" android:required="false"/>
```
- **Propósito**: Captura de fotos para búsqueda por imagen
- **Uso**: CameraActivity para tomar fotos de productos
- **Peligrosidad**: Peligroso (requiere solicitud en runtime Android 6+)
- **Feature**: No obligatorio (app funciona sin cámara)

#### 3. WRITE_EXTERNAL_STORAGE
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28"/>
```
- **Propósito**: Guardar fotos en almacenamiento externo
- **Uso**: Solo en Android 9 (API 28) o inferior
- **Peligrosidad**: Peligroso (requiere solicitud en runtime)
- **Nota**: Desde Android 10+ se usa Scoped Storage

#### 4. READ_EXTERNAL_STORAGE
```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32"/>
```
- **Propósito**: Leer imágenes desde galería
- **Uso**: Solo hasta Android 12L (API 32)
- **Peligrosidad**: Peligroso (requiere solicitud en runtime)
- **Nota**: Desde Android 13+ se usan permisos granulares

### FileProvider (Compartir Archivos)
```xml
<!-- Ubicación: AndroidManifest.xml líneas 19-27 -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```
- **Propósito**: Compartir archivos entre apps de forma segura
- **Configuración**: `/app/src/main/res/xml/file_paths.xml`
- **Uso**: Compartir fotos capturadas con la cámara

---

## 💾 Almacenamiento de Datos (Storage)

### 1. Base de Datos Local (Room - SQLite)

#### Ubicación de Archivos
- **Database**: `/app/src/main/java/com/keylab/mobile/data/local/AppDatabase.kt`
- **DAOs**: `/app/src/main/java/com/keylab/mobile/data/local/`
  - `ProductoDao.kt`
  - `CarritoDao.kt`
- **Entidades**: `/app/src/main/java/com/keylab/mobile/domain/model/`
  - `Producto.kt`
  - `CarritoItem.kt`

#### Configuración de la Base de Datos
```kotlin
// Ubicación: AppDatabase.kt líneas 36-42
Room.databaseBuilder(
    context.applicationContext,
    AppDatabase::class.java,
    "keylab_database"  // ← Nombre de la BD
)
.fallbackToDestructiveMigration() // Recrear si cambia schema
.build()
```

#### Ubicación Física
- **Ruta**: `/data/data/com.keylab.mobile/databases/keylab_database`
- **Acceso**: Solo desde la app (almacenamiento interno privado)
- **Persistencia**: Los datos permanecen entre sesiones

#### Tablas y Estructura

##### Tabla: `productos`
```kotlin
// Entidad: Producto.kt
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val id: Int,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val subcategoria: String?,
    val descripcion: String?,
    val stock: Int,
    @ColumnInfo(name = "imagen_url") val imagenUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String?,
    @ColumnInfo(name = "updated_at") val updatedAt: String?
)
```

**Operaciones CRUD** (ProductoDao.kt):
- ✅ **Lectura**: 
  - `obtenerTodos()`: Flow de todos los productos
  - `obtenerPorId(id)`: Producto específico
  - `buscarPorNombre(busqueda)`: Búsqueda por texto
  - `obtenerPorCategoria(categoria)`: Filtro por categoría
  - `obtenerConStock()`: Solo productos disponibles
  
- ✏️ **Escritura**:
  - `insertar(producto)`: Insertar/actualizar uno
  - `insertarTodos(productos)`: Inserción masiva
  - `actualizar(producto)`: Actualizar existente
  - `eliminar(producto)`: Eliminar uno
  - `eliminarTodos()`: Limpiar tabla

##### Tabla: `carrito_items`
```kotlin
// Entidad: CarritoItem.kt
@Entity(tableName = "carrito_items")
data class CarritoItem(
    @PrimaryKey val productoId: Int,
    val nombre: String,
    val precio: Double,
    val categoria: String,
    val imagenUrl: String?,
    var cantidad: Int = 1,
    val fechaAgregado: Long = System.currentTimeMillis()
)
```

**Operaciones CRUD** (CarritoDao.kt):
- ✅ **Lectura**:
  - `obtenerItems()`: Flow de items en carrito
  - `obtenerPorProductoId(id)`: Item específico
  - `contarItems()`: Cantidad total de items
  - `obtenerSubtotal()`: Suma de precios × cantidad

- ✏️ **Escritura**:
  - `insertar(item)`: Agregar al carrito
  - `actualizarCantidad(id, cantidad)`: Modificar cantidad
  - `eliminar(item)`: Quitar del carrito
  - `vaciarCarrito()`: Eliminar todo

#### Ventajas de Room
- ✅ **Reactivo**: Flow emite cambios automáticos
- ✅ **Type-safe**: Verificación en tiempo de compilación
- ✅ **Offline-first**: Funciona sin internet
- ✅ **Performante**: SQLite optimizado

---

### 2. Almacenamiento de Archivos

#### Configuración de Rutas
**Archivo**: `/app/src/main/res/xml/file_paths.xml`

```xml
<paths>
    <!-- Cache temporal para fotos -->
    <cache-path name="cache" path="." />
    
    <!-- Archivos externos persistentes -->
    <external-files-path name="external_files" path="." />
    
    <!-- Archivos internos persistentes -->
    <files-path name="files" path="." />
</paths>
```

#### Tipos de Almacenamiento

##### 📁 Cache Directory (Temporal)
- **Ruta**: `/data/data/com.keylab.mobile/cache/`
- **Uso**: Fotos temporales de cámara
- **Persistencia**: El sistema puede eliminarlos si necesita espacio
- **Acceso**: Solo la app

##### 📁 Internal Files (Privado Persistente)
- **Ruta**: `/data/data/com.keylab.mobile/files/`
- **Uso**: Archivos privados de la app
- **Persistencia**: Permanece hasta desinstalar app
- **Acceso**: Solo la app

##### 📁 External Files (Compartido Persistente)
- **Ruta**: `/Android/data/com.keylab.mobile/files/`
- **Uso**: Archivos accesibles por otras apps (con permisos)
- **Persistencia**: Permanece hasta desinstalar app
- **Acceso**: Otras apps con permisos

---

### 3. SharedPreferences (Configuración Ligera)

#### Estado Actual
Actualmente **NO se usa SharedPreferences** en el proyecto.

#### Uso Planificado
```kotlin
// Comentario en ProfileActivity.kt línea 0
// TODO: Cargar datos reales del usuario desde SharedPreferences o Supabase Auth
```

#### Ubicación Física (cuando se implemente)
- **Ruta**: `/data/data/com.keylab.mobile/shared_prefs/`
- **Formato**: XML
- **Uso recomendado**: 
  - Preferencias de usuario
  - Tokens de sesión
  - Configuraciones simples

#### Ejemplo de Implementación Futura
```kotlin
// Guardar
val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
prefs.edit()
    .putString("user_token", token)
    .putBoolean("first_launch", false)
    .apply()

// Leer
val token = prefs.getString("user_token", null)
```

---

## 📊 Resumen de Almacenamiento

| Tipo | Ubicación | Persistencia | Uso en App |
|------|-----------|--------------|------------|
| **Room Database** | `/data/data/.../databases/` | Permanente | Productos y carrito |
| **Cache** | `/data/data/.../cache/` | Temporal | Fotos de cámara |
| **Internal Files** | `/data/data/.../files/` | Permanente | Archivos privados |
| **External Files** | `/Android/data/.../files/` | Permanente | Archivos compartibles |
| **SharedPreferences** | `/data/data/.../shared_prefs/` | Permanente | ⚠️ No implementado aún |

---

## 🔒 Seguridad y Consideraciones

### Datos Sensibles en BuildConfig
```kotlin
// Ubicación: app/build.gradle.kts líneas 20-21
buildConfigField("String", "SUPABASE_URL", "\"https://pwnajivbudcwfcordblx.supabase.co\"")
buildConfigField("String", "SUPABASE_KEY", "\"eyJ...\"")
```
⚠️ **Nota de Seguridad**: La clave de Supabase está hardcodeada. Se recomienda:
- Usar variables de entorno
- Implementar ofuscación con ProGuard
- Usar Row Level Security (RLS) en Supabase

### Migraciones de Base de Datos
```kotlin
.fallbackToDestructiveMigration() // Recrear BD si cambia schema
```
⚠️ **Advertencia**: Esto elimina todos los datos al actualizar schema. Para producción, implementar migraciones incrementales:
```kotlin
.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
```

---

## 🔄 Sincronización Online/Offline

### Estrategia Actual
1. **Primera carga**: Obtener productos desde Supabase API (Retrofit)
2. **Caché local**: Guardar en Room Database
3. **Modo offline**: Leer desde Room si no hay internet
4. **Carrito**: Solo local (Room), no se sincroniza con servidor

### Flujo de Datos
```
API (Supabase) → Retrofit → Repository → Room Database → ViewModel → UI
                                    ↓
                                  Flow reactivo
```

---

## 📝 Configuración SDK

```kotlin
// Ubicación: app/build.gradle.kts líneas 9-16
compileSdk = 34      // API usada para compilar
minSdk = 24          // Android 7.0 (Nougat) mínimo
targetSdk = 34       // Android 14 como objetivo
```

**Requisitos**:
- ✅ Java 21 (configurado en líneas 34-40)
- ✅ Kotlin 1.9.22
- ✅ Gradle 8.3.2

---

*Documento generado el 2025-11-10*
*Versión de la app: 1.0 (versionCode 1)*
