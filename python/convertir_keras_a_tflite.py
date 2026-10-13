"""Reconstruction of the original Keras -> TFLite conversion workflow.

Requires the original modelo_lesiones.keras file, if you recover it.
Python package requirement: tensorflow (version compatible with the saved Keras model).
"""
from pathlib import Path
import tensorflow as tf

source = Path("modelo_lesiones.keras")
if not source.exists():
    raise FileNotFoundError("Put your original modelo_lesiones.keras in this directory")
model = tf.keras.models.load_model(source)
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converted = converter.convert()
output = Path("modelo_lesiones.tflite")
output.write_bytes(converted)
print(f"Modelo convertido: {output} ({len(converted):,} bytes)")
