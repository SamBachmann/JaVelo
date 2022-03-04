package ch.epfl.javelo.projection;

import ch.epfl.javelo.data.Attribute;
import ch.epfl.javelo.data.AttributeSet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeSetTest {

    // Le test de la précondition a été fait, il fonctionne.

    // Fonctionne.
    @Test
    public void ofWorksOnKnownValues(){
        long bits = 0b1011L;
        AttributeSet actualAttributeSet = AttributeSet.of(Attribute.HIGHWAY_SERVICE, Attribute.HIGHWAY_TRACK,
                Attribute.HIGHWAY_FOOTWAY);
        AttributeSet expectedAttributeSet = new AttributeSet(bits);
        assertEquals(expectedAttributeSet, actualAttributeSet);
    }

    // Fonctionne (avec expected true ET false)
    @Test
    public void containsWorksOnKnownValues(){
        AttributeSet actualAttributeSet = AttributeSet.of(Attribute.HIGHWAY_SERVICE, Attribute.HIGHWAY_TRACK,
                Attribute.HIGHWAY_FOOTWAY);
        boolean actuallyContains = actualAttributeSet.contains(Attribute.HIGHWAY_CYCLEWAY);
        boolean expected = false;
        assertEquals(expected, actuallyContains);
    }

    // Fonctionne (avec expected true ET false)
    @Test
    public void intersectsWorksOnKnownValues(){
        AttributeSet attributeSet1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE, Attribute.HIGHWAY_TRACK,
                Attribute.HIGHWAY_FOOTWAY);
        AttributeSet attributeSet2 = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.TRACKTYPE_GRADE2,
                Attribute.HIGHWAY_TRACK);
        boolean actuallyIntersects = attributeSet1.intersects(attributeSet2);
        boolean expected = true;
        assertEquals(expected, actuallyIntersects);
    }

    // Fonctionne
    @Test
    public void theRedefinitionOfToStringWorks(){
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }
}
