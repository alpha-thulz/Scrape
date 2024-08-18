package za.co.tyaphile.unittests;

import org.junit.jupiter.api.Test;
import za.co.tyaphile.Main;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    void testGetDoubleNumber() {
        Main main = new Main();
        assertEquals(11, main.getDecimalNumber("R11"));
        assertEquals(11, main.getDecimalNumber("R11.00"));
        assertEquals(11, main.getDecimalNumber("R11.00 per kg"));
        assertEquals(11, main.getDecimalNumber("R11.00 x kg"));
        assertEquals(11, main.getDecimalNumber("R11.00 x 51"));
        assertEquals(1000, main.getDecimalNumber("R1000.00"));
        assertEquals(1000, main.getDecimalNumber("R1,000.00"));
        assertEquals(0, main.getDecimalNumber(""));
        assertEquals(100, main.getDecimalNumber("100g"));
    }

    @Test
    void testGetQuantityOnPrice() {
        Main main = new Main();
        assertEquals("Kg", main.getQuantityOnPrice("R49.99 Per Kg"));
        assertEquals("", main.getQuantityOnPrice("R49.99"));
        assertEquals("24", main.getQuantityOnPrice("R49.99 x 24"));
    }

    @Test
    void testGetMeasurementUnit() {
        Main main = new Main();
        assertEquals("g", main.getMeasurementOnItem("100g"));
        assertEquals("g", main.getMeasurementOnItem("100 g"));
        assertEquals("kg", main.getMeasurementOnItem("5kg"));
        assertEquals("kg", main.getMeasurementOnItem("2 kg"));
    }
}
