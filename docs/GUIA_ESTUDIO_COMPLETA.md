# 📘 GUÍA DE ESTUDIO TÉCNICA - KEYLAB MOBILE
**Defensa de Proyecto Final Android Nativo**

---

## 1. ARQUITECTURA Y DISEÑO

### 🏛️ Patrón: MVVM (Model-View-ViewModel)
**¿Por qué lo usamos?**
Para separar la lógica de negocio de la interfaz gráfica. Esto hace el código más limpio, testearle y mantenible.

*   **View (Activity/XML):** Solo muestra datos y captura clicks. No toma decisiones. Observa al ViewModel.
*   **ViewModel:** Mantiene el estado de la UI (loading, listas, errores). Sobrevive a cambios de configuración (rotar pantalla). No conoce a la View (no tiene referencias a `TextView` o `Context`).
*   **Model (Repository):** La fuente única de verdad. Decide si busca datos en la base de datos local (Room) o en la nube (Supabase).

### 🔄 Repository Pattern & Offline-First
**Estrategia:** "Local First" (Primero Local).
1.  La app **SIEMPRE** muestra lo que hay en la base de datos local (`Room`).
2.  En segundo plano, intenta conectarse a internet (`Retrofit/Supabase`).
3.  Si descarga datos nuevos, actualiza `Room`.
4.  Como la UI observa `Room` (usando `Flow`), se actualiza automáticamente cuando `Room` cambia.
**Beneficio:** La app funciona rapidísimo y no se rompe si se va el internet.

---

## 2. TECNOLOGÍAS CLAVE (El "Stack")

| Tecnología | Uso en el Proyecto | ¿Por qué esta y no otra? |
| :--- | :--- | :--- |
| **Kotlin** | Lenguaje principal | Moderno, seguro (evita NullPointerExceptions), conciso. |
| **Room** | Base de datos local | Capa sobre SQLite. Verifica SQL en tiempo de compilación. Fácil de usar con objetos. |
| **Retrofit** | Cliente HTTP (Red) | Estándar de la industria. Convierte JSON a objetos Kotlin automáticamente (con Gson). |
| **Coroutines** | Asincronía | Permite hacer tareas pesadas (red, BD) sin congelar la pantalla principal. |
| **Flow** | Flujo de datos | Permite recibir actualizaciones continuas de la BD (reactividad) en tiempo real. |
| **Glide** | Carga de Imágenes | Maneja caché, redimensionamiento y errores de imágenes eficientemente. |
| **CameraX** | Cámara | API moderna de Google, más fácil que Camera2, compatible con muchos dispositivos. |

---

## 3. FLUJOS DE DATOS CRÍTICOS (Preguntas Trampa)

### 🛒 ¿Cómo funciona el Carrito?
1.  **Persistencia:** No está en memoria RAM, está en una tabla `carrito_items` en `Room`. Si cierras la app, no se pierde.
2.  **Lógica:** `CarritoViewModel` expone un `Flow` que suma totales y costos de envío en tiempo real.
3.  **Sincronización:** Al comprar, los items se mueven a una tabla `orden_items` y el carrito se vacía.

### 🔐 ¿Cómo funciona el Login/Admin?
1.  **Validación:** Se verifica email/pass contra la tabla `usuarios` en Room.
2.  **Rol Admin:** El código verifica si el email termina en `@keylab.com`.
3.  **Sesión:** Se guarda un flag `is_admin` y `user_id` en `SharedPreferences`.
4.  **Navegación:** 
    *   Admin → `AdminDashboardActivity` (CRUD Productos).
    *   User → `MainActivity` (Tienda).

### ☁️ ¿Cómo se conecta con el Backend?
*   Backend: **Supabase** (PostgreSQL).
*   Comunicación: **REST API**.
*   Truco: Para crear productos sin enviar ID (error 400), usamos un `Map<String, Any>` en lugar del objeto `Producto`, dejando que la base de datos genere el ID automáticamente.

---

## 4. PREGUNTAS POTENCIALES DEL PROFESOR

**P: ¿Qué pasa si el usuario no tiene internet?**
*R: La app sigue funcionando completamente. Puede ver productos (cacheados), agregar al carrito y crear órdenes locales. Al recuperar conexión, idealmente se sincronizaría (aunque nuestra implementación actual prioriza lectura offline).*

**P: ¿Por qué usaste Room y no SharedPreferences para los productos?**
*R: SharedPreferences es solo para datos pequeños (clave-valor). Room es una base de datos relacional completa que permite búsquedas complejas, ordenamiento y maneja grandes volúmenes de datos estructurados.*

**P: Explícame el ciclo de vida de tu ViewModel.**
*R: Se crea cuando la Activity inicia y **muere solo cuando la Activity se cierra definitivamente (finish)**. Si rotas el teléfono, la Activity se destruye y recrea, pero el ViewModel sigue vivo conservando los datos.*

**P: ¿Cómo manejas las imágenes?**
*R: Las URLs se guardan en la base de datos. Usamos **Glide** para descargarlas, mostrarlas y cachearlas en disco para no gastar datos móviles innecesariamente.*

---

## 5. INNOVACIONES (Tus puntos fuertes)

