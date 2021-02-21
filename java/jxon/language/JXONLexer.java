package jxon.language;

import lifya.lexeme.BlobParser;
import lifya.lexeme.Lexeme;
import lifya.lexeme.NumberParser;
import lifya.lexeme.Space;
import lifya.lexeme.StringParser;
import lifya.lexeme.Symbol;
import lifya.lookahead.LAHLexer;

public class JXONLexer extends LAHLexer{
	public static Lexeme<?>[] lexemes(boolean extended){
	    if( extended ) return new Lexeme[] {
		new NumberParser(),
		new StringParser(),
		new BlobParser(true), 
		new JXONReserved(),
		new Symbol("[]{},:"),
		new Space() };
	    else
		return new Lexeme[] {
			new NumberParser(),
			new StringParser(),
			new JXONReserved(),
			new Symbol("[]{},:"),
			new Space() };
	}
	    

	public JXONLexer() { this(true); }

	public JXONLexer(boolean extended) { super(lexemes(extended), new String[] {Space.TAG}); }
}