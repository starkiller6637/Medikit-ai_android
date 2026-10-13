# Prototipo anterior en Python

En los archivos históricos compartidos en la conversación había programas para:
`DetectarHerida.py`, `MenuPrincipal.py`, `ReconocimientoVoz.py`, `voz.py`,
`MedirmiTemperatura.py`, `CalcularBPM.py`, `RellenarCompartimientos.py`.

El prototipo usaba `tensorflow.keras.models.load_model("modelo_lesiones.keras")` y
preprocesaba fotos a `224 × 224`, RGB, valores divididos entre `255`.

Aquí incluimos **scripts reconstruidos** para ejemplificar la conversión y la inferencia.
**No son copias recuperadas byte a byte** de los scripts de entrenamiento ni del modelo `.keras`.
La app de Android no necesita estos scripts para usar `modelo_lesiones.tflite`.

Las funciones de GPIO, sensores, dispensadores y voz del prototipo Raspberry Pi no fueron portadas
al código Android reconstruido. No declarar dichas funciones en el CV como implementadas en Android.
