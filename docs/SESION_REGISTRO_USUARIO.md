# 📝 Sesión: Implementación de Registro de Usuario

**Fecha:** 2025-11-16  
**Última actualización:** 2025-11-17 04:28  
**Objetivo:** Crear sistema de registro y autenticación local (Room + SharedPreferences)  
**Estado:** 🟢 Completado (8/8 pasos completados - 100%)

---

## 🎯 **PLAN GENERAL**

### **Opción Elegida: A - Rápido y Funcional (Local)**
- ✅ Room + SharedPreferences
- ✅ Sin internet requerido
- ✅ Migrar a Supabase Auth después (producción)

---

## 🗺️ **ROADMAP COMPLETO**

```
1. ✅ Modelo Usuario (Usuario.kt)           ← ¿Qué datos tiene un usuario?
2. ✅ DAO Usuario (UsuarioDao.kt)           ← ¿Cómo guardamos/leemos usuarios?
3. ✅ Actualizar Base de Datos (v3 → v4)   ← Agregar tabla usuarios
4. ✅ PreferencesManager                    ← Recordar quién está logueado
5. ✅ Layout de Registro (XML)              ← Diseño de la pantalla
6. ✅ RegisterActivity (Kotlin)             ← Lógica de registro
7. ✅ Conectar Login ↔ Register             ← Navegación entre pantallas
8. ✅ Validar en LoginActivity              ← Login real con Room
```

---

## ✅ **PASO 1 COMPLETADO: Modelo Usuario**

### 📍 **Ubicación:**
```
app/src/main/java/com/keylab/mobile/domain/model/Usuario.kt
```

### 📝 **Código Implementado:**

```kotlin
package com.keylab.mobile.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Entidad Usuario para autenticación local
 * 
 * Esta clase representa un usuario registrado en la app.
 * Room la convierte en una tabla SQLite automáticamente.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val nombre: String,
    val email: String,
    val password: String,
    
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String? = null
)
```

### 💡 **Conceptos Clave Aprendidos:**

#### **1. `@Entity(tableName = "usuarios")`**
- Le dice a Room: "Esta clase es una TABLA en SQLite"
- `tableName` define el nombre de la tabla en la base de datos

#### **2. `@PrimaryKey(autoGenerate = true)`**
- El "DNI" único de cada usuario
- `autoGenerate = true` → Room asigna 1, 2, 3... automáticamente

#### **3. `data class`**
- Clase especial de Kotlin para representar datos
- Genera automáticamente: `equals()`, `hashCode()`, `toString()`, `copy()`

#### **4. `@ColumnInfo(name = "fecha_registro")`**
- Convierte `fechaRegistro` (camelCase) a `fecha_registro` (snake_case)
- SQLite usa snake_case, Kotlin usa camelCase

#### **5. `String?` (con interrogación)**
- Significa "puede ser null" (opcional)
- `avatarUrl: String? = null` → Campo opcional con valor por defecto

#### **6. `Long` para fechas**
- Timestamp de Unix (milisegundos desde 1970)
- Ocupa menos espacio que String
- Fácil de ordenar y comparar

---

## ✅ **PASO 2 COMPLETADO: DAO Usuario**

### 📍 **Ubicación:**
```
app/src/main/java/com/keylab/mobile/data/local/UsuarioDao.kt
```

### 📝 **Código Implementado:**

