package jxon;

import lifya.stringify.Stringifyable;

public interface Castable extends Stringifyable{
    JXON jxon();

    @Override
    default String stringify() { return jxon().stringify(); }
}