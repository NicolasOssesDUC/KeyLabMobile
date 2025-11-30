# 📚 Guía de Estudio - KeyLabMobile

**Preparación para evaluaciones y preguntas del profesor**  
**Última actualización:** 2025-11-17

---

## 📋 ÍNDICE

1. [Conceptos Básicos de Android](#conceptos-básicos)
2. [Layouts y Views](#layouts-y-views)
3. [Activities y Lifecycle](#activities-y-lifecycle)
4. [Listeners y Eventos](#listeners-y-eventos)
5. [Room Database](#room-database)
6. [Coroutines y Flow](#coroutines-y-flow)
7. [RecyclerView y Adapters](#recyclerview-y-adapters)
8. [ViewBinding](#viewbinding)
9. [SharedPreferences](#sharedpreferences)
10. [Intent y Navegación](#intent-y-navegación)
11. [Material Design Components](#material-design)
12. [Patrón MVVM](#patrón-mvvm)

---

## 🎯 CONCEPTOS BÁSICOS

### ❓ **¿Qué es un Toast?**

**Respuesta:**
Un Toast es un mensaje emergente temporal que aparece en la pantalla por unos segundos y luego desaparece automáticamente.

**Código de ejemplo:**
```kotlin
Toast.makeText(this, "¡Producto agregado!", Toast.LENGTH_SHORT).show()
```

**Desglose:**
- `Toast.makeText()` - Crea el Toast
- `this` - Contexto (Activity actual)
- `"¡Producto agregado!"` - Mensaje a mostrar
- `Toast.LENGTH_SHORT` - Duración (2 segundos)
- `.show()` - Muestra el Toast

**Otras duraciones:**
- `LENGTH_SHORT` = 2 segundos
- `LENGTH_LONG` = 3.5 segundos

---

### ❓ **¿Qué es el Context?**

**Respuesta:**
El Context es una interfaz que proporciona acceso a información y recursos de la aplicación.

**Tipos de Context:**
1. **Application Context** - Vive durante toda la app
2. **Activity Context** - Vive mientras existe la Activity

**Ejemplos:**
```kotlin
// En una Activity
Toast.makeText(this, "Mensaje", Toast.LENGTH_SHORT).show()

// En un Fragment
Toast.makeText(requireContext(), "Mensaje", Toast.LENGTH_SHORT).show()

// Application Context
val appContext = applicationContext
```

**¿Cuándo usar cada uno?**
- Activity Context: Para UI, diálogos, Toasts
- Application Context: Para servicios de larga duración

---

### ❓ **¿Qué es una Activity?**

**Respuesta:**
Una Activity es una pantalla de la aplicación donde el usuario puede interactuar con la UI.

**Estructura básica:**
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inicializar componentes aquí
    }
}
```

**Puntos clave:**
- Hereda de `AppCompatActivity`
- `onCreate()` se llama cuando se crea la Activity
- `setContentView()` vincula el layout XML

---

## 🎨 LAYOUTS Y VIEWS

### ❓ **¿Qué es un Layout?**

**Respuesta:**
Un Layout es un archivo XML que define la estructura visual de la UI.

**Tipos principales:**

1. **LinearLayout** - Elementos en línea (vertical u horizontal)
```xml
<LinearLayout
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <!-- Hijos aquí -->
</LinearLayout>
```

2. **ConstraintLayout** - Posicionamiento con constraints
```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <TextView
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

3. **RecyclerView** - Listas eficientes
4. **ScrollView** - Contenido desplazable

---

### ❓ **¿Qué significan `match_parent` y `wrap_content`?**

**Respuesta:**

**`match_parent`:**
- El componente ocupa todo el espacio disponible del padre
```xml
android:layout_width="match_parent"  <!-- Ancho completo -->
```

**`wrap_content`:**
- El componente se ajusta al tamaño de su contenido
```xml
android:layout_height="wrap_content"  <!-- Alto según contenido -->
```

**Ejemplo visual:**
```
┌────────────────────────────┐
│ match_parent (ancho)       │
├────────────────────────────┤
│ ┌──────────────┐          │
│ │ wrap_content │          │
│ └──────────────┘          │
└────────────────────────────┘
```

---

### ❓ **¿Qué es un TextView?**

**Respuesta:**
TextView es un componente que muestra texto en pantalla.

**Código:**
```xml
<TextView
    android:id="@+id/tvTitle"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Bienvenido"
    android:textSize="24sp"
    android:textColor="@color/black"
    android:textStyle="bold" />
```

**En Kotlin:**
```kotlin
binding.tvTitle.text = "Nuevo título"
```

---

### ❓ **¿Qué es un EditText?**

**Respuesta:**
EditText es un campo de texto donde el usuario puede escribir.

**Código:**
```xml
<EditText
    android:id="@+id/etEmail"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Correo electrónico"
    android:inputType="textEmailAddress" />
```

**Obtener texto en Kotlin:**
```kotlin
val email = binding.etEmail.text.toString()
```

**Tipos de input comunes:**
- `textEmailAddress` - Email
- `textPassword` - Contraseña (oculta)
- `number` - Solo números
- `phone` - Teléfono

---

### ❓ **¿Qué es un Button?**

**Respuesta:**
Button es un botón clickeable.

**Código:**
```xml
<Button
    android:id="@+id/btnLogin"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:text="Iniciar Sesión" />
```

**Agregar click listener:**
```kotlin
binding.btnLogin.setOnClickListener {
    // Código al hacer click
    Toast.makeText(this, "Botón presionado", Toast.LENGTH_SHORT).show()
}
```

---

## 🎯 ACTIVITIES Y LIFECYCLE

### ❓ **¿Qué es el Lifecycle de una Activity?**

**Respuesta:**
El Lifecycle son los diferentes estados por los que pasa una Activity.

**Diagrama del ciclo de vida:**
```
onCreate() → onStart() → onResume() → [ACTIVITY RUNNING]
                ↑                              ↓
                |                         onPause()
                |                              ↓
                |                         onStop()
                |                              ↓
                └─────────────────────── onDestroy()
```

**Métodos principales:**

1. **onCreate()** - Activity creada
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // Inicializar componentes
}
```

2. **onStart()** - Activity visible (pero no interactiva)
```kotlin
override fun onStart() {
    super.onStart()
    // Activity se hace visible
}
```

3. **onResume()** - Activity en primer plano (interactiva)
```kotlin
override fun onResume() {
    super.onResume()
    // Usuario puede interactuar
}
```

4. **onPause()** - Activity pierde foco
```kotlin
override fun onPause() {
    super.onPause()
    // Guardar datos importantes
}
```

5. **onStop()** - Activity ya no es visible
```kotlin
override fun onStop() {
    super.onStop()
    // Detener animaciones, timers
}
```

6. **onDestroy()** - Activity destruida
```kotlin
override fun onDestroy() {
    super.onDestroy()
    // Liberar recursos
}
```

---

## 🎧 LISTENERS Y EVENTOS

### ❓ **¿Qué es un OnClickListener?**

**Respuesta:**
OnClickListener es una interfaz que detecta cuando el usuario hace click en un componente.

**Formas de implementar:**

**1. Lambda (recomendado):**
```kotlin
binding.btnLogin.setOnClickListener {
    // Código al hacer click
}
```

**2. Interfaz completa:**
```kotlin
binding.btnLogin.setOnClickListener(object : View.OnClickListener {
    override fun onClick(v: View?) {
        // Código al hacer click
    }
})
```

**3. En XML:**
```xml
<Button
    android:onClick="onLoginClick" />
```
```kotlin
fun onLoginClick(view: View) {
    // Código al hacer click
}
```

---

### ❓ **¿Qué es un TextWatcher?**

**Respuesta:**
TextWatcher detecta cambios en un EditText mientras el usuario escribe.

**Código:**
```kotlin
binding.etEmail.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Antes del cambio
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Durante el cambio
        val text = s.toString()
    }

    override fun afterTextChanged(s: Editable?) {
        // Después del cambio
        val text = s.toString()
    }
})
```

**Versión corta (solo afterTextChanged):**
```kotlin
binding.etEmail.doAfterTextChanged { text ->
    // Código cuando termina de escribir
}
```

---

### ❓ **¿Qué otros listeners existen?**

**Respuesta:**

**1. OnLongClickListener** - Click largo
```kotlin
binding.btnDelete.setOnLongClickListener {
    // Código al mantener presionado
    true // Retorna true para consumir el evento
}
```

**2. OnFocusChangeListener** - Cambio de foco
```kotlin
binding.etEmail.setOnFocusChangeListener { view, hasFocus ->
    if (hasFocus) {
        // El EditText tiene el foco
    }
}
```

**3. OnItemClickListener** - Click en lista
```kotlin
listView.setOnItemClickListener { parent, view, position, id ->
    // Click en item de la lista
}
```

---

## 🗄️ ROOM DATABASE

### ❓ **¿Qué es Room?**

**Respuesta:**
Room es una librería de Android que facilita el trabajo con SQLite (base de datos local).

**Componentes principales:**
1. **Entity** - Tabla de la base de datos
2. **DAO** - Operaciones (CRUD)
3. **Database** - Base de datos principal

---

### ❓ **¿Qué es una Entity?**

**Respuesta:**
Una Entity es una clase que representa una tabla en la base de datos.

**Código:**
```kotlin
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val nombre: String,
    val email: String,
    val password: String,
    
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis()
)
```

**Anotaciones:**
- `@Entity` - Marca la clase como tabla
- `@PrimaryKey` - Clave primaria (única)
- `autoGenerate = true` - ID automático (1, 2, 3...)
- `@ColumnInfo` - Nombre de columna personalizado

**Resultado en SQLite:**
```sql
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT,
    email TEXT,
    password TEXT,
    fecha_registro INTEGER
)
```

---

### ❓ **¿Qué es un DAO?**

**Respuesta:**
DAO (Data Access Object) define las operaciones que puedes hacer con la base de datos.

**Código:**
```kotlin
@Dao
interface UsuarioDao {
    
    @Insert
    suspend fun insertar(usuario: Usuario): Long
    
    @Update
    suspend fun actualizar(usuario: Usuario)
    
    @Delete
    suspend fun eliminar(usuario: Usuario)
    
    @Query("SELECT * FROM usuarios WHERE email = :email")
    fun obtenerPorEmail(email: String): Flow<Usuario?>
    
    @Query("SELECT * FROM usuarios")
    fun obtenerTodos(): Flow<List<Usuario>>
}
```

**Anotaciones:**
- `@Dao` - Marca la interfaz como DAO
- `@Insert` - Insertar datos
- `@Update` - Actualizar datos
- `@Delete` - Eliminar datos
- `@Query` - Consulta SQL personalizada

---

### ❓ **¿Qué significa `suspend`?**

**Respuesta:**
`suspend` indica que la función es asíncrona (no bloquea la UI).

**Sin suspend (❌ MAL):**
```kotlin
fun insertar(usuario: Usuario) {
    // Bloquea la UI mientras guarda
}
```

**Con suspend (✅ BIEN):**
```kotlin
suspend fun insertar(usuario: Usuario) {
    // No bloquea la UI
}
```

**Uso:**
```kotlin
lifecycleScope.launch {
    database.usuarioDao().insertar(usuario)
}
```

---

### ❓ **¿Qué es AppDatabase?**

**Respuesta:**
AppDatabase es la clase principal que gestiona toda la base de datos.

**Código:**
```kotlin
@Database(
    entities = [Usuario::class, Producto::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "keylab_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

**Conceptos:**
- `entities = [...]` - Lista de tablas
- `version = 1` - Versión de la BD
- `@Volatile` - Variable visible para todos los threads
- `synchronized` - Solo un thread a la vez
- **Singleton** - Una sola instancia de la BD

---

## 🔄 COROUTINES Y FLOW

### ❓ **¿Qué son las Coroutines?**

**Respuesta:**
Coroutines son una forma de escribir código asíncrono de manera secuencial (más fácil de leer).

**Problema sin Coroutines:**
```kotlin
// ❌ Bloquea la UI
button.setOnClickListener {
    val result = database.getData() // Tarda 2 segundos
    textView.text = result
}
```

**Solución con Coroutines:**
```kotlin
// ✅ No bloquea la UI
button.setOnClickListener {
    lifecycleScope.launch {
        val result = database.getData() // Tarda 2 segundos
        textView.text = result
    }
}
```

---

### ❓ **¿Qué es lifecycleScope?**

**Respuesta:**
lifecycleScope es un scope de coroutine vinculado al ciclo de vida de la Activity/Fragment.

**Ventaja:**
Se cancela automáticamente cuando la Activity se destruye (evita memory leaks).

**Código:**
```kotlin
lifecycleScope.launch {
    // Código asíncrono aquí
}
```

**Otros scopes:**
- `GlobalScope` - Vive toda la app (usar con cuidado)
- `viewModelScope` - Vive mientras existe el ViewModel

---

### ❓ **¿Qué es Flow?**

**Respuesta:**
Flow es un río de datos que emite valores a lo largo del tiempo.

**Analogía:**
```
Base de datos → Flow → Tu código
   (cambia)   (emite)  (recibe automáticamente)
```

**Código:**
```kotlin
// Definir Flow en DAO
@Query("SELECT * FROM usuarios")
fun obtenerTodos(): Flow<List<Usuario>>

// Recoger Flow en Activity
lifecycleScope.launch {
    database.usuarioDao().obtenerTodos().collect { usuarios ->
        // Este código se ejecuta cada vez que cambian los usuarios
        adapter.submitList(usuarios)
    }
}
```

**Ventaja:**
Los cambios en la BD se reflejan automáticamente en la UI.

---

### ❓ **¿Qué hace `.first()`?**

**Respuesta:**
`.first()` obtiene el primer valor del Flow y termina.

**Diferencia:**

**Con `.collect()`:**
```kotlin
usuarioDao().obtenerPorEmail(email).collect { usuario ->
    // Se ejecuta CADA VEZ que cambia
}
```

**Con `.first()`:**
```kotlin
val usuario = usuarioDao().obtenerPorEmail(email).first()
// Obtiene el valor UNA SOLA VEZ
```

**Uso común:**
```kotlin
lifecycleScope.launch {
    val usuario = database.usuarioDao()
        .validarLogin(email, password)
        .first()
    
    if (usuario != null) {
        // Login exitoso
    }
}
```

---

## 📜 RECYCLERVIEW Y ADAPTERS

### ❓ **¿Qué es un RecyclerView?**

**Respuesta:**
RecyclerView es un componente para mostrar listas grandes de manera eficiente.

**¿Por qué es eficiente?**
Solo crea las vistas visibles en pantalla y las reutiliza.

**Ejemplo:**
```
Lista de 1000 productos
   ↓
RecyclerView solo crea 10 vistas (las que se ven)
   ↓
Al hacer scroll, reutiliza esas mismas vistas
```

---

### ❓ **¿Qué es un Adapter?**

**Respuesta:**
El Adapter conecta los datos con el RecyclerView.

**Estructura básica:**
```kotlin
class ProductoAdapter : ListAdapter<Producto, ProductoAdapter.ViewHolder>(DiffCallback()) {
    
    // 1. Crear la vista (inflate del layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    // 2. Vincular datos con la vista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    // 3. ViewHolder (representa cada item)
    class ViewHolder(private val binding: ItemProductoBinding) 
        : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(producto: Producto) {
            binding.tvNombre.text = producto.nombre
            binding.tvPrecio.text = "$${producto.precio}"
        }
    }
    
    // 4. DiffUtil (compara items para actualizaciones eficientes)
    class DiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
```

---

### ❓ **¿Qué es un ViewHolder?**

**Respuesta:**
ViewHolder mantiene referencias a las vistas de cada item.

**Sin ViewHolder (❌ LENTO):**
```kotlin
// findViewById() se ejecuta cada vez que se hace scroll
textView = view.findViewById(R.id.tvNombre) // LENTO
```

**Con ViewHolder (✅ RÁPIDO):**
```kotlin
// findViewById() se ejecuta solo una vez
class ViewHolder(binding: ItemProductoBinding) {
    val textView = binding.tvNombre // Ya está guardado
}
```

---

### ❓ **¿Qué es DiffUtil?**

**Respuesta:**
DiffUtil compara listas y actualiza solo lo que cambió.

**Sin DiffUtil:**
```kotlin
adapter.notifyDataSetChanged() // Actualiza TODO (lento)
```

**Con DiffUtil:**
```kotlin
adapter.submitList(nuevaLista) // Solo actualiza lo que cambió (rápido)
```

**Ejemplo:**
```
Lista antigua: [A, B, C, D]
Lista nueva:   [A, B, E, D]
                     ↑
DiffUtil detecta: Solo cambió C → E
RecyclerView: Solo actualiza ese item
```

---

## 🔗 VIEWBINDING

### ❓ **¿Qué es ViewBinding?**

**Respuesta:**
ViewBinding genera clases que permiten acceder a las vistas de forma segura.

**Sin ViewBinding (❌ ANTIGUO):**
```kotlin
val textView = findViewById<TextView>(R.id.tvTitle)
// Puede causar crashes si el ID no existe
```

**Con ViewBinding (✅ MODERNO):**
```kotlin
private lateinit var binding: ActivityMainBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    
    binding.tvTitle.text = "Hola" // Seguro, no puede ser null
}
```

**Ventajas:**
1. Null safety - No crashes por IDs incorrectos
2. Type safety - Tipos correctos automáticamente
3. Autocompletado en IDE

---

### ❓ **¿Cómo se activa ViewBinding?**

**Respuesta:**
En `build.gradle.kts` del módulo `app`:

```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

---

## 💾 SHAREDPREFERENCES

### ❓ **¿Qué es SharedPreferences?**

**Respuesta:**
SharedPreferences guarda datos pequeños en formato clave-valor (como un diccionario).

**Uso típico:**
- IDs de usuario
- Configuraciones
- Tokens
- Preferencias

**NO usar para:**
- Datos grandes
- Datos sensibles sin encriptar

---

### ❓ **¿Cómo usar SharedPreferences?**

**Respuesta:**

**Guardar datos:**
```kotlin
val prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
prefs.edit().apply {
    putInt("user_id", 5)
    putString("nombre", "Nicolas")
    putBoolean("is_logged_in", true)
    apply() // O commit() para sincrónico
}
```

**Leer datos:**
```kotlin
val userId = prefs.getInt("user_id", -1) // -1 es valor por defecto
val nombre = prefs.getString("nombre", "")
val isLoggedIn = prefs.getBoolean("is_logged_in", false)
```

**Eliminar datos:**
```kotlin
prefs.edit().apply {
    remove("user_id")
    apply()
}
```

---

### ❓ **¿Qué es `apply()` vs `commit()`?**

**Respuesta:**

**`apply()`** - Asíncrono (recomendado)
```kotlin
prefs.edit().apply {
    putInt("user_id", 5)
    apply() // Guarda en segundo plano
}
```

**`commit()`** - Síncrono (bloquea)
```kotlin
prefs.edit().apply {
    putInt("user_id", 5)
    commit() // Guarda inmediatamente
}
```

**Cuándo usar cada uno:**
- `apply()` - Casi siempre
- `commit()` - Cuando necesitas saber si se guardó exitosamente

---

## 🚀 INTENT Y NAVEGACIÓN

### ❓ **¿Qué es un Intent?**

**Respuesta:**
Un Intent es un mensaje que permite navegar entre Activities o invocar componentes.

**Tipos:**

**1. Intent Explícito** - Navegar a Activity específica
```kotlin
val intent = Intent(this, MainActivity::class.java)
startActivity(intent)
```

**2. Intent Implícito** - Dejar que Android elija la app
```kotlin
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
startActivity(intent)
```

---

### ❓ **¿Cómo pasar datos entre Activities?**

**Respuesta:**

**Activity origen:**
```kotlin
val intent = Intent(this, DetalleActivity::class.java)
intent.putExtra("producto_id", 5)
intent.putExtra("nombre", "Teclado")
startActivity(intent)
```

**Activity destino:**
```kotlin
val productoId = intent.getIntExtra("producto_id", -1)
val nombre = intent.getStringExtra("nombre")
```

**Tipos de datos:**
- `putExtra("key", Int)` → `getIntExtra()`
- `putExtra("key", String)` → `getStringExtra()`
- `putExtra("key", Boolean)` → `getBooleanExtra()`
- `putExtra("key", Parcelable)` → `getParcelableExtra()`

---

### ❓ **¿Qué son los Intent Flags?**

**Respuesta:**
Los flags modifican el comportamiento de navegación.

**Flags comunes:**

**1. `FLAG_ACTIVITY_NEW_TASK`**
```kotlin
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
// Inicia la Activity en una nueva tarea
```

**2. `FLAG_ACTIVITY_CLEAR_TOP`**
```kotlin
intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
// Elimina todas las activities encima de la destino
```

**3. `FLAG_ACTIVITY_CLEAR_TASK`**
```kotlin
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
// Limpia todas las activities y crea una nueva tarea
```

**Ejemplo práctico (logout):**
```kotlin
val intent = Intent(this, LoginActivity::class.java)
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
startActivity(intent)
finish()
// El usuario NO puede volver atrás con el botón Back
```

---

### ❓ **¿Qué es `finish()`?**

**Respuesta:**
`finish()` cierra la Activity actual.

**Código:**
```kotlin
val intent = Intent(this, MainActivity::class.java)
startActivity(intent)
finish() // Cierra LoginActivity
```

**Stack de navegación:**
```
ANTES:
[LoginActivity] ← Usuario está aquí

DESPUÉS de startActivity():
[LoginActivity]
[MainActivity] ← Usuario está aquí

DESPUÉS de finish():
[MainActivity] ← Usuario está aquí
(LoginActivity destruida)
```

---

## 🎨 MATERIAL DESIGN

### ❓ **¿Qué es Material Design?**

**Respuesta:**
Material Design es el sistema de diseño de Google con componentes visuales modernos.

**Componentes clave:**

**1. MaterialButton**
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:text="Login"
    app:cornerRadius="28dp"
    app:backgroundTint="@color/primary" />
```

**2. TextInputLayout** (con TextInputEditText)
```xml
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Email"
    app:endIconMode="clear_text">
    
    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</com.google.android.material.textfield.TextInputLayout>
```

**3. MaterialCardView**
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp">
    
    <!-- Contenido aquí -->
</com.google.android.material.card.MaterialCardView>
```

---

### ❓ **¿Qué es dp vs sp vs px?**

**Respuesta:**

**dp (density-independent pixels)**
- Para tamaños de elementos (ancho, alto, márgenes)
- Se adapta a diferentes densidades de pantalla
```xml
android:layout_width="100dp"
android:padding="16dp"
```

**sp (scale-independent pixels)**
- Para tamaños de texto
- Se adapta a preferencias de tamaño de texto del usuario
```xml
android:textSize="16sp"
```

**px (pixels)**
- Píxeles físicos
- ❌ NO usar (no se adapta a diferentes pantallas)

**Regla:**
- Tamaños de elementos → **dp**
- Tamaños de texto → **sp**
- Nunca uses → **px**

---

## 📐 PATRÓN MVVM

### ❓ **¿Qué es MVVM?**

**Respuesta:**
MVVM (Model-View-ViewModel) es un patrón de arquitectura que separa la lógica de la UI.

**Capas:**

```
┌─────────────────────────────────────┐
│  View (Activity/Fragment)           │
│  - Muestra datos                    │
│  - Captura eventos del usuario      │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  ViewModel                          │
│  - Lógica de presentación           │
│  - Maneja estado de UI              │
│  - Sobrevive rotaciones             │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  Model (Repository + Room)          │
│  - Lógica de negocio                │
│  - Acceso a datos                   │
└─────────────────────────────────────┘
```

---

### ❓ **¿Qué es un ViewModel?**

**Respuesta:**
ViewModel mantiene datos que sobreviven a cambios de configuración (como rotaciones).

**Código:**
```kotlin
class CarritoViewModel(
    private val repository: CarritoRepository
) : ViewModel() {
    
    // LiveData o Flow para exponer datos
    val items: Flow<List<CarritoItem>> = repository.obtenerItems()
    
    val total: Flow<Double> = repository.obtenerTotal()
    
    // Funciones para modificar datos
    fun agregarItem(item: CarritoItem) {
        viewModelScope.launch {
            repository.agregar(item)
        }
    }
}
```

**Uso en Activity:**
```kotlin
class CartActivity : AppCompatActivity() {
    
    private val viewModel: CarritoViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            viewModel.items.collect { items ->
                adapter.submitList(items)
            }
        }
        
        binding.btnAgregar.setOnClickListener {
            viewModel.agregarItem(item)
        }
    }
}
```

---

### ❓ **¿Qué es un Repository?**

**Respuesta:**
Repository centraliza el acceso a datos (Room, API, etc).

**Código:**
```kotlin
class CarritoRepository(private val carritoDao: CarritoDao) {
    
    fun obtenerItems(): Flow<List<CarritoItem>> {
        return carritoDao.obtenerTodos()
    }
    
    suspend fun agregar(item: CarritoItem) {
        carritoDao.insertar(item)
    }
    
    suspend fun eliminar(productoId: Int) {
        carritoDao.eliminar(productoId)
    }
}
```

**Ventaja:**
- Si cambias de Room a API, solo modificas el Repository
- ViewModel no sabe de dónde vienen los datos

---

## 🧪 PREGUNTAS DE CÓDIGO

### ❓ **¿Qué hace este código?**

```kotlin
lifecycleScope.launch {
    val usuario = database.usuarioDao()
        .validarLogin(email, password)
        .first()
    
    if (usuario != null) {
        Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
    }
}
```

**Respuesta:**
1. Lanza una coroutine en el lifecycleScope
2. Obtiene el primer usuario que coincida con email y password
3. Si existe (no null), muestra "Bienvenido [nombre]"
4. Si no existe (null), muestra "Credenciales incorrectas"

---

### ❓ **¿Qué hace este código?**

```kotlin
binding.btnRegister.setOnClickListener {
    val nombre = binding.etNombre.text.toString().trim()
    val email = binding.etEmail.text.toString().trim()
    
    if (nombre.isEmpty()) {
        binding.nombreInputLayout.error = "El nombre es requerido"
        return@setOnClickListener
    }
    
    if (email.isEmpty()) {
        binding.emailInputLayout.error = "El email es requerido"
        return@setOnClickListener
    }
    
    // Continuar con registro
}
```

**Respuesta:**
1. Captura el click del botón
2. Obtiene el texto de los EditText y elimina espacios (trim)
3. Valida que nombre no esté vacío, si está vacío muestra error y sale
4. Valida que email no esté vacío, si está vacío muestra error y sale
5. Si las validaciones pasan, continúa con el registro

---

### ❓ **¿Qué hace este código?**

```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val producto = getItem(position)
    holder.bind(producto)
}
```

**Respuesta:**
1. `onBindViewHolder` vincula datos con la vista de un item del RecyclerView
2. `getItem(position)` obtiene el producto en la posición actual
3. `holder.bind(producto)` pasa el producto al ViewHolder para mostrar sus datos

---

### ❓ **¿Qué hace este código?**

```kotlin
val intent = Intent(this, OrderReceiptActivity::class.java)
intent.putExtra(OrderReceiptActivity.EXTRA_ORDER_ID, ordenId.toInt())
intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
startActivity(intent)
finish()
```

**Respuesta:**
1. Crea un Intent para navegar a OrderReceiptActivity
2. Pasa el ID de la orden como extra
3. Configura flags para limpiar todas las activities anteriores
4. Inicia la nueva Activity
5. Cierra la Activity actual
6. **Resultado:** El usuario NO puede volver atrás con el botón Back

---

## 💡 TIPS PARA EL EXAMEN

### ✅ **Conceptos que SIEMPRE preguntan:**

1. **Lifecycle de Activity**
   - onCreate, onStart, onResume, onPause, onStop, onDestroy

2. **Room Database**
   - Entity, DAO, Database
   - suspend, Flow, first()

3. **RecyclerView**
   - Adapter, ViewHolder, DiffUtil
   - onCreateViewHolder, onBindViewHolder

4. **Listeners**
   - OnClickListener
   - TextWatcher
   - OnLongClickListener

5. **Intent**
   - Explícito vs Implícito
   - putExtra / getExtra
   - Flags

6. **ViewBinding**
   - Cómo se usa
   - Ventajas vs findViewById

7. **Coroutines**
   - lifecycleScope.launch
   - suspend
   - Flow vs LiveData

---

### 📝 **Cómo responder preguntas de código:**

1. **Lee TODO el código primero**
2. **Identifica el contexto** (¿Es Activity? ¿Adapter? ¿DAO?)
3. **Explica línea por línea** si es necesario
4. **Menciona el resultado final**

**Ejemplo:**
```kotlin
binding.btnLogin.setOnClickListener {
    Toast.makeText(this, "Hola", Toast.LENGTH_SHORT).show()
}
```

**Respuesta completa:**
- `binding.btnLogin.setOnClickListener` - Agrega un listener al botón de login
- `Toast.makeText()` - Crea un mensaje temporal
- `this` - Contexto de la Activity actual
- `"Hola"` - Mensaje a mostrar
- `LENGTH_SHORT` - Duración de 2 segundos
- `.show()` - Muestra el Toast en pantalla
- **Resultado:** Cuando el usuario presiona el botón, aparece un mensaje "Hola" por 2 segundos

---

## 🎓 GLOSARIO RÁPIDO

| Término | Significado |
|---------|-------------|
| Activity | Pantalla de la app |
| Fragment | Porción de UI reutilizable |
| Intent | Mensaje para navegar |
| Context | Acceso a recursos de la app |
| ViewBinding | Acceso seguro a vistas |
| ViewModel | Mantiene datos de UI |
| Repository | Centraliza acceso a datos |
| Entity | Tabla de base de datos |
| DAO | Operaciones de BD |
| Flow | Río de datos reactivo |
| Coroutine | Código asíncrono |
| suspend | Función asíncrona |
| Adapter | Conecta datos con lista |
| ViewHolder | Mantiene referencias de vistas |
| Toast | Mensaje temporal |
| Listener | Detecta eventos |
| Layout | Estructura visual (XML) |
| dp | Unidad independiente de densidad |
| sp | Unidad para texto |

---

## 🔥 PREGUNTAS BONUS DEL PROFESOR

### ❓ **¿Por qué usar Flow en vez de LiveData?**

**Respuesta:**
- Flow tiene más operadores (map, filter, combine)
- Flow es parte de Kotlin Coroutines (más moderno)
- LiveData está limitado a Android
- Flow permite transformaciones complejas

---

### ❓ **¿Qué pasa si no uso suspend en funciones de Room?**

**Respuesta:**
```kotlin
// ❌ Sin suspend
@Insert
fun insertar(usuario: Usuario)
// Error: Room no permite operaciones de BD en el hilo principal
// Crashea con: android.os.NetworkOnMainThreadException

// ✅ Con suspend
@Insert
suspend fun insertar(usuario: Usuario)
// Room ejecuta en un hilo secundario automáticamente
```

---

### ❓ **¿Cuál es la diferencia entre apply() y also()?**

**Respuesta:**
Ambos son scope functions, pero:

**`apply()`** - Retorna el objeto
```kotlin
val intent = Intent(this, MainActivity::class.java).apply {
    putExtra("id", 5)
    putExtra("nombre", "Test")
}
// intent tiene los extras
```

**`also()`** - Retorna el objeto, pero usa `it`
```kotlin
val intent = Intent(this, MainActivity::class.java).also {
    it.putExtra("id", 5)
    it.putExtra("nombre", "Test")
}
```

---

## 📚 RECURSOS ADICIONALES

- **Documentación oficial:** developer.android.com
- **Kotlin docs:** kotlinlang.org
- **Material Design:** material.io
- **Codelabs Android:** codelabs.developers.google.com

---

**¡Buena suerte en tu examen! 🚀**

*Última actualización: 2025-11-17*
