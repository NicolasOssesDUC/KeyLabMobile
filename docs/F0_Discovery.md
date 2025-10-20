# KeyLabMobile — Fase F0 (Descubrimiento)

## Objetivo y alcance
- Migrar la experiencia web de KeyLab (`FS2/Proyecto-FS2`) a una app Android nativa construida con Kotlin y vistas XML.
- Cubrir las funcionalidades visibles para usuarios finales (navegación catálogo, carrito, contacto) y para la administración básica (inicio de sesión y registro).
- Documentar dependencias actuales, flujos y activos visuales reutilizables antes de diseñar la arquitectura mobile.

## Inventario del proyecto web existente
- **Estructura**: sitio estático con múltiples páginas HTML (`index`, `productos`, `carrito`, `login`, `registro`, `blogs`, `contacto`, `nosotros`, `ubicacion`, etc.).
- **Frameworks**: Bootstrap 5 vía CDN, JavaScript vanilla y hojas de estilo personalizadas (`assets/css/main.css`, `assets/css/admin.css`).
- **Recursos**: imágenes bajo `assets/img`, catálogo simulado en `assets/data/productos.js`, iconografía SVG embebida.
- **Lógica cliente**:
  - Validaciones de login/registro/contacto en `assets/js/validaciones.js`.
  - Carrito enteramente en `assets/js/carrito.js`, persistencia por `localStorage`/`sessionStorage`.
  - No hay backend ni API; se crean usuarios y catálogo de prueba en el navegador.
- **Dependencias externas**: Google Fonts (`Jersey 10`, `Satoshi`), Bootstrap JS.

## Flujos funcionales a migrar a mobile
| Web actual | Descripción | Requerimiento móvil | Observaciones |
|------------|-------------|---------------------|---------------|
| `index.html` (landing) | Banner hero, carrusel, CTA hacia productos | Pantalla `Home` con hero/resumen de categorías y atajos | Animaciones/tipografía deben adaptarse, fondos estáticos. |
| `productos.html`, `teclados.html`, `keycaps.html`, `switches.html`, `cases.html` | Listas filtradas manualmente | `CatalogFragment` con RecyclerView + filtros por categoría/subcategoría | Fuente de datos real: API/Room. |
| `detalle-producto.html` | Vista individual con imagen, descripción, botón agregar | `ProductDetailFragment` con Add-to-cart dinámico | Debe consumir inventario vivo. |
| `carrito.html` | Gestión de carrito, totales, vaciar/finalizar | `CartFragment` con totales, checkout dummy | Persistir en Room/DataStore mientras llega backend. |
| `login.html` | Login con validaciones de formato | `AuthActivity` (LoginFragment) con estados de carga y errores | Reutilizar reglas de `validaciones.js` y soportar futuras APIs OAuth/JWT. |
| `registro.html` | Alta básica de usuarios, duplicada en localStorage | `RegisterFragment` con campos RUN, correo, password, confirmación | Debe integrarse a API real; considerar máscaras RUN y feedback en tiempo real. |
| `pass-recov.html` | Formulario para recuperar password (placeholder) | `PasswordRecoveryFragment` opcional | Definir más adelante según backend. |
| `contacto.html` | Formulario contacto + validaciones simples | `ContactFragment` con envío a endpoint o email | Puede iniciar como pantalla informativa. |
| `blogs.html` y entradas | Noticias estáticas | `BlogListFragment` opcional o WebView | Prioridad baja para MVP mobile. |
| `admin/` | Panel prototipo sin backend | Fuera de alcance inicial | Replantear para versión mobile/desktop más adelante. |

## Modelos de datos relevantes
Extraídos de `assets/data/productos.js` y formularios actuales:
- **Producto**: `id`, `nombre`, `precio`, `categoria`, `subcategoria`, `imagen`, `stock`, `descripcion`.
- **Usuario** (mock): `run`, `nombre`, `apellidos`, `email`, `password`, `rol`.
- **CarritoItem** (derivado): atributos de `Producto` + `cantidad`.
- **Contacto**: `nombre`, `correo`, `mensaje` (mínimo 10 caracteres).

## Reglas de negocio detectadas
- Correo válido sólo si dominio ∈ {`duoc.cl`, `profesor.duoc.cl`, `gmail.com`, `keylab.cl`} y ≤100 caracteres.
- Password longitud 4–10 caracteres, confirmación obligatoria.
- RUN formato `12345678-9` o `k` (`validaciones.js`).
- Carrito almacena catálogo en `localStorage` bajo clave `productos`; totales calculados en cliente.
- Conteo carrito mostrado en navbar (`#contador`).

## Identidad visual a trasladar
- **Tipografías**: `Jersey 10` (Google Fonts) predominante; fuente secundaria `Satoshi` (importada local).
- **Colores destacados** (según `main.css`):
  - `#FFFFFF` fondo general y tarjetas.
  - Gradientes y sombras suaves `rgba(0,0,0,0.1)` en navbar.
  - Botones con clases Bootstrap (`btn-primary`, `btn-success`, etc.), por lo que el tema azul/verde estándar se usa.
- **Imagotipo**: `assets/img/logokb.png` y favicon `Logo2.ico`.
- **Estilo**: tarjetas con bordes redondeados (15px), uso intensivo de imágenes hero en full-width.

## Supuestos y vacíos detectados
- No existen endpoints reales; la app mobile necesitará API o data layer propia.
- No hay manejo de sesiones seguras, recuperación de contraseña ni verificación de correos.
- Imágenes viven en `assets/img` sin CDN; en mobile deben empaquetarse localmente o servirse desde backend/storage.
- Flujo de checkout es sólo un alert; se requiere definir proceso real o placeholder UX.

## Riesgos para la migración
- **Persistencia**: replicar localStorage → DataStore/Room sólo resuelve demo; para producción se requiere backend (JWT, catálogo en BD).
- **Consistencia UI**: el diseño web está pensado para pantallas grandes; hay que reinterpretar layout para mobile (cards, bottom nav, toolbar).
- **Accesibilidad**: contraste y tamaños de fuente deben revisarse (la fuente `Jersey 10` puede perder legibilidad en móviles pequeños).
- **Integración IA**: aún no hay artefactos, pero debe reservarse espacio en arquitectura para futuros módulos de clasificación/generación.

## Entregables F0
- Este documento (`docs/F0_Discovery.md`).
- Capturas de pantallas clave (pendiente).
- Priorización de flujos listos para prototipeo mobile (ver tabla de flujos).

## Próximos pasos sugeridos (F1)
1. Preparar wireframes low-fi por flujo (Home, Catálogo, Detalle, Carrito, Login, Registro, Perfil).
2. Definir arquitectura técnica: módulos Kotlin, patrón MVVM, Navigation Component, manejo de estado.
3. Configurar proyecto Android (`KeyLabMobile/app`) con dependencias base: Kotlin DSL, Material Components, ViewBinding, Hilt/Koin y Retrofit/Room placeholders.
4. Diseñar esquema de datos y contratos API iniciales (login, list products, cart operations) para alinear con futura BDD/Backend.