1.  **Panel de Administración Móvil:** No necesitas una web para gestionar la tienda.
2.  **Elemento Nativo Real:** Uso de **CameraX** integrado en el flujo (no solo un intent externo).
3.  **Diseño Moderno:** Material Design 3 con soporte Dark Mode (estilo "Opal").
4.  **Escalabilidad:** La arquitectura permite cambiar Supabase por Firebase o una API propia sin reescribir toda la app (solo cambiando el Repository).

---

### ⚡ TIPS PARA LA DEMO
*   Ten abierta la pestaña de **App Inspection > Database Inspector** en Android Studio por si te piden ver la base de datos en vivo.
*   Si algo falla, di: *"Es una implementación prototipo para demostrar la arquitectura Offline-First"*.
*   Destaca mucho la **velocidad** de la app (gracias a la base de datos local).

---

## 6. CÓDIGO CLAVE EXPLICADO

### 📝 PreferencesManager.kt (Gestión de Sesión)

**¿Qué es?**
Es una clase que usa `SharedPreferences` para guardar datos pequeños (clave-valor) que persisten al cerrar la app.

**La Analogía del Hotel:**
*   **Room (Base de Datos):** Es el archivo gigante con carpetas. Guardas productos, historial, detalles complejos. Es robusto pero más pesado.
*   **PreferencesManager:** Es un **Post-it** en el monitor del recepcionista. Solo dice: *"Usuario actual: Juan (ID 5)"*. Es inmediato.

**Puntos Clave para Defender:**
1.  **`Context.MODE_PRIVATE`:** Seguridad. Significa que el archivo de preferencias solo puede ser leído por TU aplicación. Ninguna otra app del teléfono puede espiarlo.
2.  **`apply()` vs `commit()`:**
    *   Usamos `.apply()` porque guarda los cambios en **segundo plano (asíncrono)**.
    *   `.commit()` bloquea el hilo principal hasta que termina de escribir (podría congelar la UI). ¡Nunca uses commit en el hilo principal!
3.  **¿Por qué guardar solo el ID?**
    *   Por eficiencia y consistencia. Si guardamos todo el objeto Usuario en preferencias y luego cambiamos su nombre en la base de datos, tendríamos datos duplicados y desactualizados. Guardamos el ID (referencia) y buscamos los datos reales en la base de datos cuando los necesitamos.

### 🔍 Funciones de Lectura (isLoggedIn / esAdmin) y Lógica de Negocio

**El Código:**
```kotlin
fun isLoggedIn() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
fun esAdmin() = sharedPreferences.getBoolean(KEY_IS_ADMIN, false)
```

**¿Qué hacen?**
Son preguntas directas al archivo de preferencias.
*   El parámetro `false` es el **valor por defecto**: "Si no encuentras la nota, asume que NO está logueado y NO es admin". Esto es vital por seguridad.

**¿Dónde está la "Inteligencia"? (Relación con LoginActivity)**
`PreferencesManager` es "tonto", solo guarda lo que le dicen. La inteligencia está en `LoginActivity`.

1.  **El Cerebro (`LoginActivity`):**
    *   Al hacer login, verifica: `val isAdmin = email.endsWith("@keylab.com")`.
    *   Aquí es donde se define la regla de negocio.
2.  **La Orden:**
    *   Llama a `preferencesManager.guardarSesion(id, isAdmin)`.
3.  **La Libreta (`PreferencesManager`):**
    *   Solo escribe `true` o `false` en el archivo.

**Beneficio:** Esto se llama **Encapsulamiento**. Si mañana la regla de ser admin cambia (ej: "solo correos que empiecen con 'admin'"), solo cambias el código en `LoginActivity`, y el resto de la app ni se entera.

---

## 7. ENTENDIENDO LOS ADAPTERS (RecyclerView)

### 🍔 La Analogía del Buffet
*   **RecyclerView:** Es la barra del buffet. Solo un contenedor vacío.
*   **Datos (List):** La comida en la cocina (miles de platos).
*   **Adapter:** El **Camarero**. Su trabajo es conectar la cocina con la barra.
    *   **Reciclaje:** Cuando un plato sale de la vista del usuario (scroll), el camarero NO lo tira. Lo limpia, le pone la comida del siguiente ítem y lo vuelve a usar abajo. Por eso la app no se pone lenta aunque tengas 10.000 productos.

### 🔧 Las 3 Partes Técnicas
1.  **`onCreateViewHolder`:** Crea el "plato vacío" (infla el XML). Se ejecuta pocas veces (solo las necesarias para llenar la pantalla).
2.  **`onBindViewHolder`:** "Sirve la comida". Toma el plato vacío y rellena los datos (Nombre, Precio, Imagen) según la posición en la lista. Se ejecuta constantemente al hacer scroll.
3.  **`ViewHolder`:** Es la clase que mantiene las referencias a los elementos visuales (`TextView`, `ImageView`) para no tener que buscarlos (`findViewById`) cada vez.

### 🚀 Conceptos Avanzados (Para impresionar)
*   **DiffUtil:** Es un algoritmo que usas en tu Adapter (`ListAdapter`). En vez de recargar toda la lista cuando cambia un dato (lento), calcula la diferencia exacta y actualiza solo ese ítem con una animación suave.
*   **Lambdas para Clicks:** El Adapter no decide qué hacer al dar click. Recibe una función "lambda" (`(Producto) -> Unit`) desde la Activity. Cuando tocas un producto, el Adapter ejecuta esa función. Así, la lógica de navegación se queda en la Activity, no en el Adapter (Separación de Responsabilidades).