# 📝 Sesión: Implementación de Registro de Usuario

**Fecha:** 2025-11-16  
**Última actualización:** 2025-11-16 19:46  
**Objetivo:** Crear sistema de registro y autenticación local (Room + SharedPreferences)  
**Estado:** 🟢 En progreso (4/8 pasos completados - 50%)

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
5. ⏳ Layout de Registro (XML)              ← Diseño de la pantalla
6. ⏳ RegisterActivity (Kotlin)             ← Lógica de registro
7. ⏳ Conectar Login ↔ Register             ← Navegación entre pantallas
8. ⏳ Validar en LoginActivity              ← Login real con Room
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

### **PASO 5: Layout de Registro (XML)**
- Diseño similar a LoginActivity
- Campos: nombre, email, password, confirmar password
- Checkbox términos y condiciones
- **Tiempo estimado:** 30 minutos
- **Estado:** ⏳ Pendiente

### **PASO 6: RegisterActivity**
- Validaciones de formulario
- Verificar email único
- Guardar en Room
- Navegar a MainActivity
- **Tiempo estimado:** 40 minutos
- **Estado:** ⏳ Pendiente

### **PASO 7: Conectar Login ↔ Register**
- Link en LoginActivity → RegisterActivity
- Link en RegisterActivity → LoginActivity
- **Tiempo estimado:** 5 minutos
- **Estado:** ⏳ Pendiente

### **PASO 8: Validar Login Real**
- Modificar LoginActivity
- Usar `validarLogin()` del DAO
- Guardar sesión en PreferencesManager
- **Tiempo estimado:** 20 minutos
- **Estado:** ⏳ Pendiente

---

## ⏱️ **TIEMPO INVERTIDO Y RESTANTE**

- **Sesión 1 completada:** ~50 minutos (Paso 1 y 2)
- **Sesión 2 completada:** ~35 minutos (Paso 3 y 4)
- **Total invertido:** ~1 hora 25 minutos
- **Falta:** ~1 hora 30 minutos (4 pasos restantes)
- **Total estimado:** ~2 horas 55 minutos

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

**Última actualización:** 2025-11-16 19:46  
**Próxima sesión:** Paso 5 - Layout de Registro (XML)

---

## 📞 **DUDAS O PREGUNTAS**

Si al retomar tienes dudas sobre:
- ¿Por qué hicimos algo de cierta manera?
- ¿Cómo funciona X concepto?
- ¿Dónde va Y archivo?

Revisa las secciones de **"Conceptos Clave"** y **"Preguntas y Respuestas"** arriba.

---

🎓 **Recuerda:** Aprender a programar es como aprender un idioma. La práctica constante es clave!
