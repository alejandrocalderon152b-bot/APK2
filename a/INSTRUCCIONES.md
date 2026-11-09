# Kegel en el Galaxy Watch 7

Este proyecto es la app Kegel adaptada a la pantalla redonda del reloj. Tiene cinco programas, tres niveles, vibración en la muñeca para saber cuándo contraer y soltar, y un resumen de los últimos 7 días.

Los programas dan pistas con vibración, así puedes hacerlos sin mirar el reloj:

- Contraer: una vibración larga.
- Soltar: una vibración corta.
- Rendimiento, bloque de tres niveles: una, dos y tres vibraciones cortas.
- Descanso entre series: dos vibraciones.
- Sesión terminada: tres vibraciones.

La app funciona sin conexión y no pide ningún permiso salvo la vibración.

El proyecto no está compilado todavía. Primero hay que generar la APK (parte 1) y luego instalarla en el reloj (parte 2).

---

## Parte 1. Conseguir la APK

### Opción A. Sin instalar nada, con GitHub (recomendada)

1. Crea una cuenta gratuita en github.com y un repositorio nuevo, por ejemplo `kegel-watch`. Puede ser privado.
2. Descomprime este ZIP en tu ordenador.
3. En el repositorio pulsa **Add file > Upload files** y arrastra **todo el contenido** de la carpeta descomprimida. Incluye la carpeta oculta `.github`. Si no la ves, activa "mostrar archivos ocultos" en tu explorador de archivos.
4. Pulsa **Commit changes**.
5. Entra en la pestaña **Actions**. Si no se ha iniciado sola, abre **Compilar APK** y pulsa **Run workflow**.
6. Espera entre 3 y 8 minutos hasta que aparezca un check verde.
7. Abre esa ejecución. Abajo, en **Artifacts**, descarga **kegel-watch-apk**. Es un ZIP que contiene `kegel-watch.apk`.

### Opción B. Con Android Studio

1. Instala Android Studio (developer.android.com/studio).
2. Elige **File > Open** y selecciona la carpeta descomprimida. Espera a que termine la sincronización de Gradle.
3. Elige **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. La APK queda en `app/build/outputs/apk/debug/app-debug.apk`.

---

## Parte 2. Instalar la APK en el Galaxy Watch 7

Se instala con ADB por Wi-Fi. El Galaxy Watch 7 no tiene conexión USB.

### En el reloj

1. Abre **Ajustes > Acerca del reloj > Información del software**.
2. Toca cinco veces seguidas **Versión del software**. Saldrá "Modo desarrollador activado".
3. Vuelve a **Ajustes**. Al final aparecerá **Opciones para desarrolladores**.
4. Activa **Depuración ADB** y **Depuración inalámbrica** (los nombres pueden cambiar un poco según la versión).
5. Comprueba que el reloj está conectado por Wi-Fi a la misma red que tu ordenador (**Ajustes > Conexiones > Wi-Fi**). No vale solo la conexión Bluetooth con el móvil.
6. Entra en **Depuración inalámbrica** y pulsa **Vincular dispositivo nuevo**. Verás un código de 6 dígitos y una dirección con puerto, por ejemplo `192.168.1.40:37123`. No cierres esta pantalla.

### En el ordenador

Necesitas las herramientas de plataforma de Android (platform-tools), que incluyen `adb`. Se descargan de developer.android.com/tools/releases/platform-tools.

1. Vincula el reloj con la dirección y el código que muestra la pantalla de vinculación:

   ```
   adb pair 192.168.1.40:37123
   ```

   Cuando te lo pida, escribe el código de 6 dígitos.
2. Vuelve a la pantalla principal de **Depuración inalámbrica** del reloj. Ahí aparece otra dirección con **otro puerto**, que es la de conexión. Conéctate:

   ```
   adb connect 192.168.1.40:41555
   ```
3. Comprueba que aparece como `device`:

   ```
   adb devices
   ```
4. Instala la APK (cambia la ruta por la de tu archivo):

   ```
   adb install kegel-watch.apk
   ```

   Debe terminar con `Success`.
5. En el reloj, abre la lista de aplicaciones y busca **Kegel**.
6. Cuando termines, desactiva **Depuración inalámbrica** y **Depuración ADB** para ahorrar batería.

### Alternativa sin ordenador

Hay aplicaciones para Android, como **Wear Installer 2**, que instalan APK en un reloj usando la depuración inalámbrica desde el móvil. Copia la `kegel-watch.apk` al móvil y sigue los pasos de la aplicación. Esas aplicaciones no son mías y pueden cambiar; comprueba que siguen disponibles y tienen buenas valoraciones antes de usarlas.

---

## Parte 3. Actualizar la app más adelante

La APK usa siempre la misma firma, así que una versión nueva se instala encima y conserva tu historial:

```
adb install -r kegel-watch.apk
```

---

## Problemas frecuentes

- **`adb connect` no conecta.** El reloj y el ordenador deben estar en la misma red Wi-Fi, sin VPN, y la red no debe aislar los dispositivos entre sí (algunas redes de invitados lo hacen). El puerto de conexión cambia cada vez que activas la depuración inalámbrica, así que vuelve a mirar el de la pantalla.
- **`adb pair` dice que el código es incorrecto.** El código caduca. Pulsa de nuevo **Vincular dispositivo nuevo** y usa el código y el puerto nuevos.
- **No aparece la app.** Busca "Kegel" al final de la lista de apps. Si no está, repite `adb install` y revisa el mensaje de error.
- **El reloj se desconecta de la Wi-Fi.** Mantén la pantalla encendida mientras instalas. Algunos relojes cortan el Wi-Fi para ahorrar batería.
- **La pantalla se apaga durante la sesión.** La app la mantiene encendida mientras haces el ejercicio. Si aun así se apaga, alarga el tiempo de pantalla en **Ajustes > Pantalla**.
- **La vibración es floja.** Sube la intensidad en **Ajustes > Sonidos y vibración**.

---

## Contenido del proyecto

- `app/src/main/assets/index.html`: la app (interfaz y lógica de los programas).
- `app/src/main/java/app/kegel/watch/MainActivity.java`: abre la página y conecta la vibración y la pantalla encendida con el reloj.
- `app/src/main/AndroidManifest.xml`: declara la app como aplicación de reloj Wear OS.
- `.github/workflows/build-apk.yml`: compila la APK en GitHub.
- `app/debug.keystore`: firma fija de la APK. Sirve para instalar en tu reloj. Si publicas la app en una tienda, crea tu propia firma.

Esta app es una guía de entrenamiento y no sustituye el consejo médico. Si notas dolor o los síntomas persisten, consulta con un médico.