```kotlin
package com.keylab.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import com.keylab.mobile.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones con usuarios
 */
@Dao
interface UsuarioDao {
    
    // ═══════════════════════════════════════════════════════
    // OPERACIONES BÁSICAS
    // ═══════════════════════════════════════════════════════
    
    /**
     * Inserta un nuevo usuario en la base de datos
     * Retorna: El ID del usuario insertado
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long
    
    /**
     * Busca un usuario por su email (para login)
     * Flow<Usuario?> → Observador reactivo, puede ser null
     */
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun obtenerPorEmail(email: String): Flow<Usuario?>
    
    /**
     * Busca un usuario por su ID (para perfil)
     */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun obtenerPorId(id: Int): Flow<Usuario?>
    
    /**
     * Valida credenciales de login
     * Busca usuario con email Y password correctos
     * Si existe → Login exitoso
     * Si no existe → Credenciales incorrectas
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    fun validarLogin(email: String, password: String): Flow<Usuario?>
    
    /**
     * Verifica si un email ya está registrado
     * Retorna: true si existe, false si no existe
     */
    @Query("SELECT COUNT(*) > 0 FROM usuarios WHERE email = :email")
    fun emailExiste(email: String): Flow<Boolean>
    
    /**
     * Obtiene todos los usuarios registrados
     * Útil para pantalla de administración
     */
    @Query("SELECT * FROM usuarios ORDER BY fecha_registro DESC")
    fun obtenerTodos(): Flow<List<Usuario>>
    
    /**
     * Cuenta cuántos usuarios hay registrados
     */
    @Query("SELECT COUNT(*) FROM usuarios")
    fun contarUsuarios(): Flow<Int>
    
    // ═══════════════════════════════════════════════════════
    // OPERACIONES FUTURAS (ADMINISTRADOR)
    // ═══════════════════════════════════════════════════════
    
    /**
     * Elimina un usuario por su objeto completo
     */
    @Delete
    suspend fun eliminar(usuario: Usuario)
    
    /**
     * Elimina un usuario por su ID directamente
     * Ventaja: No necesitas obtener el objeto Usuario completo
     */
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
    
    /**
     * Actualiza datos de un usuario existente
     */
    @Update
    suspend fun actualizar(usuario: Usuario)
    
    /**
     * Actualiza solo la contraseña de un usuario
     */
    @Query("UPDATE usuarios SET password = :nuevaPassword WHERE id = :id")
    suspend fun actualizarPassword(id: Int, nuevaPassword: String)
    
    /**
     * Actualiza solo el avatar de un usuario
     */
    @Query("UPDATE usuarios SET avatar_url = :avatarUrl WHERE id = :id")
    suspend fun actualizarAvatar(id: Int, avatarUrl: String?)
}
```

### 💡 **Conceptos Clave Aprendidos:**

#### **1. `interface` vs `class`**
- **interface** → Solo define "QUÉ" hacer (firma de funciones)
- Room genera el código SQL automáticamente
- Tú no escribes la implementación

#### **2. `Flow<T>` (Río de datos)**
```
Base de datos → Flow → Tu código
Si la BD cambia → Flow emite nuevo valor → UI se actualiza automáticamente
```

#### **3. `suspend` (No bloquear la UI)**
- Funciones asíncronas que NO congelan la app
- Room ejecuta operaciones en segundo plano

#### **4. `OnConflictStrategy.REPLACE`**
- Si insertas email duplicado → Reemplaza el usuario antiguo
- Otras opciones: `IGNORE`, `ABORT`

#### **5. `Usuario?` (con interrogación)**
- Puede devolver null si no encuentra el usuario
- Útil para validar existencia

---

## 📊 **RESUMEN DE FUNCIONES DEL DAO**

| Función | ¿Para qué sirve? | Retorna |
|---------|------------------|---------|
| `insertar()` | Registrar usuario nuevo | ID del usuario (Long) |
| `obtenerPorEmail()` | Buscar por email | Flow<Usuario?> |
| `obtenerPorId()` | Cargar perfil | Flow<Usuario?> |
| `validarLogin()` | Login (email + pass) | Flow<Usuario?> |
| `emailExiste()` | Verificar duplicados | Flow<Boolean> |
| `obtenerTodos()` | Lista completa (admin) | Flow<List<Usuario>> |
| `contarUsuarios()` | Estadísticas | Flow<Int> |
| `eliminar()` | Borrar usuario | void |
| `actualizar()` | Modificar datos | void |

---

## 🎓 **LECCIONES IMPORTANTES**

### **1. Arquitectura en Capas**

```
┌─────────────────────────────────────────────────┐
│         CAPAS DE LA APLICACIÓN                  │
├─────────────────────────────────────────────────┤
│                                                 │
│  Activity (UI)                                  │
│  ↓                                              │
│  ViewModel (Lógica de presentación)            │
│  ↓                                              │
│  Repository (Lógica de negocio)                │
│  ↓                                              │
│  DAO (Operaciones SQL) ← LO QUE HICIMOS HOY    │
│  ↓                                              │
│  Room (SQLite)                                  │
│  ↓                                              │
│  Base de Datos                                  │
│                                                 │
└─────────────────────────────────────────────────┘
```

### **2. ¿Dónde va cada cosa?**

```
┌────────────────────────────────────────────────────┐
│  ¿Necesito guardar/leer/actualizar/eliminar       │
│  algo en la BASE DE DATOS?                        │
│  ✅ SÍ → Lo agregas en el DAO                     │
│  ❌ NO → Va en otra parte                         │
└────────────────────────────────────────────────────┘
```

**Ejemplos:**
- ¿Insertar usuario? → **DAO**
- ¿Validar que las contraseñas coincidan? → **Repository o ViewModel**
- ¿Mostrar mensaje de error? → **Activity**
- ¿Verificar permisos de admin? → **Repository**

### **3. Flow vs LiveData**

