package houtbecke.rs.antbytes;

import java.lang.annotation.Annotation;

public class AnnotationParsingParameters {

    public final int bytePos;
    public final int relativeBitPos;
    public final int byteLength;
    public final boolean signed;
    public final boolean isLSB;

    public AnnotationParsingParameters(Annotation annotation, int byteShift) {

        Class type = annotation.annotationType();

        if (type == U8BIT.class) {
            U8BIT castedAnnotation = (U8BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 1;
            this.signed = false;
            this.isLSB = false;
        } else if (type == U16BIT.class) {
            U16BIT castedAnnotation = (U16BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 2;
            this.signed = false;
            this.isLSB = false;
        } else if (type == U24BIT.class) {
            U24BIT castedAnnotation = (U24BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 3;
            this.signed = false;
            this.isLSB = false;
        } else if (type == U32BIT.class) {
            U32BIT castedAnnotation = (U32BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 4;
            this.signed = false;
            this.isLSB = false;
        } else if (type == LSBU16BIT.class) {
            LSBU16BIT castedAnnotation = (LSBU16BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 2;
            this.signed = false;
            this.isLSB = true;
        } else if (type == LSBU24BIT.class) {
            LSBU24BIT castedAnnotation = (LSBU24BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 3;
            this.signed = false;
            this.isLSB = true;
        } else if (type == LSBU32BIT.class) {
            LSBU32BIT castedAnnotation = (LSBU32BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 4;
            this.signed = false;
            this.isLSB = true;
        } else if (type == LSBS16BIT.class) {
            LSBS16BIT castedAnnotation = (LSBS16BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 2;
            this.signed = true;
            this.isLSB = true;
        } else if (type == LSBS24BIT.class) {
            LSBS24BIT castedAnnotation = (LSBS24BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 3;
            this.signed = true;
            this.isLSB = true;
        } else if (type == LSBS32BIT.class) {
            LSBS32BIT castedAnnotation = (LSBS32BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 4;
            this.signed = true;
            this.isLSB = true;
        } else if (type == S8BIT.class) {
            S8BIT castedAnnotation = (S8BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 1;
            this.signed = true;
            this.isLSB = false;
        } else if (type == S16BIT.class) {
            S16BIT castedAnnotation = (S16BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 2;
            this.signed = true;
            this.isLSB = false;
        } else if (type == S32BIT.class) {
            S32BIT castedAnnotation = (S32BIT) annotation;
            this.bytePos = castedAnnotation.value() + byteShift;
            this.relativeBitPos = castedAnnotation.startBit();
            this.byteLength = 4;
            this.signed = true;
            this.isLSB = false;
        } else {
            this.bytePos = 0;
            this.relativeBitPos = 0;
            this.byteLength = 0;
            this.signed = false;
            this.isLSB = false;
        }
    }

    public boolean isValid() {
        return this.byteLength > 0;
    }


}
