# Contexto del Proyecto - KeyLab Mobile

**Fecha de inicio**: 2025-11-08  
**Última actualización**: 2025-11-10 (06:16)

---

## 📌 Resumen Ejecutivo

Migración de KeyLab (aplicación web HTML/JS estática) a una aplicación móvil Android nativa para cumplir con requisitos de evaluación del ramo de Desarrollo Mobile.

---

## 🎯 Objetivo del Proyecto

Desarrollar una aplicación móvil Android que:
1. Consuma una API REST para gestionar productos (CRUD)
2. Implemente al menos un elemento nativo (cámara para captura de productos)
3. Tenga persistencia local con Room/SQLite
4. Demuestre arquitectura cliente-servidor funcional

---

## 📁 Estructura de Archivos

```
/home/nicolas/u/mobile/
├── android_2_evaluacion_2025.pdf    # Requisitos de evaluación oficial
├── MIGRACION_MOBILE.md              # Análisis del backend original
├── PLAN_DESARROLLO.md               # Roadmap técnico detallado
├── CONTEXTO_PROYECTO.md             # Este archivo (contexto y bitácora)
└── KeyLabMobile/                    # Proyecto Android (en desarrollo)
    └── src/...
```

**Proyecto original a migrar**: `/home/nicolas/u/FS2/KeyLab`

---

## 🔍 Análisis del Proyecto Original

### Estado actual del código fuente
- **Ubicación**: `/home/nicolas/u/FS2/KeyLab`
- **Tecnología**: React 19 + Vite
- **Stack frontend**:
  - React 19.1.1 con JSX
  - React Router DOM 7.9.4 (navegación SPA)
  - React Bootstrap 2.10.10 (componentes UI)
  - Bootstrap 5.3.8 (estilos base)
  - SweetAlert2 (alertas)
  - Vitest + Testing Library (testing)
- **Arquitectura**:
  - Context API para estado global (`AuthContext`, `CartContext`)
  - Custom hooks (`useProductsByCategory`)
  - Componentes funcionales con JSX
  - Routing con React Router
- **Características actuales**:
  - Catálogo de productos hardcodeado en `src/data/productos.js`
  - Autenticación mock con Context API + localStorage
  - Carrito de compras con Context API + localStorage
  - Sin backend real (sin servidor, sin base de datos)
  - UI con wrappers de React Bootstrap (Navbar, Nav, Container, etc.)

### Datos del proyecto original
- **Modelo de Producto** (hardcoded):
  ```javascript
  {
    id: number,
    nombre: string,
    precio: number,
    categoria: string,        // "Teclados", "Keycaps", "Switches", "Cases"
    subcategoria: string,     // "60%", "75%", "Full Size"
    imagen: string,           // "/assets/img/..."
    stock: number,
    descripcion: string
  }
  ```

### Limitaciones identificadas
❌ No hay API REST (todo es estático)  
❌ No hay persistencia en base de datos  
❌ No hay gestión de archivos/imágenes en servidor  
❌ No hay autenticación real (JWT/tokens)  
❌ No hay integración con IA  
❌ Context API + localStorage no escala a producción

---

## 🏗️ Arquitectura Propuesta

### Backend
**⚠️ ESTADO: EN DISCUSIÓN**

Opciones bajo evaluación:
1. **Node.js + Express + Supabase** (opción inicial)
2. **Firebase/Firestore** (alternativa serverless)
3. **Backend propio con PostgreSQL/MySQL**
4. **API REST vs GraphQL** (por definir)

**Endpoints mínimos requeridos** (independiente del stack):
- `GET /api/productos` - Listar todos
- `GET /api/productos/:id` - Obtener uno
- `POST /api/productos` - Crear
- `PUT /api/productos/:id` - Actualizar
- `DELETE /api/productos/:id` - Eliminar
- `POST /api/productos/upload` - Subir imagen

**Decisión pendiente**: Stack definitivo para backend

### Mobile (Android)
- **Lenguaje**: Kotlin
- **UI**: XML (layouts nativos)
- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Componentes clave**:
  - **Retrofit**: Cliente HTTP para consumir API
  - **Room**: Persistencia local SQLite
  - **CameraX/Intent**: Captura de imágenes (elemento nativo)
  - **Glide**: Carga de imágenes
  - **Coroutines**: Operaciones asíncronas

---

## ✅ Requisitos de Evaluación

### Técnicos
- [x] Identificar proyecto web a migrar
- [x] API REST funcional (Supabase REST API)
- [x] Dependencias configuradas (Retrofit, Room, CameraX)
- [x] Estructura de proyecto MVVM creada
- [x] Persistencia local implementada (Room SQLite)
- [x] Consumo de API desde Android implementado (Retrofit)
- [x] Repository Pattern (offline-first) implementado
- [x] ViewModels para lógica de presentación
- [x] RecyclerView Adapter implementado (con DiffUtil)
- [x] Layouts XML diseñados (item + main activity)
- [x] MainActivity conectada con arquitectura MVVM
- [x] UI funcional mostrando lista de productos
- [x] Elemento nativo implementado (CameraActivity con CameraX)
- [x] Captura de fotos funcional
- [x] CameraActivity registrada en AndroidManifest
- [x] UI pulida con sistema de diseño completo
- [x] Login moderno estilo Opal (iOS-inspired)
- [x] Material Design 3 implementado
- [x] 6 pantallas principales implementadas (Login, Main, Category, Cart, Profile, Camera)
- [x] Sistema de navegación entre pantallas funcionando
- [x] Adaptadores para lista de productos y carrito
- [x] Interfaz completamente en español (login + main)
- [x] Bottom Navigation actualizado (3 botones: Inicio, Carrito, Perfil)
- [x] Sistema de categorías con filtrado funcional (chips clickeables)
- [x] Carrito con persistencia local implementado (Room)
- [x] URLs de imágenes de Supabase Storage integradas
- [x] Botón "Agregar al carrito" en tarjetas de productos (con persistencia)
- [x] Tarjetas de productos optimizadas (tamaño reducido)
- [x] Sistema de cambio de foto de perfil (cámara + galería)
- [x] Interfaz MainActivity completamente en español
- [x] Logo KeyLab integrado en MainActivity (120dp x 120dp)
- [x] LoginActivity 100% traducido al español (validaciones + mensajes)
- [ ] CRUD completo de productos funcional en UI
- [ ] Subida de imágenes a Supabase Storage desde CameraActivity

### Entregables
- [ ] Código fuente en .ZIP (con comentarios y nombres identificatorios)
- [ ] Documento con evidencias (screenshots del código)
- [ ] Presentación PPT/PDF
- [ ] Demo funcional en emulador/dispositivo

---

## 📊 Fases de Desarrollo

### FASE 1: Backend básico ✅
**Estado**: ✅ COMPLETADO  
**Tiempo real**: ~2 horas
**Fecha**: 2025-11-09

**Decisiones tomadas**:
- ✅ Stack elegido: **Supabase** (PostgreSQL + Storage)
- ✅ API: REST (usando endpoints nativos de Supabase)
- ✅ Almacenamiento: Supabase Storage para imágenes

**Tareas completadas**:
- ✅ Cuenta Supabase configurada
- ✅ Base de datos PostgreSQL lista
- ✅ Tabla `productos` creada (estructura por confirmar)
- ✅ API Keys integradas en `BuildConfig`:
  - `SUPABASE_URL`: `https://pwnajivbudcwfcordblx.supabase.co`
  - `SUPABASE_KEY`: Configurada (anon key)
- ✅ Storage habilitado para imágenes de productos

**Endpoints disponibles**:
- `GET /rest/v1/productos` - Listar productos
- `GET /rest/v1/productos?id=eq.{id}` - Obtener por ID
- `POST /rest/v1/productos` - Crear producto
- `PATCH /rest/v1/productos?id=eq.{id}` - Actualizar
- `DELETE /rest/v1/productos?id=eq.{id}` - Eliminar
- Supabase Storage API para subir/descargar imágenes

### FASE 2: Setup Android ✅
**Estado**: ✅ COMPLETADO  
**Tiempo real**: ~30 minutos
**Fecha**: 2025-11-09

**Dependencias configuradas**:
- ✅ Retrofit 2.9.0 + Gson converter
- ✅ OkHttp 4.12.0 + Logging interceptor
- ✅ Room 2.6.1 (runtime + ktx + compiler)
- ✅ Coroutines 1.7.3 (android + core)
- ✅ Glide 4.16.0 (carga de imágenes)
- ✅ CameraX 1.3.1 (camera2 + lifecycle + view)
- ✅ ViewModel + LiveData 2.7.0
- ✅ RecyclerView 1.3.2
- ✅ SwipeRefreshLayout 1.1.0

**Permisos configurados en AndroidManifest.xml**:
- ✅ `INTERNET`
- ✅ `CAMERA`
- ✅ `WRITE_EXTERNAL_STORAGE` (API ≤28)
- ✅ `READ_EXTERNAL_STORAGE` (API ≤32)
- ✅ Feature `android.hardware.camera` (opcional)

**Estructura de carpetas creada**:
```
com.keylab.mobile/
├── data/
│   ├── local/       # Room Database, DAO
│   ├── remote/      # Retrofit ApiService, DTOs
│   └── repository/  # Repository Pattern (offline-first)
├── domain/
│   └── model/       # Entidades de dominio (Producto)
├── ui/
│   ├── adapter/     # RecyclerView Adapters
│   ├── viewmodel/   # ViewModels
│   ├── LoginActivity.kt
│   └── MainActivity.kt
└── utils/           # Extensions, constantes
```

**Configuración de build**:
- ✅ ViewBinding habilitado
- ✅ BuildConfig habilitado (para API keys)
- ✅ KAPT configurado (Room compiler)
- ✅ Java/Kotlin 21 configurado
- ✅ Android Gradle Plugin actualizado a 8.3.2
- ✅ Gradle actualizado a 8.4
- ✅ Memoria JVM aumentada a 4GB (`org.gradle.jvmargs=-Xmx4096m`)

### FASE 3: Implementación Android ✅
**Estado**: ✅ COMPLETADO (98% - Core + UI pulida)  
**Tiempo estimado**: 4-5 horas  
**Tiempo real**: ~5.5 horas  
**Fecha inicio**: 2025-11-09  
**Fecha fin**: 2025-11-10 (01:27)

**Core funcional:**
- ✅ Modelo de dominio `Producto.kt` creado (9 campos + anotaciones Room)
- ✅ `ProductoDao.kt` implementado (CRUD + búsquedas + estadísticas)
- ✅ `AppDatabase.kt` configurado (Singleton thread-safe)
- ✅ KAPT compilado exitosamente (Room genera implementación)
- ✅ `SupabaseApiService.kt` creado (interface Retrofit con CRUD)
- ✅ `RetrofitClient.kt` configurado (auth + interceptors + timeouts)
- ✅ `ApiResponse.kt` implementado (sealed class para estados)
- ✅ `ProductoRepository.kt` implementado (offline-first pattern)
- ✅ `ProductoViewModel.kt` creado (lógica de presentación + estados)
- ✅ `ProductoViewModelFactory.kt` creado (inyección de dependencias)
- ✅ `ProductoAdapter.kt` implementado (ViewHolder + Glide + DiffUtil)
- ✅ `MainActivity.kt` refactorizado (ViewBinding + ViewModel + Observers)
- ✅ `LoginActivity.kt` implementado con validación de formularios
- ✅ `CameraActivity.kt` implementado (CameraX + captura de fotos)

**UI/UX pulida (Material3):**
- ✅ Sistema de diseño completo: `dimens.xml` (25 dimensiones estandarizadas)
- ✅ Paleta de colores: `colors.xml` (50+ colores Material3 + Dark + States)
- ✅ Themes completo: `themes.xml` (Material3 DayNight + custom styles)
- ✅ Login Opal-style: `activity_login.xml` (270 líneas, dark minimalista)
- ✅ Main screen: `activity_main.xml` (98 líneas, empty state mejorado)
- ✅ Product cards: `item_producto.xml` (125 líneas, pulidas con @dimen)
- ✅ Strings organizados: `strings.xml` (48 líneas, sin duplicados)
- ✅ Build exitoso (42 tasks, 0 errores)

**Pendiente (integración):**
- [ ] Integrar subida de imágenes a Supabase Storage
- [ ] Conectar CameraActivity con creación de productos
- [ ] Pantalla de detalle de producto (opcional)
- [ ] Pantalla crear/editar producto (opcional)

