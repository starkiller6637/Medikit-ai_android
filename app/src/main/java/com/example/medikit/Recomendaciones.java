package com.example.medikit;

import java.util.Locale;

/** First-aid guidance by label, not an automated medical diagnosis. */
public final class Recomendaciones {
    private Recomendaciones() {}

    public static String para(String clase, float confianza) {
        if (clase == null) return "No se pudo clasificar la imagen. Solicita una valoración profesional si tienes síntomas.";
        String prefijo = confianza < 0.50f
                ? "La clasificación es incierta. Intenta otra foto con buena iluminación. "
                : "";
        switch (clase.toLowerCase(Locale.ROOT)) {
            case "raspaduras":
                return prefijo + "Si se trata de una raspadura superficial, limpia con agua corriente, seca alrededor y cubre con gasa estéril no adherente si hace falta. Consulta si aumenta el dolor, el enrojecimiento o aparece pus.";
            case "cortaduras":
                return prefijo + "Si hay sangrado, aplica presión directa con una gasa limpia. Limpia con agua corriente y cubre con un apósito estéril. Busca atención urgente si es profunda, está muy abierta o no deja de sangrar.";
            case "quemaduras":
                return prefijo + "Ante una quemadura reciente, enfría con agua corriente fresca durante 20 minutos; no uses hielo. Cubre con un apósito limpio no adherente. Busca atención urgente si es extensa, profunda o afecta cara, manos, pies o genitales.";
            case "hematomas":
                return prefijo + "Si tienes un moretón, puedes aplicar una compresa fría envuelta en tela durante intervalos cortos. Consulta si apareció sin golpe, aumenta rápidamente o hay dolor intenso.";
            case "alergias":
                return prefijo + "Si observas una reacción en la piel, evita el posible desencadenante y limpia suavemente la zona. No todas las erupciones son alergias. Si hay dificultad para respirar o hinchazón de lengua o garganta, llama a emergencias.";
            case "esguinces":
                return prefijo + "Una imagen por sí sola no confirma un esguince. Si hubo torcedura, evita cargar peso, aplica frío envuelto en tela y solicita valoración si hay dolor importante o no puedes apoyar la extremidad.";
            case "fracturas":
                return prefijo + "Una fotografía no permite confirmar o descartar una fractura. Si la sospechas por dolor intenso, deformidad o incapacidad de mover o apoyar, inmoviliza sin forzar y busca atención médica urgente.";
            case "no lesiones":
                return prefijo + "El modelo no identificó una lesión visible de sus categorías. Esto NO descarta heridas, infecciones u otros problemas. Si presentas dolor, inflamación o síntomas, busca valoración profesional.";
            default:
                return "No hay una recomendación específica disponible. Consulta a un profesional si estás preocupado por la lesión.";
        }
    }
}
