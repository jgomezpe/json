package jxon.language;

import lifya.lookahead.ListRule;

public class JXONList extends ListRule{
    public final static String TAG = "LIST"; 
    public JXONList(JXONParser parser) { super(TAG, parser, JXONValue.TAG); }
}
