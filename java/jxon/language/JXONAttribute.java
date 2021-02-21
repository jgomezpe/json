package jxon.language;

import lifya.Source;
import lifya.Token;
import lifya.lexeme.StringParser;
import lifya.lookahead.Rule;

public class JXONAttribute extends Rule{
    public final static String TAG = "ATTRIBUTE"; 
    public JXONAttribute(JXONParser parser) { super(TAG, parser); }
    
    @Override
    public boolean startsWith(Token t) {
	return t.type().equals(StringParser.TAG);
    }
    
    @Override
    public Token analize(lifya.Lexer lexer, Token current) {
	if(!startsWith(current)) return current.toError();
	Source input = current.input();
	int start = current.start();
	int end = current.end();
	Token[] pair = new Token[2];
	pair[0] = current;
	current = lexer.next();
	if(current==null) return eof(input,end);
	if(!check_symbol(current, ':')) return current.toError();
	end = current.end();
	pair[1] = parser.analize(JXONValue.TAG,lexer);
	if(pair[1].isError()) return pair[1];
	return token(input,start,pair[1].end(),pair);
    }
}
