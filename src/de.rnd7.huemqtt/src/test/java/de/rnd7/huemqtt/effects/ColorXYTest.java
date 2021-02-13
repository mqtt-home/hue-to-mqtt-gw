package de.rnd7.huemqtt.effects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorXYTest {

    @Test
    void test_equals_contract() {
        EqualsVerifier.forClass(ColorXY.class).verify();
    }
    
    @Test
    void test_string() {
    	Assertions.assertTrue(new ColorXY().toString().startsWith("ColorXY{"));
    }

    @Test
    void test_get_xy() {
        final List<Float> xy = new ColorXY().getXY();
        assertEquals(0.3687f, xy.get(0), 0.0001);
        assertEquals(0.371f, xy.get(1), 0.0001);
    }
}