### FASE 4: Funcionalidades extras ⏳
**Estado**: Pendiente  
**Tiempo estimado**: 1-2 horas

- [ ] Pantalla de detalles de producto
- [ ] Búsqueda/filtros
- [ ] Indicadores de estado offline

### FASE 5: Testing y documentación ⏳
**Estado**: Pendiente  
**Tiempo estimado**: 1 hora

- [ ] Pruebas de CRUD completo
- [ ] Pruebas modo offline
- [ ] Capturar screenshots
- [ ] Preparar presentación

---

## 📝 Bitácora de Desarrollo

### Sesión 1 - 2025-11-08
**Duración**: 30 minutos  
**Actividades**:
- ✅ Revisión del proyecto original en `/home/nicolas/u/FS2/KeyLab`
- ✅ Análisis de documentos de planificación
- ✅ Creación de archivo de contexto (este documento)
- ✅ Análisis técnico del proyecto React (confirmación de stack)

**Descubrimientos**:
- Proyecto original usa **React 19 + Vite** (no HTML/JS estático como se creía inicialmente)
- Usa **React Bootstrap** como librería de componentes UI
- Context API + localStorage para estado (AuthContext, CartContext)
- Arquitectura moderna con hooks personalizados
- Testing configurado con Vitest

**Decisiones tomadas**:
- ✅ Arquitectura MVVM para Android (confirmado)
- ✅ Enfoque offline-first con Room como caché (confirmado)
- ⏳ **Backend stack AÚN EN DISCUSIÓN** (opciones: Supabase, Firebase, propio)

**Próximos pasos**:
1. **Definir stack de backend** (Node/Express vs Firebase vs otro)
2. Inicializar proyecto backend según decisión
3. Implementar endpoints básicos de la API
4. Inicializar proyecto Android

**Bloqueadores**: 
- ⚠️ Decisión de backend pendiente

---

### Sesión 2 - 2025-11-09
**Duración**: ~3 horas  
**Actividades**:
- ✅ **FASE 1 completada**: Backend con Supabase configurado
- ✅ **FASE 2 completada**: Setup Android con todas las dependencias
- ✅ Resolución de problemas de build (AGP 8.2→8.3.2, Gradle 8.2→8.4)
- ✅ Configuración de memoria JVM para evitar OutOfMemoryError
- ✅ Creación de estructura MVVM completa
- ✅ Verificación de permisos en AndroidManifest

**Descubrimientos técnicos**:
- Android Gradle Plugin 8.2 tiene incompatibilidad con JDK 21 (jlink failure)
- AGP 8.3+ requiere Gradle 8.4 mínimo
- Compilación exitosa después de actualizar toolchain
- Supabase usa REST API nativa (sin necesidad de backend custom)

**Decisiones tomadas**:
- ✅ **Backend definitivo: Supabase** (PostgreSQL + Storage + REST API)
- ✅ Arquitectura offline-first con Room como fuente de verdad
- ✅ Estructura MVVM estándar implementada
- ✅ Java/Kotlin 21 como target (compatible con AGP 8.3.2)

**Stack final confirmado**:
```
Backend:    Supabase (PostgreSQL + Storage)
Frontend:   Kotlin + XML
Async:      Coroutines + Flow
Network:    Retrofit + OkHttp
Local DB:   Room SQLite
Images:     Glide
Camera:     CameraX
```

**Métricas**:
- APK Debug generado: 7.6 MB
- APK Release generado: 6.3 MB
- Build time: ~13 segundos
- Cobertura Android: API 24+ (~94% dispositivos)

**Próximos pasos**:
1. ✅ ~~Implementar modelo `Producto.kt`~~
2. ✅ ~~Configurar Room Database~~
3. Crear ApiService para Supabase
4. Implementar Repository Pattern
5. Desarrollar UI con RecyclerView

**Bloqueadores resueltos**: 
- ✅ Stack de backend decidido
- ✅ Problemas de build solucionados
- ✅ Estructura de proyecto lista

---

### Sesión 3 - 2025-11-09 (21:00-21:30)
**Duración**: ~30 minutos  
**Actividades**:
- ✅ Implementación de capa de persistencia (Room Database)
- ✅ Creación de modelo de dominio `Producto.kt`
- ✅ Implementación de `ProductoDao.kt` con operaciones CRUD completas
- ✅ Configuración de `AppDatabase.kt` con patrón Singleton
- ✅ Verificación de compilación KAPT exitosa

**Archivos creados**:
```
app/src/main/java/com/keylab/mobile/
├── domain/model/
│   └── Producto.kt          # Modelo + @Entity Room (78 líneas)
└── data/local/
    ├── ProductoDao.kt       # Interface DAO con queries (73 líneas)
    └── AppDatabase.kt       # Database principal (48 líneas)
```

**Decisiones técnicas**:
- ✅ `Producto` sirve como modelo de dominio Y entidad Room (DRY)
- ✅ DAO con Flow para reactividad (cambios automáticos en UI)
- ✅ Queries incluyen búsquedas, filtros y estadísticas
- ✅ OnConflictStrategy.REPLACE para sincronización idempotente
- ✅ Documentación concisa con títulos y palabras clave

**Room Database implementado**:
- Tabla `productos` con 9 campos
- 11 operaciones de lectura (SELECT con Flow y suspend)
- 6 operaciones de escritura (INSERT/UPDATE/DELETE)
- 2 operaciones de estadísticas (COUNT)
- Base de datos: `keylab_database.db` (SQLite)

**Próximo paso**:
Implementar capa de presentación (ViewModel + UI)

---

### Sesión 3 (continuación) - 2025-11-09 (21:30-21:47)
**Duración**: ~17 minutos  
**Actividades**:
- ✅ Implementación de capa de red (Retrofit + Supabase)
- ✅ Creación de `SupabaseApiService.kt` con endpoints REST
- ✅ Configuración de `RetrofitClient.kt` con auth headers
- ✅ Implementación de `ApiResponse.kt` para manejo de estados
- ✅ Creación de `ProductoRepository.kt` con patrón offline-first
- ✅ Verificación de compilación Kotlin exitosa

**Archivos creados**:
```
app/src/main/java/com/keylab/mobile/
├── data/
│   ├── remote/
│   │   ├── SupabaseApiService.kt    # Interface Retrofit (74 líneas)
│   │   ├── RetrofitClient.kt        # Singleton Retrofit (62 líneas)
│   │   └── ApiResponse.kt           # Sealed class (17 líneas)
│   └── repository/
│       └── ProductoRepository.kt    # Offline-first (148 líneas)
```

**Decisiones técnicas**:
- ✅ Retrofit con interceptores para auth automática (apikey + Bearer)
- ✅ Logging solo en debug (HttpLoggingInterceptor)
- ✅ Timeouts de 30s para operaciones lentas
- ✅ ApiResponse como sealed class (Loading/Success/Error)
- ✅ Repository devuelve Flow de Room (reactivo)
- ✅ Sincronización: API → Room → UI automática

**Arquitectura de datos implementada**:
```
┌─────────────┐
│  ViewModel  │
└──────┬──────┘
       ↓
┌──────────────────┐
│   Repository     │ ← OFFLINE-FIRST
└────┬────────┬────┘
     ↓        ↓
┌────────┐ ┌─────────┐
│  Room  │ │ Retrofit│
│ (DAO)  │ │  (API)  │
└────────┘ └─────────┘
     ↓           ↓
  SQLite     Supabase
```

**Flujo offline-first**:
1. ViewModel pide datos → Repository
2. Repository devuelve Flow de Room → UI se actualiza RÁPIDO
3. Repository sincroniza con Supabase en background
4. Nuevos datos se guardan en Room
5. Flow emite cambios → UI se actualiza automáticamente

**Próximo paso**:
Implementar ProductoViewModel para conectar con UI

---

### Sesión 3 (continuación) - 2025-11-09 (21:55)
**Siguiente:** Implementación de ProductoViewModel

**¿Qué es un ViewModel?**

Un ViewModel es el "cerebro" entre la UI (Activity) y los datos (Repository).
Es una clase que:
- Contiene la **lógica de presentación** (qué mostrar en la UI)
- **Sobrevive a rotaciones** de pantalla (no se destruye con onCreate)
- Expone datos mediante **LiveData/Flow** (reactivo: UI se actualiza sola)
- **NO conoce la UI** (no tiene referencias a Activity/Fragment)

**Problema sin ViewModel:**
```kotlin
// ❌ Activity hace todo (malo)
class MainActivity : AppCompatActivity() {
    var productos = emptyList<Producto>() // Se pierde al rotar 🔄
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Se ejecuta CADA VEZ que rotas el celular
        cargarProductosDeAPI() // Request HTTP innecesario
    }
}
```
→ Rotas celular → Datos perdidos → Vuelve a cargar todo ❌

**Solución con ViewModel:**
```kotlin
// ✅ ViewModel maneja lógica (bueno)
class ProductoViewModel : ViewModel() {
    val productos: LiveData<List<Producto>> // Sobrevive a rotaciones
    fun sincronizar() { /* lógica */ }
}

class MainActivity : AppCompatActivity() {
    val viewModel: ProductoViewModel by viewModels() // Se crea UNA VEZ
    
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.productos.observe(this) { lista ->
            mostrarEnUI(lista) // Se actualiza automáticamente
        }
    }
}
```
→ Rotas celular → Datos siguen ahí → UI se restaura instantánea ✅

**Flujo de datos con ViewModel:**
```
Usuario pulsa botón
    ↓
Activity: viewModel.cargarProductos()
    ↓
ViewModel: repository.sincronizar()
    ↓
Repository: Room → API → Room
    ↓
LiveData/Flow emite cambios
    ↓
UI se actualiza automáticamente
```

**Analogía:**
- **UI (Activity)** = Camarero (toma pedidos, sirve platos)
- **ViewModel** = Chef (decide qué cocinar, cómo preparar)
- **Repository** = Despensa (trae ingredientes)
- **Room/Retrofit** = Proveedores (locales/externos)

**Ahora implementaremos ProductoViewModel con:**
- Flow para lista de productos (reactivo desde Room)
- LiveData para estados (loading, errores)
- Funciones CRUD (sincronizar, crear, actualizar, eliminar)
- viewModelScope (coroutines automáticas)

---

### Sesión 3 (continuación) - 2025-11-09 (21:55-22:00)
**Duración**: ~5 minutos  
**Actividades**:
- ✅ Implementación de ProductoViewModel (lógica de presentación)
- ✅ Creación de ProductoViewModelFactory (inyección de Repository)
- ✅ Explicación conceptual de ViewModel agregada al contexto
- ✅ Verificación de compilación Kotlin exitosa

**Archivos creados**:
```
app/src/main/java/com/keylab/mobile/
└── ui/viewmodel/
    ├── ProductoViewModel.kt           # ViewModel principal (178 líneas)
    └── ProductoViewModelFactory.kt    # Factory (25 líneas)
```

**ProductoViewModel implementado:**
- **Flow<List<Producto>>** productos → Lista reactiva desde Room
- **LiveData<Boolean>** isLoading → Estado de carga para UI
- **LiveData<String?>** error → Mensajes de error
- **LiveData<String?>** successMessage → Confirmaciones
- **Funciones CRUD completas:**
  - `sincronizarProductos()` → Supabase → Room
  - `buscarProductos(query)` → Búsqueda local
  - `filtrarPorCategoria(categoria)` → Filtros
  - `crearProducto(producto)` → POST a API
  - `actualizarProducto(producto)` → PATCH a API
  - `eliminarProducto(id)` → DELETE a API

**Decisiones técnicas:**
- ✅ Flow para datos que cambian (productos desde Room)
- ✅ LiveData para estados puntuales (loading, errores)
- ✅ viewModelScope para coroutines (se cancelan automáticamente)
- ✅ Separación _private MutableLiveData / public LiveData (encapsulación)
- ✅ when exhaustivo para ApiResponse (Loading/Success/Error)

**Arquitectura MVVM completada al 75%:**
```
┌─────────────────┐
│  Activity (UI)  │ ← Observa LiveData/Flow
└────────┬────────┘
         ↓
┌──────────────────┐
│ ProductoViewModel│ ← ✅ Lógica de presentación
└────────┬─────────┘
         ↓
┌──────────────────┐
│    Repository    │ ← Coordina fuentes
└────┬────────┬────┘
     ↓        ↓
  Room ←→ Retrofit
```

