package jxon.language;


import jxon.JXON;
import lifya.Meaner;
import lifya.Token;
import speco.array.Array;

public class JXONMeaner implements Meaner{
    public static final String TAG = "JSON";
    public JXONMeaner() { }
		
    @Override
    public Token apply(Token obj){
	if( obj.isError() ) return obj;
	return new Token(TAG, obj.input(), obj.start(), obj.end(), inner_apply(obj));
    }

    public Object inner_apply(Token obj){
	switch( obj.type() ) {
	case JXONObj.TAG:
	    JXON json = new JXON();
	    @SuppressWarnings("unchecked") 
	    Array<Token> attr = (Array<Token>)obj.value();
	    for(Token a:attr) {
		Object[] p = (Object[])inner_apply(a);
		json.set((String)p[0], p[1]);
	    }
	    return json;
	case JXONAttribute.TAG:
	    Token[] pair = (Token[])obj.value();
	    Object value = inner_apply(pair[1]);
	    return new Object[] {pair[0].value(), value};
	case JXONList.TAG:
	    Array<Object> a = new Array<Object>();
	    @SuppressWarnings("unchecked") 
	    Array<Token> l = (Array<Token>)obj.value();
	    for(Token x:l) {
    		Object y = inner_apply(x);
    		a.add(y);
	    }
	    Object[] b = new Object[a.size()];
	    for(int i=0; i<b.length; i++) b[i] = a.get(i); 
	    return b;
	default:
	    return obj.value();
	}
    }
}