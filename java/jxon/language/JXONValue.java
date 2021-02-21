package jxon.language;

import lifya.Token;
import lifya.lexeme.Symbol;
import lifya.lookahead.Rule;

public class JXONValue extends Rule{
    public final static String TAG = "VALUE"; 
    public JXONValue(JXONParser parser) { super(TAG, parser); }
    
    @Override
    public boolean startsWith(Token t) {
	if(t.type().equals(Token.ERROR)) return false;
	if(t.type().equals(Symbol.TAG)) {
	    char c = (char)t.value();
	    return c=='[' || c== '{';
	}
	return true; 
    }
    
    @Override
    public Token analize(lifya.Lexer lexer, Token current) {
	if(current.type()==Symbol.TAG) {
	    char c = (char)current.value();
	    switch(c) {
	    case '[': return parser.rule(JXONList.TAG).analize(lexer, current);
	    case '{': return parser.rule(JXONObj.TAG).analize(lexer, current);
	    default: return current.toError();
	    }
	}
	return current;
    }
}