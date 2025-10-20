# KeyLabMobile — Preparación F1 (Proyecto Android)

## Pendientes técnicos tras la estructura base
- Agregar wrapper de Gradle (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`) para permitir builds reproducibles.
- Configurar `settings.gradle.kts` con repositorios (`dependencyResolutionManagement`) una vez se confirme la versión de Gradle/Android Studio.
- Crear módulo `core` o `data` si se decide separar capas desde el inicio.
- Incorporar librerías planificadas: Navigation Component, Hilt/Koin, Room, Retrofit, DataStore y módulos de test (MockK, CoroutineTest).
- Definir paquetes por feature (`auth`, `catalog`, `cart`, `profile`) y mover `MainActivity` a `ui.main` cuando existan fragments.
- Configurar `NavHostFragment` y layout base (`activity_main.xml`) para soportar navegación con `FragmentContainerView` y BottomNavigationView.
- Ajustar `themes.xml` y paleta en `colors.xml` con los valores reales de KeyLab (cuando se cierre branding mobile).
- Crear recursos `mipmap` personalizados usando el isotipo `logokb` adaptado (vector/drawable).

## Próximos pasos de documentación
- Actualizar `docs/F0_Discovery.md` con wireframes cuando estén listos.
- Añadir checklist de dependencias y convenciones de nombres en `docs/`.
- Registrar decisiones de arquitectura (MVVM, Clean Architecture, modularización) en un ADR breve.
