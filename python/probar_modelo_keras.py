"""Recreated from the preprocessing/class-label logic discussed for DetectarHerida.py.
This is NOT the original training script and does not retrain anything.
"""
import sys
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing import image

CLASES = ["alergias", "cortaduras", "esguinces", "fracturas",
          "hematomas", "no_lesiones", "quemaduras", "raspaduras"]

if len(sys.argv) != 2:
    raise SystemExit("Uso: python probar_modelo_keras.py foto.jpg")
model = tf.keras.models.load_model("modelo_lesiones.keras")
img = image.load_img(sys.argv[1], target_size=(224, 224))
array = image.img_to_array(img)[None, ...] / 255.0
scores = model.predict(array, verbose=0)[0]
i = int(np.argmax(scores))
print(f"Clase: {CLASES[i]}; salida del modelo: {float(scores[i]):.4f}")
print("Esto es clasificación experimental, no un diagnóstico médico.")
