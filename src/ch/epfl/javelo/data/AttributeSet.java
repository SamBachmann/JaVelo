package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.StringJoiner;

public record AttributeSet(long bits) {

    public AttributeSet {
       long result = bits >> Attribute.COUNT;
        Preconditions.checkArgument(result == 0);
    }

    public static AttributeSet of(Attribute... attributes) {
        long newbits = 0;
        for (Attribute attribute : attributes) {
            long mask = 1L << attribute.ordinal();
            newbits = newbits | mask;
        }
        return new AttributeSet(newbits);
    }

    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return  (bits & mask) == mask;
    }

    public boolean intersects(AttributeSet that) {

        long mask = 1L;
        boolean intersection = false;
        for (int i = 0; i < 64; ++i) {
            intersection = (mask & this.bits & that.bits) == mask;
            if (intersection) {
                break;
            }
            mask = mask << 1;
        }
        return intersection;
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",","{","}");
        for (Attribute attribut : Attribute.ALL) {
            if (this.contains(attribut)) {
                j.add(attribut.keyValue());
            }
        }
        return j.toString();
    }
}
