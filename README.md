# Aplicación de adopción de mascotas

Esta aplicación Android busca acercar a los refugios y a las personas que desean adoptar. El refugio puede publicar animales y atender solicitudes, mientras que el adoptante puede explorar el catálogo, guardar favoritos y conversar sobre una mascota que le interesa. La construimos en Java como proyecto académico y combinamos datos locales con servicios de Firebase para compartir la información entre usuarios.

El repositorio original está en [GitHub](https://github.com/AmnerMarin/AppAdopcionMascotas).

## Recorrido dentro de la aplicación

Al registrarse, la persona elige si entra como refugio o adoptante. Firebase Authentication gestiona el acceso por correo y contraseña; después, el repositorio de usuarios guarda el perfil en Cloud Firestore y conserva una copia local en SQLite para que las pantallas trabajen con sus identificadores y datos cercanos.

Cuando un refugio publica una mascota, la aplicación registra primero su información en SQLite, sube la fotografía a Firebase Storage y guarda en Firestore los datos compartidos junto con la URL de la imagen. El refugio también puede editar o eliminar sus publicaciones; por otro lado, el adoptante puede consultar los detalles, aplicar filtros y añadir mascotas a favoritos.

Si el adoptante solicita una adopción, la solicitud se registra con estado pendiente. El refugio puede aprobarla o rechazarla y, cuando la aprueba, la mascota pasa a estado adoptado. Además, el chat permite intercambiar mensajes relacionados con una mascota; Firestore comunica los nuevos mensajes y SQLite mantiene la conversación local que muestra la interfaz.

La aplicación también integra Google Maps, Places y los servicios de ubicación para elegir o mostrar un lugar. En la lista de refugios externos, Retrofit consulta un archivo JSON alojado en GitHub Gist, de modo que esa sección depende de que el recurso público siga disponible.

## Tecnologías y responsabilidades

| Tecnología | Para qué se utiliza |
| --- | --- |
| Java y Android Views con XML | Implementan las actividades, los fragmentos, los formularios y las listas. |
| Firebase Authentication | Registra y autentica cuentas mediante correo y contraseña. |
| Cloud Firestore | Comparte perfiles, mascotas, solicitudes, favoritos y mensajes. |
| Firebase Storage | Guarda las fotografías publicadas por los refugios. |
| SQLite | Mantiene tablas locales de usuarios, refugios, mascotas, solicitudes, favoritos y chat. |
| Google Maps, Places y servicios de ubicación | Muestran el mapa, permiten buscar lugares y obtienen la ubicación con permiso del usuario. |
| Retrofit y Gson | Descargan y convierten la lista externa de refugios desde JSON. |

En `app/src/main/java/sistemas/unc/edu/appadopcionmascotas/`, las clases `Actividad*.java` abren los recorridos principales y `fragments/` reúne las secciones de cada rol. Los adaptadores de `UI/` convierten mascotas, solicitudes y conversaciones en elementos de lista; `Model/` define los datos que usan esas pantallas; `Data/` contiene la base SQLite y su DAO; `Firebase/` coordina las operaciones remotas y algunas sincronizaciones; por último, `API/` agrupa la consulta HTTP de refugios externos. Los diseños y recursos visuales están en `app/src/main/res/`.

## Preparar el entorno

Abre la raíz del proyecto en Android Studio y deja que Gradle sincronice las dependencias. La configuración actual compila con Android SDK 36 y requiere como mínimo Android 13, correspondiente a API 33; por eso necesitas un emulador o dispositivo que cumpla ese nivel.

Para usar un proyecto de Firebase propio, registra la aplicación Android con el identificador `sistemas.unc.edu.appadopcionmascotas` y coloca su archivo `google-services.json` en `app/`. Habilita el inicio de sesión por correo y contraseña, Cloud Firestore y Firebase Storage, y configura para esas bases las reglas de acceso que correspondan a tus usuarios. Los mapas y la búsqueda de lugares requieren una clave válida para Maps SDK y Places API; en la implementación actual hay referencias a esa clave en `AndroidManifest.xml` y `ActividadMapas.java`, por lo que debes revisar ambas al cambiar de proyecto de Google Cloud.

Después de configurar los servicios, ejecuta la variante `app` desde Android Studio. Puedes probar primero el registro de un refugio y la publicación de una mascota; luego registra un adoptante para comprobar el catálogo, los favoritos, la solicitud y el chat. Si quieres compilar desde la terminal, el repositorio incluye el wrapper de Gradle:

```bash
./gradlew assembleDebug
```

En Windows, el mismo comando se ejecuta con `gradlew.bat`.

## Alcance actual

La aplicación usa SQLite junto con Firebase y sincroniza varias entidades, aunque esas escrituras no forman una transacción única entre el teléfono y la nube; por ello, un fallo a mitad del proceso puede requerir reconciliar ambos lados. Es un proyecto académico y, antes de utilizarlo con datos reales, habría que eliminar el almacenamiento local de contraseñas, revisar las reglas de Firestore y Storage y restringir las claves de Google Maps y Places.
