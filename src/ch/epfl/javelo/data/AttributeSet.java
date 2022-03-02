package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

public record AttributeSet(long bits) {

    public AttributeSet {
       long result = bits >> Attribute.COUNT;
        Preconditions.checkArgument(result == 0);
    }

    public static AttributeSet of(Attribute... attributes) {
        long newbits = 0;
        for (int i = 0; i < attributes.length; ++i) {
            long bitsTemporaire = 1;

        }
        return new AttributeSet(newbits);
    }
}
