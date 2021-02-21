package jxon.language;

import lifya.lookahead.ListRule;

public class JXONObj extends ListRule{
    public final static String TAG = "OBJ"; 
    public JXONObj(JXONParser parser) { super(TAG, parser, JXONAttribute.TAG, '{', '}', ','); }
}