| Característica | Flow | LiveData |
|----------------|------|----------|
| **Reactivo** | ✅ Sí | ✅ Sí |
| **Lifecycle-aware** | ❌ No (manual) | ✅ Automático |
| **Operadores** | ✅ Muchos (map, filter) | ⚠️ Limitados |
| **Uso típico** | Room queries, APIs | Estados UI simples |

---

## ✅ **PASO 3 COMPLETADO: Actualizar Base de Datos (v3 → v4)**

### 📍 **Ubicación:**
```
app/src/main/java/com/keylab/mobile/data/local/AppDatabase.kt
```

### 📝 **Cambios Implementados:**

```kotlin
package com.keylab.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keylab.mobile.domain.model.CarritoItem
import com.keylab.mobile.domain.model.Producto
import com.keylab.mobile.domain.model.Usuario  // ← NUEVO

@Database(
    entities = [Producto::class, CarritoItem::class, Usuario::class],  // ← CAMBIO
    version = 4,  // ← CAMBIO (antes era 3)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productoDao(): ProductoDao
    abstract fun carritoDao(): CarritoDao
    abstract fun usuarioDao(): UsuarioDao  // ← NUEVO
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keylab_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

### 💡 **Conceptos Clave Aprendidos:**

#### **1. Versioning de Base de Datos**
- Cada cambio de schema (estructura) incrementa la versión
- v3 → v4 porque agregamos una nueva tabla
- Room detecta el cambio y actualiza la BD

#### **2. `.fallbackToDestructiveMigration()`**
```
Room detecta: version 3 → version 4
    ↓
¿Existe una migración definida? NO
    ↓
.fallbackToDestructiveMigration() → "Borra todo"
    ↓
1. DROP todas las tablas existentes
2. CREATE todas las tablas desde cero
    ↓
Resultado: BD limpia, CERO datos
```

**⚠️ Solo para desarrollo** - En producción necesitas migraciones para conservar datos.

#### **3. Migración Manual (Producción)**
```kotlin
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS usuarios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "email TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "fecha_registro INTEGER NOT NULL, " +
            "avatar_url TEXT)"
        )
    }
}
```

#### **4. Array de Entities**
- `entities = [...]` → Lista de todas las tablas
- Cada `@Entity` se convierte en una tabla SQLite
- Room genera el SQL automáticamente

#### **5. Abstract DAOs**
- Room genera la implementación automáticamente
- Tú solo defines la firma: `abstract fun usuarioDao(): UsuarioDao`

### ✅ **Verificación:**
```bash
./gradlew assembleDebug --no-daemon
# Resultado: BUILD SUCCESSFUL in 15s
```

---

## ✅ **PASO 4 COMPLETADO: PreferencesManager**

### 📍 **Ubicación:**
```
app/src/main/java/com/keylab/mobile/data/local/PreferencesManager.kt
```

### 📝 **Código Implementado:**

```kotlin
package com.keylab.mobile.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de SharedPreferences para guardar datos de sesión
 */
class PreferencesManager(context: Context) {
    
