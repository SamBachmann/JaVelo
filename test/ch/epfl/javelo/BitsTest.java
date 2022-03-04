package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsTest {
    @Test
    /**
     * Teste la méthode ExtractSigned à l'aide de l'exemple du site.
     */
    void TestExtractSignedExample(){
        int baseNumber = 0b1100_1010_1111_1110_1011_1010_1011_1110;
        int expected = Bits.extractSigned(baseNumber, 8,4);
        int actual = 0b1111_1111_1111_1111_1111_1111_1111_1010;
        assertEquals(expected, actual);
    }
    @Test
    void TestExtractUnSignedExample(){
        int baseNumber = 0b1100_1010_1111_1110_1011_1010_1011_1110;
        int expected = Bits.extractUnsigned(baseNumber, 8,4);
        int actual = 0b1010;
        assertEquals(expected, actual);
    }


}
