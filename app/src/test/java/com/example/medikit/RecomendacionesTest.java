package com.example.medikit;

import org.junit.Test;
import static org.junit.Assert.*;

public class RecomendacionesTest {
    @Test public void raspaduraMencionaGasa() {
        assertTrue(Recomendaciones.para("Raspaduras", 0.9f).contains("gasa"));
    }
    @Test public void fracturaNoSeConfirmaConFoto() {
        assertTrue(Recomendaciones.para("Fracturas", 0.9f).contains("no permite confirmar"));
    }
    @Test public void bajaConfianzaIncluyeAdvertencia() {
        assertTrue(Recomendaciones.para("Alergias", 0.2f).contains("incierta"));
    }
}