    // Nombres de las claves (constantes)
    companion object {
        private const val PREFS_NAME = "KeyLab_preferences"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    // Instancia de SharedPreferences
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Guarda el ID del usuario que hizo login
     */
    fun guardarSesion(userId: Int) {
        sharedPreferences.edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * Obtiene el ID del usuario logueado
     * Retorna -1 si no hay sesión activa
     */
    fun obtenerUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }
    
    /**
     * Verifica si hay un usuario logueado
     */
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * Cierra la sesión del usuario
     */
    fun cerrarSesion() {
        sharedPreferences.edit().apply {
            remove(KEY_USER_ID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
}
```

### 💡 **Conceptos Clave Aprendidos:**

#### **1. ¿Qué es SharedPreferences?**

Un **archivo XML simple** que guarda datos pequeños en formato `clave-valor`:

```
┌─────────────────────────────────────────┐
│  SharedPreferences                      │
│  (Archivo XML en el teléfono)          │
├─────────────────────────────────────────┤
│  user_id = 5                            │
│  is_logged_in = true                    │
└─────────────────────────────────────────┘
```

**Ubicación física:** `/data/data/com.keylab.mobile/shared_prefs/KeyLab_preferences.xml`

#### **2. Características de SharedPreferences**

| Característica | Detalle |
|----------------|---------|
| **Alcance** | Solo tu app puede leerlo |
| **Persistencia** | Sobrevive al cerrar la app |
| **Tamaño** | Para datos pequeños |
| **Velocidad** | Muy rápido (en memoria) |
| **Formato** | Clave-Valor (XML) |

#### **3. Context.MODE_PRIVATE**

**❌ NO encripta automáticamente**
- Los datos se guardan en texto plano
- Android protege la carpeta: solo tu app tiene acceso (sandboxing)
- Otras apps NO pueden leer tus SharedPreferences

**Niveles de seguridad:**
```
MODE_PRIVATE (usado aquí)
    ✅ Aislamiento entre apps
    ❌ NO encriptado
    👉 Bueno para: IDs, configuraciones

EncryptedSharedPreferences (producción)
    ✅ Encriptado con AES-256
    👉 Bueno para: tokens, datos sensibles
```

#### **4. ¿Por qué solo guardar el ID?**

```kotlin
// ❌ MAL: Guardar objeto completo
preferences.putString("usuario", "{id:5, nombre:'...', email:'...', ...}")

// ✅ BIEN: Solo el ID
preferences.putInt("user_id", 5)
    ↓
Cuando necesites los datos completos → Room
```

**Razones:**
1. **Seguridad** - No guardar contraseñas ni emails
2. **Sincronización** - Si cambian datos, evitas conflictos
3. **Eficiencia** - Menos espacio, más rápido

#### **5. Arquitectura SharedPreferences + Room**

```
┌────────────────────────────────────────────┐
│  SharedPreferences                         │
│  (Solo datos mínimos de sesión)           │
│  - user_id: 5                              │
│  - is_logged_in: true                      │
└────────────────────────────────────────────┘
            ↓ (Si necesitas más datos)
┌────────────────────────────────────────────┐
│  Room Database                             │
│  (Datos completos y actualizados)         │
│  SELECT * FROM usuarios WHERE id = 5       │
│  → Usuario(id=5, nombre="Nico", ...)      │
└────────────────────────────────────────────┘
```

**Regla de oro:**
- SharedPreferences = "¿Quién está logueado?"
- Room = "¿Qué datos tiene esa persona?"

#### **6. companion object**

```kotlin
companion object {
    private const val PREFS_NAME = "KeyLab_preferences"
}
```

- Similar a `static` en Java
- Una sola instancia para toda la clase
- `const val` = Constante (valor inmutable)
- `private` = Solo accesible en esta clase

#### **7. Valor por defecto -1**

```kotlin
return sharedPreferences.getInt(KEY_USER_ID, -1)
```

**¿Por qué -1 y no 0?**
- Room con `autoGenerate = true` genera IDs: 1, 2, 3, 4...
- `-1` es imposible como ID válido
- Sirve como indicador: "No hay sesión activa"

```kotlin
val userId = preferencesManager.obtenerUserId()
if (userId == -1) {
    // No hay sesión → Mostrar LoginActivity
} else {
    // Hay sesión → Cargar datos del usuario
}
```

### 📊 **Funciones del PreferencesManager:**

| Función | ¿Para qué sirve? | Retorna |
|---------|------------------|---------|
| `guardarSesion(userId)` | Iniciar sesión | void |
| `obtenerUserId()` | Obtener ID logueado | Int (-1 si no hay) |
| `isLoggedIn()` | ¿Hay sesión activa? | Boolean |
| `cerrarSesion()` | Logout | void |

### 🔄 **Flujo de uso:**

```
Usuario hace login exitoso
    ↓
preferencesManager.guardarSesion(usuario.id)
    ↓
SharedPreferences guarda:
    user_id = 5
    is_logged_in = true
    ↓
Usuario cierra app y la vuelve a abrir
    ↓
if (preferencesManager.isLoggedIn()) {
    val userId = preferencesManager.obtenerUserId()
    // Cargar datos de Room y navegar a MainActivity
} else {
    // Mostrar LoginActivity
}
```

---

## 📋 **PRÓXIMOS PASOS (Continuación en siguiente sesión)**

### **PASO 5: Layout de Registro (XML)** ✅ COMPLETADO
- Diseño similar a LoginActivity
- Campos: nombre, email, password, confirmar password
- Checkbox términos y condiciones
- **Tiempo real:** 10 minutos
- **Estado:** ✅ Completado

### **PASO 6: RegisterActivity** ✅ COMPLETADO
- Validaciones de formulario
- Verificar email único
- Guardar en Room
- Navegar a MainActivity
- **Tiempo real:** 15 minutos
- **Estado:** ✅ Completado

### **PASO 7: Conectar Login ↔ Register** ✅ COMPLETADO
- Link en LoginActivity → RegisterActivity
- Link en RegisterActivity → LoginActivity
- **Tiempo real:** 5 minutos
- **Estado:** ✅ Completado

### **PASO 8: Validar Login Real** ✅ COMPLETADO
- Modificar LoginActivity
- Usar `validarLogin()` del DAO
- Guardar sesión en PreferencesManager
- Verificar sesión existente al iniciar
- **Tiempo real:** 15 minutos
- **Estado:** ✅ Completado

---

## ✅ **PASO 5 COMPLETADO: Layout de Registro (XML)**

### 📍 **Ubicación:**
```
app/src/main/res/layout/activity_register.xml
```

### 📝 **Elementos del Layout:**
- TextView título "Crear Cuenta"
- TextView subtítulo "Únete a KeyLab hoy"
- TextInputLayout para nombre
- TextInputLayout para email
- TextInputLayout para contraseña
- TextInputLayout para confirmar contraseña
- CheckBox para términos y condiciones
- MaterialButton para registro
- TextView link para ir a login
- ProgressBar para indicador de carga

### 💡 **Conceptos Aplicados:**
- ScrollView para contenido desplazable
- ConstraintLayout para posicionamiento
- Material Design 3 components
- Inputs con validación visual
- Password toggle para ver/ocultar contraseña
- Consistencia de diseño con LoginActivity

---

## ✅ **PASO 6 COMPLETADO: RegisterActivity**

### 📍 **Ubicación:**
```
app/src/main/java/com/keylab/mobile/ui/RegisterActivity.kt
```

### 📝 **Código Implementado (Resumen):**

```kotlin
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager

    // Validaciones:
    // - Nombre no vacío
    // - Email válido y no duplicado
    // - Contraseña mínimo 6 caracteres
    // - Confirmar contraseña coincide
    // - Términos aceptados

    private fun performRegister(nombre: String, email: String, password: String) {
        lifecycleScope.launch {
            // 1. Verificar si email existe
            val emailExists = database.usuarioDao().emailExiste(email).first()
            
            // 2. Crear usuario
            val usuario = Usuario(nombre, email, password)
            val userId = database.usuarioDao().insertar(usuario)
            
            // 3. Guardar sesión
            preferencesManager.guardarSesion(userId.toInt())
            
            // 4. Navegar a MainActivity
            navigateToMain()
        }
    }
}
```

### 💡 **Conceptos Clave:**

#### **1. lifecycleScope.launch**
- Corrutina vinculada al ciclo de vida del Activity
- Se cancela automáticamente si el Activity se destruye
- Evita memory leaks

#### **2. Flow.first()**
- Obtiene el primer valor emitido por el Flow
- Espera hasta que Room devuelva el resultado
- Convierte Flow<Boolean> a Boolean

#### **3. Validación en cascada**
```kotlin
var isValid = true
if (nombre.isEmpty()) { isValid = false }
if (email.isEmpty()) { isValid = false }
// ...
if (isValid) { performRegister() }
```

#### **4. Flags de Intent**
```kotlin
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
```
- `NEW_TASK`: Crea nueva tarea
- `CLEAR_TASK`: Limpia todas las activities anteriores
- Resultado: Usuario no puede volver atrás con botón "Back"

---

## ✅ **PASO 7 COMPLETADO: Conectar Login ↔ Register**

### 📝 **Cambios en LoginActivity:**

```kotlin
binding.tvRegister.setOnClickListener {
    val intent = Intent(this, RegisterActivity::class.java)
    startActivity(intent)
}
```

### 📝 **Cambios en RegisterActivity:**

```kotlin
binding.tvLogin.setOnClickListener {
    finish() // Vuelve a LoginActivity
}
```

### 📝 **Strings agregados:**

```xml
<!-- Register Activity -->
<string name="register_title">Crear Cuenta</string>
<string name="register_subtitle">Únete a KeyLab hoy</string>
<string name="register_name_hint">Nombre completo</string>
<string name="register_email_hint">Correo electrónico</string>
<string name="register_password_hint">Contraseña</string>
<string name="register_confirm_password_hint">Confirmar contraseña</string>
<string name="register_terms_accept">Acepto los términos y condiciones</string>
<string name="register_button">Crear cuenta</string>
<string name="register_have_account_login">¿Ya tienes cuenta? Inicia sesión</string>

<!-- Validation Messages -->
<string name="error_empty_name">El nombre es requerido</string>
<string name="error_empty_email">El correo es requerido</string>
<string name="error_invalid_email">Correo inválido</string>
<string name="error_empty_password">La contraseña es requerida</string>
<string name="error_password_too_short">La contraseña debe tener al menos 6 caracteres</string>
<string name="error_passwords_dont_match">Las contraseñas no coinciden</string>
<string name="error_terms_not_accepted">Debes aceptar los términos y condiciones</string>
<string name="error_email_already_exists">Este correo ya está registrado</string>
<string name="error_registration_failed">Error al crear la cuenta</string>
<string name="success_account_created">¡Cuenta creada exitosamente!</string>
<string name="error_login_failed">Correo o contraseña incorrectos</string>
<string name="error_login_general">Error al iniciar sesión</string>
```

### 📝 **AndroidManifest.xml:**

```xml
<activity
    android:name=".ui.RegisterActivity"
    android:exported="false"
    android:parentActivityName=".ui.LoginActivity" />
```

---

## ✅ **PASO 8 COMPLETADO: Validar Login Real**

### 📝 **Cambios en LoginActivity:**

```kotlin
class LoginActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)
        
        checkExistingSession()
    }