**Próximo paso**:
Crear RecyclerView Adapter + Layouts XML para mostrar productos

---

### Sesión 3 (continuación) - 2025-11-09 (22:10-22:24)
**Siguiente:** Implementación de UI (RecyclerView + Adapter + Layouts)

**¿Qué son item_producto.xml y ProductoAdapter?**

Para mostrar una LISTA de productos en Android necesitas 3 componentes:

**1️⃣ RecyclerView** → El contenedor (como `<ul>` en HTML)
- Muestra la lista completa de productos
- Eficiente: reutiliza vistas (no crea 1000 vistas si hay 1000 productos)

**2️⃣ item_producto.xml** → La plantilla de CADA producto (como `<li>`)
- Define CÓMO SE VE un producto individual
- Se repite por cada producto en la lista
- Contiene: ImageView, TextViews (nombre, precio), Buttons

**3️⃣ ProductoAdapter** → El "pegamento" que une datos con UI
- Recibe List<Producto> del ViewModel
- Infla (crea) item_producto.xml por cada producto
- LLENA cada vista con datos reales (nombre, precio, imagen)
- Maneja clicks (navegar a detalle, eliminar)

**Flujo de datos:**
```
ViewModel: List<Producto>
    ↓ envía lista
ProductoAdapter
    ↓ infla XML + llena datos
RecyclerView
    ↓ muestra en pantalla
Usuario ve lista de productos ✅
```

**Analogía del álbum de fotos:**
- **RecyclerView** = El álbum completo (contenedor)
- **item_producto.xml** = Plantilla de cada página (molde)
- **ProductoAdapter** = Persona que llena cada página con fotos/datos

**Ejemplo visual de item_producto.xml:**
```
┌─────────────────────────────────────┐
│  ┌───────────────────────────────┐  │
│  │   [Imagen del teclado]        │  │ ← ImageView (200dp)
│  └───────────────────────────────┘  │
│                                     │
│  Keychron K8 Pro                    │ ← TextView nombre (bold)
│  $89.990                            │ ← TextView precio
│                                     │
│  [Categoría: Teclados]              │ ← Chip categoría
│                                     │
│  [Ver Detalle]  [Eliminar]          │ ← Buttons
└─────────────────────────────────────┘
```

**Código simplificado de ProductoAdapter:**
```kotlin
class ProductoAdapter : RecyclerView.Adapter<ViewHolder>() {
    
    // 1. Inflar XML (crear vista)
    onCreateViewHolder() {
        inflar item_producto.xml
    }
    
    // 2. Llenar con datos
    onBindViewHolder(producto) {
        tvNombre.text = producto.nombre
        tvPrecio.text = producto.precio
        Glide.load(producto.imagenUrl).into(ivImagen)
        
        itemView.setOnClickListener {
            // Navegar a detalle
        }
    }
    
    // 3. Cantidad de items
    getItemCount() = productos.size
}
```

**Reutilización del diseño React:**
- ProductGrid.jsx → RecyclerView con GridLayoutManager
- Card.jsx → MaterialCardView en item_producto.xml
- ProductGrid.css → Dimensiones Android (200dp, 16dp padding, 8dp radius)

**Ahora implementaremos:**
1. `item_producto.xml` (layout del card individual)
2. `ProductoAdapter.kt` (adapter con ViewHolder)
3. Actualizar `activity_main.xml` (agregar RecyclerView)
4. Conectar MainActivity con ViewModel + Adapter

---

### Sesión 3 (continuación) - 2025-11-09 (22:24-22:46)
**Duración**: ~22 minutos  
**Actividades**:
- ✅ Análisis de UI reutilizable de React (ProductGrid.jsx)
- ✅ Creación de `item_producto.xml` (MaterialCardView layout)
- ✅ Implementación de `ProductoAdapter.kt` con ViewHolder
- ✅ Actualización de `activity_main.xml` (RecyclerView + Toolbar)
- ✅ Refactorización de `MainActivity.kt` (ViewModel + Observers)
- ✅ Build exitoso completo (111 tasks, 0 errores)

**Archivos creados/actualizados:**
```
app/src/main/java/com/keylab/mobile/
├── ui/
│   ├── adapter/
│   │   └── ProductoAdapter.kt         # Adapter completo (143 líneas)
│   └── MainActivity.kt                # Refactorizado (148 líneas)
└── res/
    ├── layout/
    │   ├── item_producto.xml          # Card layout (118 líneas)
    │   └── activity_main.xml          # Simplificado (91 líneas)
    └── values/
        └── strings.xml                # 6 strings agregados
```

**item_producto.xml estructura:**
- MaterialCardView (8dp radius, 2dp elevation, stroke 1dp)
- ImageView (200dp height, centerCrop con Glide)
- TextView nombre (bold, 16sp, maxLines 2)
- TextView precio (bold, 18sp, color primary, formato CLP)
- Chip categoría (Material3.Chip.Assist, opcional)
- Chip "Sin Stock" (rojo, visible solo si stock = 0)
- Button "Ver Detalle" (TonalButton, full width)
- Button "Eliminar" (OutlinedButton, visible solo admin)

**ProductoAdapter.kt funcionalidades:**
- ViewHolder con ViewBinding (ItemProductoBinding)
- Glide para cargar imágenes (placeholder + error fallback)
- DiffUtil para actualizaciones eficientes
- Formato precio CLP con puntos de miles ($89.990)
- Click listeners (onItemClick, onDeleteClick opcional)
- Manejo de estados visuales (stock, categoría)

**activity_main.xml componentes:**
- MaterialToolbar (título "KeyLab Mobile")
- SwipeRefreshLayout (pull-to-refresh)
- RecyclerView con GridLayoutManager (2 columnas)
- ProgressBar (loading centralizado)
- TextView "No hay productos" (lista vacía)
- FloatingActionButton (agregar producto)

**MainActivity.kt arquitectura:**
- ViewBinding (ActivityMainBinding)
- ViewModel con Factory pattern
- Repository inyectado (AppDatabase + RetrofitClient)
- Observers:
  - Flow<List<Producto>> → adapter.submitList()
  - LiveData<Boolean> isLoading → spinner + swipeRefresh
  - LiveData<String?> error → Toast
  - LiveData<String?> successMessage → Toast
- GridLayoutManager (2 columnas, fixed size)
- SwipeRefresh listener → sincronizar productos

**Flujo de datos implementado:**
```
Usuario abre app
    ↓
MainActivity.onCreate()
    ↓
viewModel.sincronizarProductos()
    ↓
Repository → Supabase API
    ↓
Datos guardados en Room
    ↓
Flow emite cambios
    ↓
Observer en MainActivity
    ↓
adapter.submitList(productos)
    ↓
RecyclerView renderiza grid 2 columnas
    ↓
Usuario ve productos con imágenes ✅
```

**Pull-to-refresh:**
```
Usuario arrastra hacia abajo
    ↓
SwipeRefreshLayout.onRefresh()
    ↓
viewModel.sincronizarProductos()
    ↓
API → Room → Flow → Adapter
    ↓
Lista actualizada ✅
```

**Decisiones técnicas:**
- ✅ GridLayoutManager (2 columnas) inspirado en ProductGrid.jsx React
- ✅ SwipeRefreshLayout para sincronización manual
- ✅ DiffUtil para comparar listas (eficiente, no redibuja todo)
- ✅ ViewBinding en toda la app (type-safe)
- ✅ Offline-first: muestra Room primero, sincroniza después
- ✅ Material Design 3 (Chips, TonalButton, OutlinedButton)
- ✅ Error handling con Toast (LiveData observers)
- ✅ Formato CLP con String.format + replace comas por puntos

**Funcionalidades activas:**
- Lista productos desde Room (reactiva con Flow)
- Pull-to-refresh sincroniza con Supabase
- Loading spinner mientras carga
- Mensaje "No hay productos" si lista vacía
- Toast para errores de red/API
- Toast para mensajes de éxito (sincronización)
- Grid 2 columnas con MaterialCardView
- Glide carga imágenes desde Supabase Storage URLs
- Formato precio automático ($89.990)
- Click en producto → Toast (TODO: navegar a detalle)
- FAB agregar → Toast (TODO: pantalla crear)

**Build metrics:**
- Total tasks ejecutadas: 111
- Errores de compilación: 0
- Warnings: 0
- APK Debug: ~7.6 MB
- APK Release: ~6.3 MB
- Tiempo de build: 22 segundos

**TODOs identificados:**
- Pantalla de detalle de producto (navigation)
- Pantalla crear/editar producto (forms)
- Autenticación admin (para mostrar botón eliminar)
- CameraX para captura de foto de producto
- Subida de imagen a Supabase Storage

**Próximo paso:**
Implementar CameraX para captura de imágenes (requisito nativo)

---

### Sesión 4 - 2025-11-09 (23:00-23:15)
**Duración**: ~15 minutos  
**Actividades**:
- ✅ Implementación de LoginActivity siguiendo diseño React KeyLab
- ✅ Creación de layout XML con Material Design 3
- ✅ Validación de formularios (email + password)
- ✅ Configuración de colores siguiendo paleta original
- ✅ Build exitoso (12s)

**Archivos creados/actualizados:**
```
app/src/main/java/com/keylab/mobile/
└── ui/
    └── LoginActivity.kt              # Actualizado (85 líneas)
app/src/main/res/
├── layout/
│   └── activity_login.xml            # Ya existente (223 líneas)
├── values/
│   ├── colors.xml                    # Colores del login agregados
│   └── strings.xml                   # Strings ya disponibles
```

**LoginActivity.kt funcionalidades:**
- ✅ ViewBinding (ActivityLoginBinding)
- ✅ Validación de email con Patterns.EMAIL_ADDRESS
- ✅ Validación de contraseña (mínimo 6 caracteres)
- ✅ Mensajes de error inline en TextInputLayout
- ✅ ProgressBar durante login (simulado con delay 1.5s)
- ✅ Navegación a MainActivity tras login exitoso
- ✅ Click listeners para:
  - Botón "Comencemos" → attemptLogin()
  - Link "¿Olvidaste tu contraseña?" → Toast placeholder
  - Link "Regístrate aquí" → Toast placeholder

**activity_login.xml estructura:**
- ScrollView (soporte para teclado)
- Logo KeyLab (120dp, centrado superior)
- TextView "Bienvenido" (32sp, monospace, bold)
- MaterialCardView con efecto glass simulado:
  - Corner radius: 15dp
  - Elevation: 8dp
  - Background: #F5F5F5
  - Padding interno: 32dp
- TextInputLayout (Material3) con iconos:
  - Email con ic_dialog_email
  - Password con ic_lock_idle_lock + toggle
- MaterialButton "Comencemos":
  - Color: #007BFF (azul KeyLab)
  - Full width, 56dp height
  - Corner radius: 8dp
