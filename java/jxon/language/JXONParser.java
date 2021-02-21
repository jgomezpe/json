package jxon.language;

import lifya.lookahead.LAHParser;
import lifya.lookahead.Rule;

public class JXONParser extends LAHParser{
    protected static Rule[] rules() { return new Rule[] {
	    new JXONObj(null),
	    new JXONList(null),
	    new JXONValue(null),
	    new JXONAttribute(null)
    }; }
    
    public JXONParser(){
	super(rules(), JXONObj.TAG);
    }
	
}