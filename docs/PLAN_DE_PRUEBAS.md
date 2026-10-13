# Diseño de pruebas del sistema

Estas son **pruebas propuestas**, no resultados ya ejecutados ni validación clínica.

| ID | Módulo | Escenario | Criterio de aceptación |
|----|--------|-----------|-----------------------|
| F-01 | Cámara | Capturar foto | Vista previa, predicción y sin cierre inesperado |
| F-02 | Galería | Elegir JPEG/PNG | Carga imagen y muestra resultado |
| F-03 | Flujo | Identificar nueva | Reinicia foto, resultado y botones |
| F-04 | Red | Sin Wi-Fi ni datos | Inferencia sigue funcionando |
| F-05 | Error | Archivo no válido | Mensaje comprensible, sin cierre inesperado |
| F-06 | Rendimiento | Analizar 20 fotos | No se bloquea la interfaz; registrar tiempos |
| M-01 | Modelo | Imagen independiente por cada clase | Generar matriz de confusión y reporte por clase |
| M-02 | Modelo | Imagen borrosa o fuera de distribución | No confundir score con certeza médica |
| M-03 | Modelo | Diferentes luces y tonos de piel | Estudiar desempeño estratificado |
| M-04 | Seguridad | Supuesta fractura/esguince | Avisar que fotografía no confirma la lesión |

## Métricas pendientes

Registrar conjunto de prueba independiente, número de imágenes por etiqueta,
exactitud, precisión, exhaustividad (recall), F1, matriz de confusión,
calibración de Softmax y latencia de inferencia en varios dispositivos.
**No existen métricas verificadas de precisión clínica, sensibilidad o especificidad**
en los archivos recuperados.

## Consideraciones éticas

No subir fotografías de pacientes ni conjuntos de datos sin autorización/licencia.
La aplicación es experimental y no sustituye evaluación profesional.
