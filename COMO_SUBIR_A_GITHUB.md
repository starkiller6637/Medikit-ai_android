# Cómo subir Medikit AI a GitHub

## Opción A: con GitHub Desktop (recomendada si nunca has usado Git)

1. Descarga y descomprime `MedikitAI-Reconstruido-GitHub.zip`.
2. Abre **GitHub Desktop** y entra a **File → Add local repository**.
3. Selecciona la carpeta `MedikitAI-Reconstruido`.
4. Si te ofrece **Create a repository here**, acéptalo y haz el primer commit.
5. Haz clic en **Publish repository**, define un nombre como `medikit-ai-android` y elige visibilidad pública cuando hayas revisado que no hay imágenes privadas.

## Opción B: con Git por terminal

1. Crea en github.com un repositorio **vacío**, por ejemplo `medikit-ai-android`.
2. Abre una terminal en la carpeta del proyecto y ejecuta:

```bash
git init
git add .
git commit -m "Reconstruir Medikit AI con modelo TensorFlow Lite recuperado"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/medikit-ai-android.git
git push -u origin main
```

Sustituye `TU_USUARIO` por tu cuenta de GitHub. GitHub puede pedir autenticación mediante navegador o Git Credential Manager.

## Antes de publicar

- Comprueba que no incluyes imágenes clínicas reales, credenciales o bases de datos privadas.
- `modelo_lesiones.tflite` mide aproximadamente 9.5 MB y puede almacenarse con Git normal; no hace falta Git LFS para ese archivo.
- El APK antiguo **no está** en el repositorio reconstruido, pero puedes guardarlo aparte como copia histórica o subirlo a GitHub Releases.
- No afirmes que Medikit usa CameraX ni que su clasificación está médicamente validada.

## Cómo describir el proyecto en un CV

**Medikit AI — Aplicación Android con inferencia local de IA**  
Desarrollo de aplicación Android con Java y Kotlin; integración de modelo TensorFlow Lite para análisis de imágenes en ocho categorías, carga desde cámara y galería y presentación de puntuaciones y orientaciones generales. Gestión del proyecto con Git y documentación de pruebas.

> Si no utilizas Git para gestionar cambios posteriores, evita atribuirte experiencia avanzada con flujos de trabajo Git solamente por subir este primer repositorio.