    private fun checkExistingSession() {
        if (preferencesManager.isLoggedIn()) {
            navigateToMain()
        }
    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            val usuario = database.usuarioDao()
                .validarLogin(email, password)
                .first()
            
            if (usuario != null) {
                preferencesManager.guardarSesion(usuario.id)
                Toast.makeText(this@LoginActivity, 
                    "¡Bienvenido ${usuario.nombre}!", 
                    Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this@LoginActivity,
                    getString(R.string.error_login_failed),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
```

### 💡 **Flujo Completo de Autenticación:**

```
┌────────────────────────────────────────────┐
│  App inicia → LoginActivity                │
├────────────────────────────────────────────┤
│  checkExistingSession()                    │
│  ├─ ¿Hay sesión en SharedPreferences?      │
│  │  ├─ SÍ → navigateToMain()              │
│  │  └─ NO → Mostrar pantalla de login     │
└────────────────────────────────────────────┘
         ↓ (Usuario hace login)
┌────────────────────────────────────────────┐
│  performLogin(email, password)             │
│  ├─ Room: validarLogin(email, pass)       │
│  │  ├─ Usuario encontrado → Login exitoso │
│  │  │  ├─ Guardar sesión                  │
│  │  │  ├─ Toast "¡Bienvenido {nombre}!"   │
│  │  │  └─ navigateToMain()                │
│  │  └─ null → Credenciales incorrectas    │
│  │     └─ Toast error                     │
└────────────────────────────────────────────┘
         ↓ (Usuario hace registro)
┌────────────────────────────────────────────┐
│  RegisterActivity                          │
│  ├─ Validar formulario                     │
│  ├─ Verificar email único                  │
│  ├─ Insertar usuario en Room              │
│  ├─ Guardar sesión                         │
│  └─ navigateToMain()                       │
└────────────────────────────────────────────┘
```

---

## 🐛 **FIX APLICADO: usuarioDao() con minúscula**

### **Error encontrado:**
```kotlin
// ❌ INCORRECTO en AppDatabase.kt
abstract fun UsuarioDao(): UsuarioDao

// Error: Unresolved reference: usuarioDao
database.usuarioDao().validarLogin(...)
```

### **Solución:**
```kotlin
// ✅ CORRECTO
abstract fun usuarioDao(): UsuarioDao
```

**Razón:** Kotlin usa convención camelCase para métodos. Room genera la implementación basándose en el nombre del método.

---

## 📋 **PRÓXIMOS PASOS (Mejoras Futuras)**

### **1. Funcionalidad de Logout**
- Agregar botón en ProfileActivity
- Llamar a `preferencesManager.cerrarSesion()`
- Navegar a LoginActivity

### **2. Editar Perfil**
- Permitir cambiar nombre, avatar
- Usar `usuarioDao().actualizar()`
- Actualizar UI en tiempo real con Flow

### **3. Recuperación de Contraseña**
- Enviar email (requiere backend)
- Por ahora: reset manual en Room

### **4. Migración a Supabase Auth**
- Mantener Room como caché offline
- Supabase Auth para producción
- JWT tokens en SharedPreferences (encrypted)

### **5. Encriptación de Contraseñas**
- Implementar bcrypt o usar librería de hashing
- NO guardar contraseñas en texto plano en producción

---

## ⏱️ **TIEMPO INVERTIDO Y RESTANTE**

- **Sesión 1 completada:** ~50 minutos (Paso 1 y 2)
- **Sesión 2 completada:** ~35 minutos (Paso 3 y 4)
- **Sesión 3 completada:** ~45 minutos (Paso 5, 6, 7 y 8)
- **Total invertido:** ~2 horas 10 minutos
- **Total estimado original:** ~2 horas 55 minutos
- **Ahorro de tiempo:** ~45 minutos (eficiencia mejorada)

---

## 🔄 **FLUJO DE REGISTRO COMPLETO (Planificado)**

```
Usuario abre app
    ↓
LoginActivity
    ↓ Click "Regístrate"
RegisterActivity
    ↓ Usuario llena formulario
Validar campos (nombre, email, password)
    ↓
¿Email ya existe?
    ├─ SÍ → Error "Email ya registrado"
    └─ NO → Continuar
        ↓
Insertar en Room (UsuarioDao.insertar)
    ↓
Guardar sesión (PreferencesManager)
    ↓
Toast: "¡Cuenta creada!"
    ↓
Navegar a MainActivity
```

---

## 📚 **PREGUNTAS Y RESPUESTAS DE LA SESIÓN**

### **P1: ¿Por qué usar Long para fechas y no String?**

**R:** 
- `Long` es timestamp de Unix (milisegundos desde 1970)
- Ocupa menos espacio (8 bytes vs ~20 bytes)
- Fácil de ordenar: `ORDER BY fecha_registro DESC`
- Fácil de comparar: `if (fecha1 > fecha2)`

---

### **P2: ¿Por qué validarLogin() devuelve Usuario? y no Boolean?**

**R:**
Si devolviera `Boolean`:
- Solo sabrías: login correcto o incorrecto
- NO sabrías el nombre, ID, avatar del usuario

Al devolver `Usuario?`:
- `null` → Login incorrecto
- Objeto → Login correcto + datos completos del usuario

```kotlin
val usuario = validarLogin(email, pass).first()
if (usuario != null) {
    Toast.makeText("¡Bienvenido ${usuario.nombre}!")
    guardarEnPreferences(usuario.id)
}
```

---

### **P3: ¿Si quiero agregar función para eliminar usuarios (admin), dónde la pongo?**

**R:**
✅ **SÍ, la agregas en el DAO**

```kotlin
@Dao
interface UsuarioDao {
    @Delete
    suspend fun eliminar(usuario: Usuario)
    
    // O por ID directamente:
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
```

**Regla:** Operaciones de base de datos → DAO

---

## 📖 **RECURSOS ÚTILES**

### **Documentación Oficial:**
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)

### **Archivos del Proyecto:**
```
/home/nicolas/u/mobile/KeyLabMobile/
├── CONTEXTO_PROYECTO.md          ← Estado general del proyecto
├── ESTRUCTURA_PROYECTO.md         ← Arquitectura completa
├── docs/
│   ├── FLUJO_DE_DATOS.md         ← Cómo fluyen los datos
│   ├── DIAGRAMA_FLUJO_DATOS.md   ← Diagramas visuales
│   └── SESION_REGISTRO_USUARIO.md ← Este archivo
```

---

## 💾 **ARCHIVOS CREADOS/MODIFICADOS EN ESTAS SESIONES**

### **Sesión 1:**
1. ✅ `app/src/main/java/com/keylab/mobile/domain/model/Usuario.kt` (creado)
2. ✅ `app/src/main/java/com/keylab/mobile/data/local/UsuarioDao.kt` (creado)

### **Sesión 2:**
3. ✅ `app/src/main/java/com/keylab/mobile/data/local/AppDatabase.kt` (modificado - v3 → v4)
4. ✅ `app/src/main/java/com/keylab/mobile/data/local/PreferencesManager.kt` (creado)

### **Sesión 3:**
5. ✅ `app/src/main/res/layout/activity_register.xml` (creado)
6. ✅ `app/src/main/res/values/strings.xml` (modificado - agregados strings)
7. ✅ `app/src/main/java/com/keylab/mobile/ui/RegisterActivity.kt` (creado)
8. ✅ `app/src/main/java/com/keylab/mobile/ui/LoginActivity.kt` (modificado - login real)
9. ✅ `app/src/main/AndroidManifest.xml` (modificado - RegisterActivity registrado)
10. ✅ `app/src/main/java/com/keylab/mobile/data/local/AppDatabase.kt` (fix: usuarioDao())

---

## 🎯 **CHECKLIST PARA LA PRÓXIMA SESIÓN**

Antes de continuar, verifica que tengas:

### **Archivos creados:**
- [x] Usuario.kt creado y sin errores de compilación
- [x] UsuarioDao.kt creado y sin errores de compilación
- [x] AppDatabase.kt actualizado a v4
- [x] PreferencesManager.kt creado y sin errores
- [x] Android Studio sin errores (build exitoso)

### **Entendimiento de conceptos:**
- [x] ¿Qué es un Entity?
- [x] ¿Qué es un DAO?
- [x] ¿Qué es Flow?
- [x] ¿Qué es suspend?
- [x] ¿Qué es SharedPreferences?
- [x] ¿Por qué guardar solo el ID del usuario?
- [x] ¿Qué hace fallbackToDestructiveMigration()?
- [x] ¿Diferencia entre MODE_PRIVATE y EncryptedSharedPreferences?

### **Conceptos para profundizar en próxima sesión:**
- [ ] ¿Por qué usamos -1 como valor por defecto en obtenerUserId()?
  - **Respuesta:** Porque Room genera IDs desde 1, entonces -1 es imposible como ID válido e indica "no hay sesión activa"

---

## 🚀 **COMANDO PARA RETOMAR**

Cuando vuelvas, ejecuta esto para verificar que todo está bien:

```bash
cd /home/nicolas/u/mobile/KeyLabMobile
./gradlew assembleDebug --no-daemon
```

Si hay errores de compilación, resuélvelos antes de continuar con el Paso 5 (Layout de Registro).

---

## 📝 **NOTAS ADICIONALES**

### **Seguridad (Importante para producción):**
- ⚠️ Contraseñas en texto plano NO son seguras
- En producción usar: bcrypt, argon2, o Supabase Auth
- Esta implementación es SOLO para aprendizaje

### **Migración futura a Supabase Auth:**
- El DAO local se mantiene para caché offline
- Supabase Auth maneja autenticación real
- SharedPreferences guarda JWT tokens
- Room guarda datos del usuario (caché)

---

## 📊 **RESUMEN DE SESIÓN 3 (2025-11-17 04:28)**

### **✅ Logros completados:**
1. **Layout de Registro creado** (activity_register.xml)
   - Campos: nombre, email, contraseña, confirmar contraseña
   - CheckBox términos y condiciones
   - Diseño consistente con LoginActivity
   - Material Design 3

2. **RegisterActivity implementado**
   - Validaciones completas de formulario
   - Verificación de email único en Room
   - Inserción de usuario en base de datos
   - Sesión automática después de registro
   - Navegación a MainActivity

3. **LoginActivity actualizado**
   - Login real con Room (validarLogin)
   - Verificación de sesión existente
   - Guardar sesión en SharedPreferences
   - Mensajes personalizados con nombre de usuario

4. **Strings localizados agregados**
   - Mensajes de validación
   - Textos de interfaz de registro
   - Mensajes de error y éxito

5. **RegisterActivity registrado en AndroidManifest**
   - Configurado como hijo de LoginActivity
   - Navegación correcta entre pantallas

6. **Fix aplicado en AppDatabase**
   - Corrección: `UsuarioDao()` → `usuarioDao()`
   - Compilación exitosa

### **🎓 Conceptos aplicados:**
- Corrutinas con lifecycleScope
- Flow.first() para obtener valores únicos
- ViewBinding en RegisterActivity
- Validación en cascada de formularios
- Intent flags para navegación sin retroceso
- Room queries con Flow
- SharedPreferences para persistencia de sesión
- Material Design 3 TextInputLayout
- CheckBox para términos y condiciones

### **📈 Progreso:**
- **8 de 8 pasos completados (100%)**
- **10 archivos creados/modificados**
- **Compilación exitosa sin errores**
- **Sistema de autenticación funcional**

### **🎯 Estado final:**
✅ Sistema de registro y login completamente funcional
✅ Persistencia de sesión entre aperturas de app
✅ Validaciones robustas
✅ UI consistente y moderna
✅ Código limpio y documentado

---

## 📊 **RESUMEN DE SESIÓN 2 (2025-11-16 19:46)**

### **✅ Logros completados:**
1. **AppDatabase actualizado** (v3 → v4)
   - Agregada entidad Usuario
   - Agregado UsuarioDao
   - Base de datos lista para registrar usuarios

2. **PreferencesManager creado**
   - Guardar sesión de usuario
   - Verificar si hay usuario logueado
   - Cerrar sesión
   - Persistencia entre aperturas de app

### **🎓 Conceptos aprendidos:**
- Versionado de bases de datos Room
- Destructive Migration vs Manual Migration
- SharedPreferences y su uso correcto
- MODE_PRIVATE vs EncryptedSharedPreferences
- Arquitectura: SharedPreferences (sesión) + Room (datos completos)
- companion object y constantes en Kotlin
- Por qué usar -1 como valor por defecto

### **📈 Progreso:**
- **4 de 8 pasos completados (50%)**
- **4 archivos creados/modificados**
- **Compilación exitosa sin errores**

### **🎯 Siguiente objetivo:**
Crear el layout de registro (activity_register.xml) con campos para nombre, email, password y confirmar password.

---

**Última actualización:** 2025-11-17 04:28  
**Estado:** ✅ COMPLETADO - Sistema de registro y autenticación funcional  
**Próximos pasos sugeridos:** Implementar logout, editar perfil, migración a Supabase Auth

---

## 📞 **DUDAS O PREGUNTAS**

Si al retomar tienes dudas sobre:
- ¿Por qué hicimos algo de cierta manera?
- ¿Cómo funciona X concepto?
- ¿Dónde va Y archivo?

Revisa las secciones de **"Conceptos Clave"** y **"Preguntas y Respuestas"** arriba.

---

🎓 **Recuerda:** Aprender a programar es como aprender un idioma. La práctica constante es clave!
