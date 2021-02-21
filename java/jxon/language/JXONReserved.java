package jxon.language;

import lifya.Source;
import lifya.Token;
import lifya.lexeme.ID;

public class JXONReserved extends ID<Object>{
    public static final String TAG = "reserved";

    @Override
    public String type() { return TAG; }

    public JXONReserved(){}	
	
    @Override
    public Token match(Source input, int start, int end) {
	Token t = super.match(input, start, end);
	String v = (String)t.value();
	switch(v) {
	case "true":
	    t.value(true);
	    return t;
	case "false":
	    t.value(false);
	    return t;
	case "null":
	    t.value(null);
	    return t;
	default:
	    t.type(Token.ERROR);
	    t.value(type());
	    return t;
	}
    }

    @Override
    public boolean startsWith(char c) { return c=='t' || c=='f' || c=='n'; }
}