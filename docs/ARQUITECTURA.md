# Arquitectura de Medikit AI

```text
                 ANDROID (Java + Kotlin)
 +--------------------------------------------------+
 | MainActivity.java                                |
 |  Camera intent (foto completa) / Photo Picker    |
 |        |                      |                  |
 |        +----------+-----------+                  |
 |                   v                              |
 |          Decodificación / orientación            |
 |                   v                              |
 |            Detector.kt (TFLite)                  |
 |    RGB, 224x224, normalización x/255             |
 |                   v                              |
 |         8 salidas Softmax (0..1)                 |
 |                   v                              |
 |       Etiqueta + puntuación del modelo           |
 |                   v                              |
 |            Recomendaciones.java                  |
 |                   v                              |
 |         Vista de resultados y advertencia        |
 +--------------------------------------------------+
```

## Fuente del modelo

- Recuperado **sin modificaciones** del archivo `assets/modelo_lesiones.tflite` en `MEDIKIT.apk`.
- Identificador FlatBuffer del modelo: `TFL3`, versión de esquema: `3`.
- Entrada: `FLOAT32 [1,224,224,3]`.
- Salida: `FLOAT32 [1,8]`.
- Última operación de la red: `SOFTMAX`.
- Tamaño binario: **9,524,368 bytes**.
- Orden de etiquetas: alergias, cortaduras, esguinces, fracturas, hematomas, no_lesiones, quemaduras, raspaduras.
- La puntuación Softmax **no es una certeza diagnóstica** ni equivale a exactitud validada.

## Procedencia y límites

Los tres archivos de código compartidos en octubre de 2026 corresponden a una versión
previa e incompleta. En particular, `Detector.kt` tenía una llamada incompatible
`Interpreter(ByteArray)` y `MainActivity.java` todavía esperaba `String`.
Este repositorio presenta una reconstrucción funcional de referencia, no los fuentes
exactos compilados dentro del APK ni la arquitectura original del entrenamiento.

La captura usa una aplicación de cámara del sistema mediante
`ActivityResultContracts.TakePicture`; **no usa CameraX**.
Los datos se procesan en el dispositivo y no se envían a servidores.