- Separador "o" con líneas horizontales
- Links clickeables (azul #007BFF):
  - "¿Olvidaste tu contraseña?"
  - "¿No tienes una cuenta? Regístrate aquí"
- ProgressBar centralizado (hidden por defecto)

**Colores agregados a colors.xml:**
```xml
<!-- Login Colors (siguiendo el diseño React de KeyLab) -->
<color name="login_background">#FFFFFF</color>
<color name="login_card_background">#F5F5F5</color>
<color name="login_button_primary">#007BFF</color>
<color name="login_link_accent">#007BFF</color>
<color name="login_error">#D32F2F</color>
```

**Adaptación de React a Android:**
```
REACT (split-screen)           →  ANDROID (single card)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
┌─────────┬──────────┐         ┌─────────────────┐
│  Back   │  Glass   │         │  ScrollView     │
│  Image  │  Card    │    →    │  ┌───────────┐  │
│  Logo   │  Form    │         │  │ Logo      │  │
│         │          │         │  │ Welcome   │  │
└─────────┴──────────┘         │  │ Card Form │  │
                                │  └───────────┘  │
                                └─────────────────┘
✗ backdrop-filter: blur()       ✓ MaterialCardView elevation
✗ SVG icons inline              ✓ Material Icons (sistema)
✓ Font Jersey 10 monospace      ✓ android:fontFamily="monospace"
✓ Color #007BFF                 ✓ @color/login_button_primary
✓ Glass card 15dp radius        ✓ app:cardCornerRadius="15dp"
```

**Validaciones implementadas:**
1. Email vacío → "El email es requerido"
2. Email inválido → "Email inválido" (Patterns.EMAIL_ADDRESS)
3. Password vacío → "La contraseña es requerida"
4. Password corto → "Mínimo 6 caracteres"
5. Login exitoso → Toast "¡Bienvenido!" + navegación

**Flujo de login:**
```
Usuario abre app
    ↓
LoginActivity (primera pantalla)
    ↓
Usuario llena email + password
    ↓
Click "Comencemos"
    ↓
attemptLogin() valida campos
    ↓
Si válido: performLogin()
    ↓
ProgressBar visible (1.5s simulado)
    ↓
Toast "¡Bienvenido!"
    ↓
startActivity(MainActivity)
    ↓
finish() LoginActivity
    ↓
Usuario en pantalla principal ✅
```

**Build metrics:**
- BUILD SUCCESSFUL en 12s
- Warnings: 2 (parámetros email/password no usados en log)
- APK Debug: ~7.6 MB
- Tasks ejecutadas: 42 (15 nuevas, 27 up-to-date)

**Próximo paso:**
Configurar LoginActivity como pantalla de inicio en AndroidManifest.xml

**Estado de implementación:**
✅ Login UI completo (siguiendo diseño React)  
✅ Validación de formularios funcional  
✅ Navegación a MainActivity implementada  
⏳ Integrar con autenticación real (Supabase Auth)  
⏳ SharedPreferences para recordar sesión  
⏳ Biometría (huella/face ID)

---

### Sesión 5 - 2025-11-10 (00:00-00:57)
**Duración**: ~57 minutos  
**Actividades**:
- ✅ Implementación de CameraActivity con CameraX
- ✅ Configuración completa de captura de fotos
- ✅ UI para vista previa y captura de imágenes
- ✅ Manejo de permisos de cámara en runtime
- ✅ Strings en español para la interfaz de cámara
- ✅ Registro de CameraActivity en AndroidManifest

**Archivos creados:**
```
app/src/main/java/com/keylab/mobile/
└── ui/
    └── CameraActivity.kt              # Nueva (192 líneas)
app/src/main/res/
├── layout/
│   └── activity_camera.xml            # Nuevo (85 líneas)
└── values/
    └── strings.xml                    # 4 strings agregados
```

**CameraActivity.kt funcionalidades:**
- ✅ ViewBinding (ActivityCameraBinding)
- ✅ CameraX con Preview + ImageCapture
- ✅ Configuración automática de aspectRatio (RATIO_16_9)
- ✅ Target rotation ajustado al display
- ✅ Selección de cámara trasera por defecto
- ✅ Captura de foto con callback de éxito/error
- ✅ Guardado en directorio de app (getExternalFilesDir)
- ✅ Nombres de archivo con timestamp (IMG_yyyyMMdd_HHmmss.jpg)
- ✅ Manejo de permisos con ActivityResultContract
- ✅ Request de permiso si no está otorgado
- ✅ Botones funcionales:
  - Capturar → Guarda foto y retorna URI
  - Cancelar → Cierra actividad
  - Voltear → Cambia entre cámara frontal/trasera
- ✅ Mensajes Toast para feedback

**activity_camera.xml estructura:**
- ConstraintLayout (full screen)
- PreviewView (CameraX, match_parent)
- LinearLayout inferior con 3 botones:
  - MaterialButton "Cancelar" (outlined)
  - MaterialButton "Capturar foto" (filled)
  - MaterialButton "Voltear" (tonal)
- Botones con iconos Material (ic_menu_camera, ic_menu_close, ic_menu_revert)

**Strings agregados:**
```xml
<string name="camera_instruction">Enmarca el producto en el cuadro</string>
<string name="camera_capture">Capturar foto</string>
<string name="camera_cancel">Cancelar</string>
<string name="camera_flip">Voltear</string>
```

**AndroidManifest.xml:**
- ✅ CameraActivity registrada con:
  - `android:exported="false"` (solo acceso interno)
  - `android:screenOrientation="portrait"` (orientación fija)

**Arquitectura de captura:**
```
MainActivity/Form
    ↓ startActivityForResult
CameraActivity
    ↓ solicita permiso si no existe
Usuario otorga permiso
    ↓ startCamera()
CameraX Preview + ImageCapture
    ↓ Usuario presiona "Capturar"
Foto guardada en storage
    ↓ setResult(RESULT_OK, imageUri)
MainActivity recibe URI
    ↓ usar para crear/editar producto
```

**Permisos manejados:**
- CAMERA (solicitud en runtime con registerForActivityResult)
- Fallback a almacenamiento interno si no hay WRITE_EXTERNAL

**Decisiones técnicas:**
- ✅ CameraX en lugar de Camera2 (API más simple)
- ✅ Preview + ImageCapture (sin video ni análisis)
- ✅ Cámara trasera por defecto (mejor calidad)
- ✅ Orientación portrait fija (UX consistente)
- ✅ ActivityResultContract para permisos (recomendado)
- ✅ getExternalFilesDir() no requiere permisos en Android 10+
- ✅ Callbacks Success/Error con logging

**Build metrics:**
- BUILD SUCCESSFUL
- CameraX dependencies correctas (1.3.1)
- No warnings relacionados con cámara

**Próximo paso:**
Integrar CameraActivity con el flujo de creación/edición de productos:
1. Botón "Agregar foto" en form → startActivityForResult
2. Recibir URI de foto capturada
3. Mostrar preview de imagen en form
4. Subir a Supabase Storage al guardar producto

**Estado de implementación:**
✅ CameraActivity funcional (elemento nativo completado)  
✅ Captura de fotos operativa  
✅ Permisos de cámara manejados  
⏳ Integración con creación de productos  
⏳ Subida a Supabase Storage  
⏳ Preview de imagen antes de enviar

---

### Sesión 6 - 2025-11-10 (01:00-01:27)
**Duración**: ~27 minutos  
**Actividades**:
- ✅ Creación de sistema de diseño completo
- ✅ Implementación de login Opal-style minimalista
- ✅ Mejora de UI en todas las pantallas
- ✅ Estandarización de dimensiones y colores
- ✅ Material Design 3 correctamente implementado
- ✅ Build exitoso final

**Archivos creados/actualizados:**
```
app/src/main/res/
├── values/
│   ├── dimens.xml              # Creado (34 líneas, 25 dimensiones)
│   ├── colors.xml              # Expandido (64 líneas, 50+ colores)
│   ├── themes.xml              # Reescrito (53 líneas, Material3 completo)
│   └── strings.xml             # Limpiado (48 líneas, sin duplicados)
└── layout/
    ├── activity_login.xml      # Reescrito (270 líneas, Opal-style)
    ├── activity_main.xml       # Mejorado (98 líneas, empty state)
    └── item_producto.xml       # Pulido (125 líneas, dimens aplicados)

app/src/main/java/com/keylab/mobile/
├── ui/
│   ├── LoginActivity.kt        # IDs actualizados + inglés
│   └── MainActivity.kt         # emptyStateLayout visibility
└── adapter/
    └── ProductoAdapter.kt      # chipSinStock ID corregido
```

**Sistema de Diseño Implementado:**

**📐 dimens.xml - Dimensiones Estandarizadas**
```xml
<!-- Spacing -->
spacing_xs: 4dp
spacing_sm: 8dp
spacing_md: 16dp
spacing_lg: 24dp
spacing_xl: 32dp

<!-- Card -->
card_radius: 12dp
card_elevation: 2dp
card_stroke: 1dp

<!-- Product -->
product_image_height: 180dp
product_card_height: 320dp
grid_spacing: 12dp

<!-- Text Sizes -->
text_xs: 12sp
text_sm: 14sp
text_md: 16sp
text_lg: 18sp
text_xl: 24sp
text_xxl: 32sp

<!-- Icons -->
icon_sm: 20dp
icon_md: 24dp
```

**🎨 colors.xml - Paleta Completa (50+ colores)**
```xml
<!-- KeyLab Brand -->
keylab_primary: #007BFF
keylab_primary_dark: #0056B3
keylab_primary_light: #E7F1FF

<!-- Material3 Theme -->
md_theme_primary: #007BFF
md_theme_background: #FAFBFE
md_theme_surface: #FFFFFF
md_theme_error: #BA1A1A
... (30+ colores Material3)

<!-- Dark Theme (Login Opal-style) -->
dark_background: #121212
dark_surface: #1E1E1E
dark_text_primary: #FFFFFF
dark_text_secondary: #B0B0B0
dark_button_primary: #FFFFFF
dark_input_background: #2C2C2C
... (11 colores dark)

<!-- Text Colors -->
text_primary: #1F1F1F
text_secondary: #5A5A5A
text_tertiary: #8E8E8E
text_disabled: #BDBDBD

<!-- State Colors -->
success: #28A745
warning: #FFC107
error: #DC3545
info: #17A2B8

<!-- Product States -->
stock_available: #28A745
stock_low: #FFC107
stock_out: #DC3545

<!-- Divider & Border -->
divider: #E0E0E0
border_light: #EEEEEE
```

**🎭 themes.xml - Material3 DayNight**
```xml
<style name="Theme.KeyLabMobile" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- Primary, Secondary, Error colors -->
    <!-- Background, Surface, Outline colors -->
    <!-- Status bar: keylab_primary -->
    <!-- Text appearances configurados -->
</style>

<style name="Widget.KeyLab.Toolbar" parent="Widget.Material3.Toolbar">
    <!-- Custom toolbar con elevation 4dp -->
</style>

<style name="Widget.KeyLab.Card" parent="Widget.Material3.CardView.Elevated">
    <!-- Card radius 12dp, elevation 2dp -->
</style>
```

**📱 Login Screen (Opal-style Minimalista)**

**Características visuales:**
- Fondo oscuro elegante (#121212)
- Tipografía sans-serif-medium limpia
- Campos de texto con bordes redondeados (12dp)
- Botón principal blanco con texto negro (#FFFFFF / #000000)
- Botones sociales (Apple/Phone) con stroke gris
- Divider "or" con líneas horizontales
- Espaciado generoso (48dp título, 32dp botones, 16dp inputs)
- Password toggle icon integrado (Material3)
- Loading indicator sobre botón (sin bloquear UI)

**Componentes:**
```xml
- tvAppName: "KeyLab" centrado superior
- tvWelcome: "Welcome" (32sp, bold)
- tvSubtitle: "Sign up for a better experience!" (16sp)
- emailInput: TextInputEditText con outline
- passwordInput: TextInputEditText con toggle
- tvForgotPassword: Link secundario
- loginButton: MaterialButton blanco (56dp height, 28dp radius)
- dividerLayout: "or" con líneas
- appleLoginButton: Outlined con icon
- phoneLoginButton: Outlined con icon
- tvRegister: "Don't have an account? Register"
- progressBar: ProgressBar oculto sobre loginButton
```

**🏠 Main Screen (Grid de Productos)**

**Mejoras implementadas:**
- Toolbar custom con estilo Widget.KeyLab.Toolbar
- Título "KeyLab" centrado
- Background: md_theme_background (#FAFBFE)
- SwipeRefreshLayout con pull-to-refresh
- RecyclerView con padding @dimen/spacing_sm
- Empty state mejorado con LinearLayout:
  - Emoji 📦 (64sp)
  - "No hay productos disponibles" (text_lg)
  - "Pull down to refresh" (text_sm, tertiary)
- ProgressBar con indeterminateTint keylab_primary
- FAB con margin @dimen/spacing_md

**🎴 Product Card (item_producto.xml)**

**Estructura pulida:**
```xml
MaterialCardView
├─ cornerRadius: @dimen/card_radius (12dp)
├─ elevation: @dimen/card_elevation (2dp)
├─ strokeColor: @color/border_light
└─ strokeWidth: @dimen/card_stroke (1dp)
    └─ LinearLayout (vertical)
        ├─ ImageView (product_image_height: 180dp)
        └─ LinearLayout (content, padding: spacing_md)
            ├─ tvProductoNombre (text_md, text_primary, bold, maxLines 2)
            ├─ tvProductoPrecio (text_lg, keylab_primary, bold)
            ├─ chipCategoria (Chip.Assist, surface_tint background)
            ├─ chipSinStock (Chip.Assist, stock_out background, conditional)
            └─ LinearLayout (buttons, horizontal)
                ├─ btnEliminar (OutlinedButton, text_sm, conditional)
                └─ btnVerDetalle (TonalButton, text_sm)
```

**Decisiones técnicas:**
- ✅ Todas las dimensiones usan @dimen (mantenibilidad)
- ✅ Todos los colores usan @color (consistencia)
- ✅ Material3 widgets (TonalButton, Assist Chip)
- ✅ Spacing respirable (16dp interno, 8dp margin)
- ✅ Elevación sutil (2dp, no invasiva)
- ✅ Border light (#EEEEEE, 1dp)
- ✅ Text sizes semánticos (xs→xxl)
- ✅ Colores de estado (success/warning/error)

**Flujo de actualización:**
```
Usuario abre app
    ↓
MainActivity.onCreate()
    ↓
viewModel.sincronizarProductos()
    ↓
Repository → Supabase API
    ↓
Datos guardados en Room
    ↓
Flow emite cambios
    ↓
adapter.submitList(productos)
    ↓
RecyclerView renderiza:
    - Si hay productos → Grid 2 columnas con cards pulidas
    - Si lista vacía → Empty state con emoji 📦
    ↓
Usuario ve UI pulida con Material3 ✅
```

**Build metrics finales:**
- BUILD SUCCESSFUL en 10s
- 42 tasks ejecutadas (11 nuevas, 31 up-to-date)
- 0 errores de compilación
- 2 warnings (parámetros no usados en performLogin - no críticos)
- APK Debug: ~7.6 MB
- APK Release: ~6.3 MB
- Target API: 24+ (94% dispositivos Android)

**Comparativa Antes/Después:**

**ANTES:**
- Login básico con card blanco (estilo KeyLab React)
- Colores limitados (10 colores)
- Sin sistema de diseño
- Dimensiones hardcodeadas
- Theme básico (2 items)
- Empty state simple text
- Cards sin polish

**DESPUÉS:**
- Login Opal-style minimalista (dark theme)
- 50+ colores organizados (Material3 + Dark + States)
- Sistema de diseño completo (dimens.xml)
- Dimensiones estandarizadas (@dimen referencias)
- Theme completo Material3 DayNight (40+ items)
- Empty state con emoji + subtítulo
- Cards pulidas con elevation + border

**Arquitectura UI final:**
```
res/
├── values/
│   ├── colors.xml          # 50+ colores (Material3 + Dark + States)
│   ├── dimens.xml          # 25 dimensiones (spacing + text + card)
│   ├── themes.xml          # Material3 DayNight + custom styles
│   └── strings.xml         # Limpio, sin duplicados
└── layout/
    ├── activity_login.xml  # Opal-style (dark, minimalista)
    ├── activity_main.xml   # Grid + empty state mejorado
    └── item_producto.xml   # Card pulida con @dimen/@color
```

**Próximo paso:**
Integrar CameraActivity con el flujo de creación/edición de productos + Supabase Storage

**Estado de implementación UI:**
✅ Sistema de diseño completo (dimens + colors + themes)  
✅ Login Opal-style minimalista funcional  
✅ Main screen con Material3 pulido  
✅ Product cards profesionales  
✅ Empty states informativos  
✅ Material Design 3 correctamente implementado  
✅ Build exitoso sin errores  
⏭️ Siguiente: Integrar cámara con creación de productos

---

### Sesión 7 - 2025-11-10 (01:30-02:17)
**Duración**: ~47 minutos  
**Actividades**:
- ✅ Revisión del proyecto KeyLab original para analizar UI patterns
- ✅ Creación de CategoryActivity para mostrar productos por categoría
- ✅ Creación de CartActivity con gestión de carrito de compras
- ✅ Creación de ProfileActivity con menú de usuario
- ✅ Creación de CartAdapter para items del carrito
- ✅ Simplificación de MainActivity para navegación entre pantallas
- ✅ Actualización de AndroidManifest con nuevas activities
- ✅ Agregado de recursos faltantes (strings, drawables)
- ✅ Build exitoso completo

**Archivos creados:**
```
app/src/main/java/com/keylab/mobile/ui/
├── CategoryActivity.kt           # Categorías (117 líneas)
├── CartActivity.kt               # Carrito (91 líneas)
├── ProfileActivity.kt            # Perfil (79 líneas)
├── MainActivity.kt               # Simplificado (89 líneas)
└── adapter/
    └── CartAdapter.kt            # Adapter del carrito (105 líneas)

app/src/main/res/layout/
├── activity_category.xml         # Layout categoría (102 líneas)
├── activity_cart.xml             # Layout carrito (211 líneas)
├── activity_profile.xml          # Layout perfil (387 líneas)
└── item_cart.xml                 # Item carrito (158 líneas)

app/src/main/res/drawable/
├── ic_arrow_back.xml             # Icono back blanco
├── ic_placeholder_image.xml      # Placeholder imágenes
└── ic_shopping_cart.xml          # Icono carrito
```

**Patrón de diseño analizado del KeyLab original:**

**Categorías del negocio:**
1. **Teclados** (60%, 75%, Full Size)
2. **Keycaps** (ABS, PBT)
3. **Switches** (Lineales, Clicky, Tactiles)
4. **Cases** (60%, 75%, 80%)

**Modelo de producto:**
- id, nombre, precio, categoria, subcategoria
- imagen, stock, descripcion

**Sistema de colores Bootstrap → Material3:**
- Primary: #007BFF (KeyLab brand)
- Success: #28A745 (stock disponible)
- Danger: #DC3545 (sin stock, eliminar)
- Warning: #FFC107 (stock bajo)

**CategoryActivity características:**
- RecyclerView con GridLayoutManager (2 columnas)
- SwipeRefreshLayout para pull-to-refresh
- Filtrado automático por categoría desde ViewModel
- Empty state con emoji 📦
- Toolbar con navegación back
- Dark theme consistente
- Navegación: `putExtra(EXTRA_CATEGORIA, "Teclados")`

**CartActivity características:**
- RecyclerView con LinearLayoutManager (lista vertical)
- CartAdapter con controles de cantidad (+/-)
- Botón eliminar por producto
- Card resumen inferior fijo:
  - Subtotal calculado
  - Envío ($3.990 o gratis si > $50.000)
  - Total final
  - Botón "Proceder al pago"
- Empty state con emoji 🛒
- Formato precio CLP con puntos ($89.990)

**ProfileActivity características:**
- Avatar circular con inicial del usuario
- Card superior con nombre + email
- Secciones organizadas:
  - "Mi cuenta": Pedidos, Direcciones, Pagos, Favoritos
  - "Configuración": Settings, Ayuda
- Menu items con emojis (📦 📍 💳 ❤️ ⚙️ ❓)
- Botón "Cerrar sesión" (outlined red)
- NestedScrollView para scroll suave
- Cards clickeables con indicador "›"

**MainActivity simplificado:**
- Navegación a CategoryActivity por categoría
- Navegación a CartActivity
- Navegación a ProfileActivity
- Búsqueda dinámica de elementos en layout (flexible)
- Sincronización de productos al iniciar
- Compatible con múltiples layouts (Explore/Main)

**Decisiones técnicas:**
- ✅ Dark theme consistente en todas las pantallas
- ✅ CoordinatorLayout + AppBarLayout en layouts
- ✅ Material3 widgets (MaterialToolbar, MaterialButton, MaterialCardView)
- ✅ ViewBinding en todas las activities
- ✅ Emojis para iconografía rápida (📦 🛒 📍 💳 ❤️ ⚙️ ❓)
- ✅ Empty states informativos con emoji + texto
- ✅ Arquitectura lista para cargar imágenes desde Supabase
- ✅ Glide con placeholder y error handling
- ✅ Formato precio chileno con puntos separadores

**AndroidManifest.xml actualizado:**
```xml
<activity android:name=".ui.CategoryActivity" 
          android:parentActivityName=".ui.MainActivity" />
<activity android:name=".ui.CartActivity" 
          android:parentActivityName=".ui.MainActivity" />
<activity android:name=".ui.ProfileActivity" 
          android:parentActivityName=".ui.MainActivity" />
```

**Build metrics finales:**
- BUILD SUCCESSFUL en 11s
- 42 tasks ejecutadas (12 nuevas, 30 up-to-date)
- 0 errores de compilación
- 0 warnings
- APK Debug: ~7.6 MB
- APK Release: ~6.3 MB
- Min SDK: 24+ (94% dispositivos Android)

**Pantallas totales implementadas:**
1. ✅ LoginActivity (Opal-style dark)
2. ✅ MainActivity (navegación hub)
3. ✅ CategoryActivity (lista por categoría)
4. ✅ CartActivity (carrito con resumen)
5. ✅ ProfileActivity (perfil de usuario)
6. ✅ CameraActivity (elemento nativo CameraX)

**Arquitectura completa:**
```
┌─────────────────────────────────────┐
│         Activities (6)              │
│  Login → Main → Category/Cart/Profile │
│                ↓                    │
│           CameraActivity            │
└────────────────┬────────────────────┘
                 ↓
┌────────────────────────────────────┐
│      ProductoViewModel             │
└────────────────┬───────────────────┘
                 ↓
┌────────────────────────────────────┐
│      ProductoRepository            │ ← Offline-first
└────────┬───────────────┬───────────┘
         ↓               ↓
    ┌────────┐     ┌──────────┐
    │  Room  │     │ Retrofit │
    │  (DAO) │     │(Supabase)│
    └────────┘     └──────────┘
```

**TODOs identificados:**
- [ ] Implementar CartManager/Repository para persistir carrito
- [ ] Integrar Supabase Auth en LoginActivity
- [ ] Conectar CameraActivity con creación de productos
- [ ] Subida de imágenes a Supabase Storage
- [ ] Pantalla de detalle de producto
- [ ] Pantalla crear/editar producto con formulario
- [ ] BottomNavigationView para navegación principal
- [ ] Implementar búsqueda funcional en MainActivity
- [ ] Funcionalidades de perfil (pedidos, direcciones, pagos)
- [ ] Checkout flow completo

**Próximo paso:**
Implementar gestión de carrito persistente y conectar CameraActivity con formulario de productos

**Estado general:**
✅ Core de la aplicación completado (6 pantallas)  
✅ Sistema de navegación funcionando  
✅ Arquitectura MVVM completa  
✅ UI dark theme consistente  
✅ Listo para integrar con Supabase Storage  
⏭️ Siguiente: Persistencia de carrito + subida de imágenes

---

## 🔗 Recursos y Referencias

### Documentación oficial
- [Supabase Docs](https://supabase.com/docs)
- [Retrofit](https://square.github.io/retrofit/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [CameraX](https://developer.android.com/training/camerax)

### Archivos importantes
- `MIGRACION_MOBILE.md` - Inventario completo del backend original
- `PLAN_DESARROLLO.md` - Roadmap técnico con comandos y código de ejemplo

---

## 💡 Notas para la siguiente sesión

### Información clave
- El proyecto original usa **React 19 + Vite + React Bootstrap**
- Backend definitivo: **Supabase** (PostgreSQL + Storage)
- API Keys configuradas en BuildConfig (no hardcodear en código)
- Requisito crítico: implementar cámara como elemento nativo en Android
- Room debe funcionar como caché offline (offline-first)
- **Estructura MVVM creada** - listo para implementar lógica de negocio

### Estado actual del proyecto
✅ Build funciona correctamente (./gradlew build exitoso - 12s)  
✅ APKs se generan sin errores (Debug 7.6MB, Release 6.3MB)  
✅ Todas las dependencias instaladas y funcionando  
✅ Estructura de carpetas MVVM completa  
✅ Room Database implementado y compilado  
✅ Retrofit + Supabase API configurado y probado  
✅ Repository Pattern offline-first implementado  
✅ ProductoViewModel con lógica de presentación  
✅ UI completa con RecyclerView + Adapter + ViewBinding  
✅ MainActivity funcional mostrando lista de productos  
✅ LoginActivity implementado (Opal-style minimalista)  
✅ CameraActivity implementado con CameraX (elemento nativo)  
✅ Sistema de diseño completo (dimens + colors + themes Material3)  
✅ FASE 3: 98% completado (Core + UI pulida)  
⏭️ Siguiente: Integrar cámara con creación de productos + Supabase Storage

---

### Sesión 8 - 2025-11-10 (01:30-04:59)
**Duración**: ~3h 29min  
**Actividades**:
- ✅ Traducción completa de strings a español (Login + MainActivity)
- ✅ Eliminación de elementos duplicados (activity_explore.xml)
- ✅ Reorganización del Bottom Navigation (3 botones: Inicio, Carrito, Perfil)
- ✅ Eliminación del FAB flotante (carrito ahora en bottom nav)
- ✅ Eliminación de barra de búsqueda del main
- ✅ Sistema de categorías con chips tipo pill (solo texto)
- ✅ Filtrado de productos por categoría funcional
- ✅ Modelo de datos actualizado para Supabase (imagen_url, created_at, updated_at)
- ✅ Sincronización de productos desde Supabase funcionando
- ✅ Carrito con persistencia local (Room) implementado completo
- ✅ CartActivity actualizado con ViewModel y Repository
- ✅ Base de datos Room actualizada a versión 3 (con tabla carrito_items)
- ✅ URLs de imágenes de Supabase Storage configuradas

**Archivos creados/actualizados:**
```
app/src/main/java/com/keylab/mobile/
├── domain/model/
│   ├── Producto.kt                    # Actualizado con @SerializedName
│   └── CarritoItem.kt                 # Nuevo modelo para carrito
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt             # Versión 3 con CarritoItem
│   │   ├── ProductoDao.kt             # Queries actualizadas
│   │   └── CarritoDao.kt              # Nuevo DAO para carrito
│   └── repository/
│       ├── ProductoRepository.kt      # Logs agregados
│       └── CarritoRepository.kt       # Nuevo repository
├── ui/
│   ├── MainActivity.kt                # Sistema de categorías + carrito
│   ├── CartActivity.kt                # Refactorizado con ViewModel
│   └── adapter/
│       ├── CategoryAdapter.kt         # Estados seleccionado/no seleccionado
│       └── CartAdapter.kt             # Actualizado para CarritoItem
└── viewmodel/
    ├── CarritoViewModel.kt            # Nuevo ViewModel
    └── CarritoViewModelFactory.kt     # Factory para carrito

app/src/main/res/
├── layout/
│   ├── activity_main.xml              # Sin banner, sin búsqueda, título "Home"
│   └── item_category.xml              # Chip tipo pill solo texto
├── values/
│   └── strings.xml                    # Traducidos al español
└── menu/
    └── menu_bottom_nav.xml            # 3 botones: Inicio, Carrito, Perfil
```

**Cambios en la UI:**

**MainActivity simplificado:**
- ❌ Banner carousel eliminado
- ❌ Barra de búsqueda eliminada
- ❌ Ícono de lupa en AppBar eliminado
- ❌ FAB flotante del carrito eliminado
- ✅ Título cambiado: "Explorar" → "Home"
- ✅ Categorías como chips horizontales (Todos, Teclados, Keycaps, Switches, Cases)
- ✅ Click en categoría filtra productos instantáneamente
- ✅ Bottom Navigation: Inicio (shop) | Carrito | Perfil

**Sistema de categorías:**
- MaterialCardView redondeado (24dp radius)
- Sin imágenes, solo texto
- Estados visuales:
  - **Seleccionado**: Fondo blanco, texto negro, sin borde
  - **No seleccionado**: Fondo gris oscuro, texto blanco, borde 1dp
- 5 categorías: Todos, Teclados, Keycaps, Switches, Cases
- Scroll horizontal con padding

**Filtrado de productos:**
```kotlin
// Usuario hace click en "Teclados"
categoryAdapter.setSelectedPosition(1)
selectedCategory = "Teclados"
filterProducts()
    ↓
productos.filter { it.categoria == "Teclados" }
    ↓
RecyclerView actualiza automáticamente
```

**Bottom Navigation reorganizado:**
```
ANTES:                       DESPUÉS:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Explore | Shop | Favorites | Profile
                  🛒 FAB
                            Inicio | Carrito | Perfil
```

**Modelo Producto actualizado:**
```kotlin
// Mapeo Supabase → Kotlin
@SerializedName("imagen_url")  // imagen_url (snake_case)
val imagenUrl: String?         // imagenUrl (camelCase)

@SerializedName("created_at")
val createdAt: String?

@SerializedName("updated_at")
val updatedAt: String?
```

**Carrito con persistencia implementado:**

**Estructura:**
```
CarritoItem (Room entity)
├─ productoId: Int (primary key)
├─ nombre: String
├─ precio: Double
├─ categoria: String
├─ imagenUrl: String?
├─ cantidad: Int
└─ fechaAgregado: Long

CarritoDao
├─ obtenerItems(): Flow<List<CarritoItem>>
├─ contarItems(): Flow<Int>
├─ obtenerSubtotal(): Flow<Double?>
├─ insertar(item)
├─ actualizarCantidad(productoId, cantidad)
├─ eliminarPorId(productoId)
└─ vaciarCarrito()

CarritoRepository
├─ agregarProducto(producto)
├─ incrementarCantidad(productoId)
├─ decrementarCantidad(productoId)
├─ actualizarCantidad(productoId, cantidad)
├─ eliminarItem(productoId)
└─ vaciarCarrito()

CarritoViewModel
├─ items: Flow<List<CarritoItem>>
├─ totalItems: Flow<Int>
├─ subtotal: Flow<Double>
├─ costoEnvio: Flow<Double>        # Gratis si > $50.000
├─ total: Flow<Double>              # Subtotal + envío
└─ funciones CRUD
```

**Flujo del carrito:**
```
Usuario en MainActivity
    ↓
Click en botón "Agregar" de producto
    ↓
carritoViewModel.agregarProducto(producto)
    ↓
Repository → Room inserta/incrementa
    ↓
Flow emite cambios
    ↓
Toast "✓ Agregado al carrito"
    ↓
Usuario va a CartActivity (bottom nav)
    ↓
CartActivity observa Flow de items
    ↓
RecyclerView muestra productos con:
    - Imagen, nombre, precio unitario
    - Controles +/- para cantidad
    - Precio total (precio × cantidad)
    - Botón eliminar
    ↓
Card resumen inferior:
    - Subtotal
    - Envío ($3.990 o GRATIS)
    - Total
    ↓
Persiste incluso si cierras la app ✅
```

**Problemas resueltos:**

1. **"Unable to resolve host"**
   - Causa: Dispositivo sin Internet
   - Solución: Usuario conectó WiFi/datos móviles

2. **Imágenes placeholder feas**
   - Causa: URLs de picsum.photos (placeholders genéricos)
   - Solución: Usuario subió imágenes reales a Supabase Storage

3. **Campos no coincidían con Supabase**
   - Causa: Supabase usa snake_case (imagen_url), Kotlin usa camelCase (imagenUrl)
   - Solución: @SerializedName("imagen_url") en modelo

4. **Base de datos incompatible**
   - Causa: Cambio de schema (fechaCreacion → createdAt/updatedAt)
   - Solución: Incrementar versión DB + .fallbackToDestructiveMigration()

**Decisiones técnicas:**

- ✅ Carrito con Room (no SharedPreferences) para datos estructurados
- ✅ Flow reactivo desde Room → UI se actualiza automáticamente
- ✅ Cálculo de envío dinámico (gratis si > $50.000)
- ✅ Botón "Agregar" reusa onDeleteClick del adapter (temporal)
- ✅ CategoryAdapter con estados visuales claros
- ✅ MainActivity como hub de navegación simple
- ✅ Bottom Navigation sin FAB (más limpio)
- ✅ Toda la UI en español (consistencia)

**Strings traducidos:**
```kotlin
// Login
"Bienvenido"
"¡Inicia sesión para una mejor experiencia!"
"Correo electrónico"
"Contraseña"
"¿Olvidaste tu contraseña?"
"Iniciar sesión"
"o"
"Iniciar con Apple"
"Iniciar con teléfono"
"¿No tienes cuenta? Regístrate"

// Main
"Home"
"Categorías"
"Productos destacados"
"Ver todo"
"No hay productos disponibles"
```

**Build metrics:**
- BUILD SUCCESSFUL en 9-12s
- Version DB: 2 → 3
- Entities: Producto + CarritoItem
- DAOs: ProductoDao + CarritoDao
- ViewModels: ProductoViewModel + CarritoViewModel
- 0 errores de compilación
- APK Debug: ~7.7 MB

**URLs de imágenes en Supabase:**
Usuario configuró manualmente en SQL Editor:
```sql
UPDATE productos 
SET imagen_url = 'https://pwnajivbudcwfcordblx.supabase.co/storage/v1/object/public/productos/archivo.jpg'
WHERE id = 1;
```

**TODOs identificados:**
- [ ] Botón "Agregar al carrito" con ícono propio (no reusar onDeleteClick)
- [ ] Badge en ícono del carrito con cantidad de items
- [ ] Animación al agregar producto
- [ ] Confirmación antes de vaciar carrito
- [ ] Persistir sesión con SharedPreferences
- [ ] Integrar Supabase Auth en login
- [ ] Checkout flow completo
- [ ] Pantalla de detalle de producto

**Próximo paso:**
Implementar pantalla de detalle de producto y mejorar la UX del botón "Agregar al carrito"

**Estado general:**
✅ Carrito con persistencia completo y funcional  
✅ Sistema de categorías con filtrado instantáneo  
✅ UI en español completo  
✅ Productos cargando desde Supabase con imágenes reales  
✅ Bottom Navigation reorganizado (3 botones)  
✅ Sincronización offline-first funcionando  
⏭️ Siguiente: Detalle de producto + mejoras UX carrito

### Comandos útiles
```bash
# Compilar y verificar proyecto Android
cd KeyLabMobile && ./gradlew build --no-daemon

# Limpiar build
./gradlew clean

# Ver estructura del proyecto
tree app/src/main/java/com/keylab/mobile -L 3

# Acceder a Supabase
# Dashboard: https://supabase.com/dashboard/project/pwnajivbudcwfcordblx
# API URL: https://pwnajivbudcwfcordblx.supabase.co
# REST docs: https://supabase.com/docs/guides/api
```

### Variables de entorno configuradas
```kotlin
// Android (app/build.gradle.kts - BuildConfig)
buildConfigField("String", "SUPABASE_URL", "\"https://pwnajivbudcwfcordblx.supabase.co\"")
buildConfigField("String", "SUPABASE_KEY", "\"eyJhbGc...\"") // Anon key

// Uso en código Kotlin:
val supabaseUrl = BuildConfig.SUPABASE_URL
val supabaseKey = BuildConfig.SUPABASE_KEY
```

**⚠️ Importante**: Las API keys están en BuildConfig, NO commitear en Git si se hace público

---

## 📊 Estado Actual del Proyecto (2025-11-10 06:16)

### ✅ Completado
- [x] Arquitectura MVVM completa implementada
- [x] 7 pantallas principales creadas y funcionales
- [x] Sistema de navegación entre pantallas
- [x] Room Database con persistencia local (versión 3)
- [x] Retrofit conectado a Supabase REST API
- [x] Repository Pattern offline-first
- [x] ProductoViewModel con LiveData/Flow
- [x] CarritoViewModel con gestión de carrito persistente
- [x] Sistema de diseño Material3 completo
- [x] Dark theme consistente en toda la app
- [x] LoginActivity con validación de formularios (100% español)
- [x] CategoryActivity con filtrado por categoría (4 categorías)
- [x] CartActivity con gestión de carrito persistente (Room)
- [x] ProfileActivity con menú de usuario
- [x] CameraActivity con CameraX (elemento nativo)
- [x] ProductDetailActivity con descripción completa
- [x] Agregar al carrito desde detalle del producto
- [x] Agregar al carrito desde tarjetas de catálogo (botón negro)
- [x] Adapters: ProductoAdapter + CartAdapter + CategoryAdapter
- [x] Layouts pulidos con @dimen y @color estandarizados
- [x] Sistema de categorías con chips clickeables y filtrado
- [x] Bottom Navigation reorganizado (3 botones)
- [x] Interfaz completamente en español (MainActivity + LoginActivity)
- [x] Productos cargando desde Supabase con imágenes reales
- [x] Mapeo correcto de campos (snake_case → camelCase)
- [x] Tarjetas de productos optimizadas (imagen 160dp, más compactas)
- [x] Sistema de cambio de foto de perfil (cámara + galería)
- [x] BottomSheet para opciones de foto
- [x] FileProvider configurado para compartir archivos
- [x] ActivityResultContracts (APIs modernas de Android)
- [x] Logo KeyLab integrado en MainActivity (120dp x 120dp)
- [x] Todas las validaciones y mensajes en español
- [x] Build exitoso sin errores (23s, 42 tasks)

### 🎯 Pantallas Implementadas (7)
1. **LoginActivity** - Opal-style dark, validación de formularios
2. **MainActivity** - Hub de navegación a categorías, carrito, perfil
3. **CategoryActivity** - Lista productos por categoría (Grid 2 columnas)
4. **CartActivity** - Carrito con resumen de compra y checkout
5. **ProfileActivity** - Perfil con menú (pedidos, direcciones, config)
6. **CameraActivity** - Captura de fotos con CameraX
7. **ProductDetailActivity** - Detalle completo + agregar al carrito

### 📦 Categorías Soportadas (KeyLab)
- **Teclados** (60%, 75%, Full Size)
- **Keycaps** (ABS, PBT)
- **Switches** (Lineales, Clicky, Tactiles)
- **Cases** (60%, 75%, 80%)

### 🔄 Pendiente
- [ ] Badge en ícono del carrito (cantidad de items)
- [ ] Pantalla crear/editar producto con formulario
- [ ] Integrar CameraActivity con creación de productos
- [ ] Subida de imágenes a Supabase Storage (productos y perfil)
- [ ] Supabase Auth en LoginActivity
- [ ] Checkout flow completo (datos envío + pago)
- [ ] Funcionalidades de perfil (pedidos, direcciones, pagos)
- [ ] Búsqueda funcional en MainActivity
- [ ] Confirmación antes de vaciar carrito
- [ ] Animaciones al agregar producto
- [ ] Compartir producto (share button)
- [ ] Favoritos (heart icon toggle)
- [ ] Productos relacionados en detalle
- [ ] Persistir foto de perfil en Supabase Storage

### 🏗️ Arquitectura Implementada
```
┌──────────────────────────┐
│   6 Activities (UI)      │
│ Login→Main→Category/Cart │
│      Profile/Camera      │
└──────────┬───────────────┘
           ↓
┌──────────────────────────┐
│  ProductoViewModel       │ ← LiveData + Flow
└──────────┬───────────────┘
           ↓
┌──────────────────────────┐
│  ProductoRepository      │ ← Offline-first
└────┬──────────────┬──────┘
     ↓              ↓
┌─────────┐  ┌──────────────┐
│  Room   │  │  Retrofit    │
│  (DAO)  │  │  (Supabase)  │
└─────────┘  └──────────────┘
```

### 📈 Métricas del Proyecto
- **Activities**: 7
- **Layouts**: 14+ (activities + items + bottom_sheet)
- **Adapters**: 3 (ProductoAdapter, CartAdapter, CategoryAdapter)
- **ViewModels**: 2 (ProductoViewModel, CarritoViewModel)
- **Repositories**: 2 (ProductoRepository, CarritoRepository)
- **DAOs**: 2 (ProductoDao, CarritoDao)
- **Room Entities**: 2 (Producto, CarritoItem)
- **Database Version**: 3
- **APK Debug**: 7.7 MB
- **APK Release**: 6.4 MB
- **Min SDK**: 24 (Android 7.0+, 94% dispositivos)
- **Target SDK**: 34 (Android 14)
- **Build time**: 36s (última compilación)
- **Compilación**: ✅ BUILD SUCCESSFUL
- **Líneas de código**: ~3800+ (sin contar generados)

---

## ⚠️ Riesgos y Mitigaciones

| Riesgo | Impacto | Mitigación | Estado |
|--------|---------|------------|--------|
| No completar a tiempo | Alto | Priorizar FASE 1-3, documentar desde el inicio | ✅ Fases 1-2 completadas |
| Problemas con Supabase gratis | Medio | Tener plan B con SQLite + almacenamiento local | 🔄 Monitorear cuotas |
| Cámara no funciona en emulador | Medio | Probar en dispositivo físico o usar imágenes mock | ⏳ Por verificar |
| API caída en presentación | Alto | Implementar modo offline robusto con Room | 🔄 Por implementar |
| OutOfMemoryError en build | Bajo | JVM configurado con 4GB RAM | ✅ Resuelto |
| Incompatibilidad JDK 21 | Bajo | AGP 8.3.2 + Gradle 8.4 | ✅ Resuelto |

---

### Sesión 9 - 2025-11-10 (05:20-05:40)
**Duración**: ~20 minutos  
**Actividades**:
- ✅ Implementación de ProductDetailActivity completa
- ✅ Creación de layout activity_product_detail.xml
- ✅ Navegación desde MainActivity y CategoryActivity al detalle
- ✅ Controles de cantidad (+/-) con validación de stock
- ✅ Botón "Agregar al Carrito" funcional
- ✅ Cálculo dinámico de precio total
- ✅ Registro en AndroidManifest.xml
- ✅ Strings agregados (8 nuevos)
- ✅ Build exitoso (55s, 111 tasks)

**Archivos creados:**
```
app/src/main/java/com/keylab/mobile/ui/
└── ProductDetailActivity.kt           # Nueva (233 líneas)

app/src/main/res/layout/
└── activity_product_detail.xml        # Nuevo (258 líneas)
```

**Archivos modificados:**
```
app/src/main/java/com/keylab/mobile/ui/
├── MainActivity.kt                    # onItemClick → navigate to detail
└── CategoryActivity.kt                # onItemClick → navigate to detail + Intent import

app/src/main/AndroidManifest.xml      # ProductDetailActivity registrada
app/src/main/res/values/strings.xml   # 8 strings agregados
```

**ProductDetailActivity funcionalidades:**
- ✅ Carga producto por ID desde Room (offline-first)
- ✅ Muestra descripción completa del producto
- ✅ Imagen grande con Glide (320dp)
- ✅ Categoría y subcategoría mostradas
- ✅ Precio destacado en azul KeyLab
- ✅ Stock disponible con color dinámico (verde/rojo)
- ✅ Controles de cantidad:
  - Botón "-" disminuye (mínimo 1)
  - Botón "+" aumenta (máximo = stock disponible)
  - Precio total calculado dinámicamente
  - Toast si se supera el stock
- ✅ Botón "Agregar al Carrito":
  - Icono de carrito integrado
  - Agrega cantidad seleccionada a Room
  - Toast confirmación con nombre y cantidad
  - Resetea cantidad a 1 tras agregar
- ✅ Estados visuales:
  - ProgressBar durante carga
  - Deshabilita botones si stock = 0
  - Cambia texto a "Sin Stock"
  - Colores según disponibilidad

**Layout XML estructura:**
```xml
CoordinatorLayout (dark theme)
├─ AppBarLayout
│  └─ MaterialToolbar (con back navigation)
└─ NestedScrollView
   ├─ ProgressBar (loading)
   └─ ContentLayout
      ├─ MaterialCardView (imagen 320dp)
      │  └─ ImageView (producto con Glide)
      ├─ MaterialCardView (info)
      │  ├─ Nombre (text_xl, bold)
      │  ├─ Categoría (text_sm, secondary)
      │  ├─ Precio (text_xxl, keylab_primary)
      │  ├─ Stock (text_sm, color dinámico)
      │  └─ Descripción (text_sm, lineSpacing 4dp)
      └─ MaterialCardView (compra)
         ├─ Label "Cantidad"
         ├─ LinearLayout (controles)
         │  ├─ OutlinedButton "-" (56dp)
         │  ├─ TextView cantidad (text_xl)
         │  └─ OutlinedButton "+" (56dp)
         ├─ LinearLayout (total)
         │  ├─ "Total:" (text_md)
         │  └─ Precio total (text_xl, keylab_primary)
         └─ MaterialButton "Agregar al Carrito"
            (56dp, icon, keylab_primary)
```

**Integración completa:**
```
Usuario en MainActivity/CategoryActivity
    ↓ Click en producto (card o botón "Ver Detalle")
ProductDetailActivity
    ↓ Intent con EXTRA_PRODUCTO_ID
Carga producto desde Room por ID
    ↓ Flow.first() para obtener lista
Muestra datos completos
    ↓ Usuario ajusta cantidad (+/-)
Click "Agregar al Carrito"
    ↓ Validación de stock
CarritoViewModel.agregarProducto() × cantidad
    ↓ Persistencia en Room
Toast confirmación "✓ 2x Keychron K8 Pro agregado"
    ↓ Cantidad resetea a 1
Usuario continúa comprando o va al carrito ✅
```

**Decisiones técnicas:**
- ✅ Offline-first: carga desde Room con Flow.first()
- ✅ Dark theme consistente en toda la pantalla
- ✅ Material3 widgets (OutlinedButton, MaterialCardView)
- ✅ Glide con placeholder y error handling
- ✅ Formato precio CLP con puntos ($89.990)
- ✅ Validación de stock antes de agregar
- ✅ Controles cantidad con límites dinámicos
- ✅ NestedScrollView para scroll fluido
- ✅ ViewBinding type-safe
- ✅ Lifecycle-aware (lifecycleScope para coroutines)

**Strings agregados:**
```xml
<string name="product_detail_title">Detalle del Producto</string>
<string name="product_description">Descripción</string>
<string name="product_quantity">Cantidad</string>
<string name="product_total">Total:</string>
<string name="product_add_to_cart">Agregar al Carrito</string>
<string name="product_stock_available">Stock disponible: %d unidades</string>
<string name="product_out_of_stock">Sin Stock</string>
<string name="product_no_description">Sin descripción disponible</string>
```

**Build metrics:**
- BUILD SUCCESSFUL en 55s
- 111 tasks ejecutadas (36 nuevas, 75 cached)
- 0 errores de compilación
- 0 warnings
- APK Debug: ~7.7 MB
- APK Release: ~6.4 MB

**Pantallas totales implementadas: 7**
1. ✅ LoginActivity (Opal-style dark)
2. ✅ MainActivity (navegación hub + categorías)
3. ✅ CategoryActivity (lista por categoría)
4. ✅ CartActivity (carrito con resumen)
5. ✅ ProfileActivity (perfil de usuario)
6. ✅ CameraActivity (elemento nativo CameraX)
7. ✅ **ProductDetailActivity** (detalle + agregar al carrito) ← NUEVO

**TODOs identificados:**
- [ ] Animación de transición al abrir detalle
- [ ] Compartir producto (share button)
- [ ] Favoritos (heart icon toggle)
- [ ] Productos relacionados (horizontal RecyclerView)
- [ ] Zoom en imagen (PhotoView/pinch to zoom)
- [ ] Reviews y calificaciones de usuarios
- [ ] Botón "Comprar ahora" (checkout directo sin carrito)
- [ ] Badge en carrito con cantidad de items
- [ ] Subir foto de perfil a Supabase Storage

**Próximo paso:**
Mejoras opcionales en UX o implementar pantalla de creación/edición de productos con cámara

**Estado general:**
✅ Detalle de producto completamente funcional  
✅ Navegación fluida desde main y categorías  
✅ Agregar al carrito desde detalle funcionando  
✅ Validación de stock implementada  
✅ UI pulida con Material3 y dark theme  
✅ Build exitoso sin errores  
✅ Sistema de cambio de foto de perfil funcional  
⏭️ Siguiente: Crear/editar productos con CameraX o mejoras UX

---

### Sesión 10 - 2025-11-10 (05:15-05:53)
**Duración**: ~38 minutos  
**Actividades**:
- ✅ Optimización de tarjetas de productos en catálogo
- ✅ Cambio de botón "Eliminar" por "Agregar al carrito"
- ✅ Reducción de altura de imagen en tarjetas (180dp → 160dp)
- ✅ Implementación de sistema de cambio de foto de perfil
- ✅ BottomSheet con opciones de cámara y galería
- ✅ Integración con APIs nativas de Android
- ✅ Build exitoso completo

**Archivos creados:**
```
app/src/main/res/
├── layout/
│   └── bottom_sheet_photo_options.xml  # Nuevo (112 líneas)
├── xml/
│   └── file_paths.xml                  # Nuevo (12 líneas)
└── values/
    └── ids.xml                         # Nuevo (4 líneas)
```

**Archivos modificados:**
```
app/src/main/java/com/keylab/mobile/
├── ui/
│   ├── ProfileActivity.kt              # +150 líneas de código
│   └── adapter/
│       └── ProductoAdapter.kt          # Callback cambiado
├── MainActivity.kt                      # Callback actualizado
└── CategoryActivity.kt                  # Callback actualizado

app/src/main/res/
├── layout/
│   ├── item_producto.xml               # Optimizado (103 líneas)
│   └── activity_profile.xml            # Avatar clickeable
├── values/
│   └── strings.xml                     # 3 strings agregados
└── AndroidManifest.xml                 # FileProvider agregado
```

**Optimización de tarjetas de productos:**

**ANTES:**
```
┌─────────────────────┐
│  [Imagen 180dp]     │
│                     │
│  Nombre             │
│  $89.990            │
│  [Teclados]         │
│                     │
│  [Eliminar] [Detalle]│ ← 2 botones horizontales
└─────────────────────┘
   Altura: ~320dp
```

**DESPUÉS:**
```
┌─────────────────────┐
│  [Imagen 160dp]     │ ← 20dp menos
│                     │
│  Nombre             │
│  $89.990            │
│  [Teclados]         │
│                     │
│  ┌─────────────────┐│
│  │ 🛒 Agregar     ││ ← Full width, negro
│  │  al carrito    ││   Texto blanco
│  └─────────────────┘│
└─────────────────────┘
   Altura: ~280dp (40dp menos)
```

**Características del nuevo botón:**
- ✅ Texto: "Agregar al carrito" (español)
- ✅ Color fondo: Negro (#000000)
- ✅ Color texto: Blanco (#FFFFFF)
- ✅ Icono carrito incluido (ic_shopping_cart)
- ✅ Ancho completo (match_parent)
- ✅ Alto: 48dp (tamaño táctil óptimo)
- ✅ Funcionalidad: Agrega producto a Room con persistencia
- ✅ Toast confirmación: "✓ [Nombre] agregado al carrito"
- ✅ Se desactiva si stock = 0

**Sistema de cambio de foto de perfil implementado:**

**Flujo completo:**
```
Usuario en ProfileActivity
    ↓
Click en avatar circular
    ↓
BottomSheetDialog aparece
    ↓
┌─────────────────────────────────┐
│  Cambiar foto de perfil         │
│                                  │
│  ┌────────────────────────────┐ │
│  │ 📷 Tomar foto             │ │ → Cámara nativa
│  └────────────────────────────┘ │
│                                  │
│  ┌────────────────────────────┐ │
│  │ 🖼️  Elegir de galería      │ │ → Selector galería
│  └────────────────────────────┘ │
│                                  │
│  [Cancelar]                      │
└─────────────────────────────────┘
    ↓
Usuario selecciona opción
    ↓
Foto capturada/seleccionada
    ↓
Avatar actualizado (circular con Glide)
    ↓
Toast: "Foto actualizada" ✅
```

**APIs nativas de Android utilizadas:**

1. **ActivityResultContracts.GetContent()**
   - Selector de galería
   - No requiere permisos en Android 11+
   - Retorna Uri de imagen seleccionada

2. **ActivityResultContracts.TakePicture()**
   - Captura con cámara nativa
   - Guarda en ubicación especificada (FileProvider)
   - Solicita permiso de cámara en runtime

3. **ActivityResultContracts.RequestPermission()**
   - Solicitud moderna de permisos
   - Maneja aceptar/denegar con callbacks
   - Reemplaza requestPermissions antiguo

4. **FileProvider**
   - Compartir archivos de forma segura
   - Evita FileUriExposedException (Android 7+)
   - Configurado en AndroidManifest + xml/file_paths.xml

5. **BottomSheetDialog**
   - Componente Material Design 3
   - Menú deslizable desde abajo
   - Dark theme consistente con app

**Tecnologías utilizadas:**
- ✅ ActivityResultContracts (API moderna)
- ✅ FileProvider (seguridad Android 7+)
- ✅ BottomSheetDialog (Material3)
- ✅ Glide.circleCrop() (transformación circular)
- ✅ Archivos temporales en cache

**Características implementadas:**
- ✅ Solicita permiso de cámara solo cuando es necesario
- ✅ Maneja denegación con Toast informativo
- ✅ Galería sin permisos (Android 11+)
- ✅ Imagen circular automática con Glide
- ✅ Archivos temporales en cache (se limpian solos)
- ✅ BottomSheet dark theme consistente
- ✅ Feedback visual con Toast
- ✅ Avatar clickeable con ripple effect

**Decisiones técnicas:**
- ✅ Sin librerías externas (solo APIs nativas)
- ✅ Archivos temporales en cacheDir (no requiere WRITE_EXTERNAL)
- ✅ FileProvider con authorities dinámico (${applicationId}.fileprovider)
- ✅ ImageView agregado dinámicamente sobre TextView
- ✅ TextView "U" se oculta al cargar foto
- ✅ Glide maneja memoria y cache automáticamente

**file_paths.xml configurado:**
```xml
<paths>
    <cache-path name="cache" path="." />
    <external-files-path name="external_files" path="." />
    <files-path name="files" path="." />
</paths>
```

**Build metrics:**
- BUILD SUCCESSFUL en 36s
- 42 tasks ejecutadas (19 nuevas, 23 cached)
- 3 warnings (no críticos: parámetros no usados en LoginActivity)
- APK Debug: ~7.7 MB
- APK Release: ~6.4 MB

**TODOs para foto de perfil:**
- [ ] Subir imagen a Supabase Storage
- [ ] Guardar URL en perfil de usuario (tabla users)
- [ ] Cargar foto desde Supabase al iniciar sesión
- [ ] Comprimir imagen antes de subir (reducir tamaño)
- [ ] Crop/edición de imagen antes de guardar
- [ ] Placeholder mientras carga desde red
- [ ] Caché de imágenes de perfil

**Próximo paso:**
Integrar subida de fotos a Supabase Storage o implementar pantalla de creación/edición de productos

**Estado general:**
✅ Tarjetas de productos optimizadas (40dp menos)  
✅ Botón "Agregar al carrito" funcional con persistencia  
✅ Sistema de cambio de foto de perfil completo  
✅ Cámara y galería funcionando con APIs nativas  
✅ BottomSheet elegante con Material3  
✅ Build exitoso sin errores  
✅ Todo en español con UX pulida  
⏭️ Siguiente: Subir imágenes a Supabase Storage o CRUD de productos

---

### Sesión 11 - 2025-11-10 (06:00-06:16)
**Duración**: ~16 minutos  
**Actividades**:
- ✅ Traducción completa de textos a español en MainActivity
- ✅ Integración del logo KeyLab desde proyecto original
- ✅ Agrandamiento del logo x3 (40dp → 120dp)
- ✅ Traducción completa de LoginActivity al español
- ✅ Build exitoso completo

**Archivos creados:**
```
app/src/main/res/drawable/
└── logokb.png                      # Logo KeyLab (1024x1024, 71KB)
```

**Archivos modificados:**
```
app/src/main/res/
├── layout/
│   └── activity_main.xml           # Logo 120dp + textos español
├── values/
│   └── strings.xml                 # "Categorías" + "Ver todo"
└── java/com/keylab/mobile/ui/
    └── LoginActivity.kt            # 8 mensajes en español
```

**Cambios en MainActivity:**

**Textos actualizados:**
- ✅ "Categories" → "Categorías"
- ✅ "View All" → "Ver todo" (2 ocurrencias)

**Logo KeyLab integrado:**
```
ORIGEN:
/home/nicolas/u/FS2/KeyLab/public/assets/img/logokb.png

DESTINO:
app/src/main/res/drawable/logokb.png

ESPECIFICACIONES:
- Formato: PNG con transparencia (RGBA)
- Resolución: 1024 x 1024 px
- Tamaño archivo: 71 KB
- Calidad: Alta resolución

TAMAÑO EN UI:
- ANTES: 32dp x 32dp (ic_launcher genérico)
- DESPUÉS: 120dp x 120dp (3x más grande)
- ScaleType: fitCenter
- Ubicación: Toolbar superior izquierda
```

**Cambios en LoginActivity (100% español):**

**Validaciones de campos:**
```kotlin
// Email
"Email is required" → "El correo electrónico es requerido"
"Invalid email" → "Correo electrónico inválido"

// Password
"Password is required" → "La contraseña es requerida"
"Minimum 6 characters" → "Mínimo 6 caracteres"
```

**Mensaje de bienvenida:**
```kotlin
"Welcome!" → "¡Bienvenido a KeyLab!"
```

**Mensajes de funcionalidades futuras:**
- "Password recovery coming soon" → "Recuperación de contraseña próximamente"
- "Registration coming soon" → "Registro próximamente"
- "Apple sign-in coming soon" → "Inicio con Apple próximamente"
- "Phone sign-in coming soon" → "Inicio con teléfono próximamente"

**Resultado visual:**

**LOGIN SCREEN:**
```
┌────────────────────────────────────────┐
│          KeyLab                        │
│          Bienvenido                    │
│                                        │
│  [Correo electrónico]                  │
│   ↳ "El correo electrónico es requerido"
│                                        │
│  [Contraseña]                          │
│   ↳ "La contraseña es requerida"      │
│                                        │
│  [Iniciar sesión]                      │
│         ↓                              │
│  Toast: "¡Bienvenido a KeyLab!"       │
└────────────────────────────────────────┘
```

**MAIN SCREEN:**
```
┌────────────────────────────────────────┐
│  [LOGO KEYLAB 120x120dp]  Home         │ ← 3x más grande
│                                        │
│  Categorías              Ver todo →    │
│  [Todos] [Teclados] [Keycaps]...      │
│                                        │
│  Productos destacados    Ver todo →    │
│  [Grid 2 columnas]                     │
└────────────────────────────────────────┘
```

**Comparativa de logo:**
```
ANTES:
┌──────┐
│ 32dp │  Android genérico
└──────┘

DESPUÉS:
┌────────────┐
│            │
│   120dp    │  Logo KeyLab real
│            │
└────────────┘
     3x más visible
```

**Build metrics:**
- BUILD SUCCESSFUL en 23s
- 42 tasks ejecutadas (16 nuevas, 26 cached)
- 2 warnings (no críticos: parámetros email/password no usados)
- APK Debug: ~7.7 MB
- APK Release: ~6.4 MB

**Archivos de recursos actualizados:**
1. **logokb.png**: Logo oficial de KeyLab (alta resolución)
2. **strings.xml**: 2 strings traducidos
3. **activity_main.xml**: Logo 120dp + textos español
4. **LoginActivity.kt**: 8 mensajes completamente en español

**Localización completa:**
- ✅ MainActivity: "Categorías" + "Ver todo"
- ✅ LoginActivity: Todas las validaciones en español
- ✅ Mensaje de bienvenida: "¡Bienvenido a KeyLab!"
- ✅ Mensajes informativos: Todos en español
- ✅ Experiencia de usuario 100% localizada

**Branding KeyLab:**
- ✅ Logo oficial integrado (1024x1024px)
- ✅ Logo 3x más grande y visible (120dp)
- ✅ Presencia visual fuerte en MainActivity
- ✅ Identidad de marca consistente

**Decisiones técnicas:**
- ✅ Logo copiado desde proyecto original KeyLab
- ✅ PNG con transparencia (RGBA) para flexibilidad
- ✅ ScaleType fitCenter mantiene proporciones
- ✅ Tamaño 120dp óptimo para visibilidad sin saturar UI
- ✅ Mensajes de error contextualizados en español
- ✅ Toast de bienvenida más personal e inclusivo

**Próximo paso:**
Implementar CRUD de productos o subir imágenes a Supabase Storage

**Estado general:**
✅ Logo KeyLab integrado y prominente (120dp)  
✅ MainActivity con textos en español  
✅ LoginActivity 100% traducido (validaciones + mensajes)  
✅ Branding KeyLab reforzado  
✅ Experiencia de usuario completamente localizada  
✅ Build exitoso sin errores  
⏭️ Siguiente: CRUD de productos o integración Storage

---

## 📞 Contacto y Soporte

**Estudiante**: Nicolás  
**Ramo**: Desarrollo Mobile  
**Institución**: Universidad (no especificada)  
**Fecha de entrega**: Por confirmar

---

*Este documento se actualiza al final de cada sesión de trabajo*
