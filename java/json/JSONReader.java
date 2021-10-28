package json;

import java.io.IOException;

import lifya.Language;
import lifya.Parser;
import lifya.Source;
import lifya.Token;
import lifya.parsergenerator.ParserGenerator;
import lifya.parsergenerator.ProcessDerivationTree;
import speco.array.Array;
import speco.json.JSON;

public class JSONReader extends Language<JSON>{
	
	protected final static String number="<number> = [\\+|\\-]?\\d+(\\.\\d+)?([e|E][\\+|\\-]?\\d+)?\n";
	protected final static String space ="<%space> = [\\n|\\r|\\t|\\s]+\n";
	protected final static String string="<string> = \"(-[\\\\|\"]|\\\\([\\\\|n|r|t|\"]|u[A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d]))*\"\n";
	protected final static String reserved ="<reserved>  = {true|false|null}\n";
	
	protected final static String object ="<object> :- \\{ <attrlist>? \\}.\n";
	protected final static String attrlist ="<attrlist> :- <attribute> (, <attribute>)*.\n";
	protected final static String attribute ="<attribute> :- <string> \\: <value>.\n";
	protected final static String value ="<value> :- <object> | <list> | <reserved> | <number> | <string>.\n";
	protected final static String list ="<list> :- \\[ <itemlist>? \\].\n";
	protected final static String itemlist ="<itemlist> :- <value> (, <value>)*.\n";
	
	protected final static String ATTRLIST = "<attrlist>";
	protected final static String ITEMLIST = "<itemlist>";
	protected final static String OBJECT = "<object>";
	protected final static String LIST = "<list>";
	protected final static String ATTRIBUTE = "<attribute>";
	protected final static String STRING = "<string>";
	protected final static String RESERVED = "<reserved>";
	protected final static String NUMBER = "<number>";

	protected static String parser=string+number+reserved+space+
								object+attrlist+attribute+value+list+itemlist;
	
	protected static Parser init() {
		try { return ParserGenerator.parser(parser,OBJECT);  } 
		catch (IOException e) { e.printStackTrace(); }
		return null;
	}

	
	public JSONReader() { super(init()); }

	@SuppressWarnings({ "unchecked" })
	protected Object process(Token t) {
		Array<Token> a;
		switch(t.type()) {
			case ATTRLIST:
				JSON json = new JSON();
				a = (Array<Token>)t.value();
				for( int i=0; i<a.size(); i++ ) {
					Array<Token> b = (Array<Token>)a.get(i).value();
					String key = ParserGenerator.raw_string((String)b.get(0).value(),'"');
					Object attr = process(b.get(1));
					json.set(key, attr);
				}
				return json;
			case STRING: return ParserGenerator.raw_string((String)t.value(),'"');
			case NUMBER: return Double.parseDouble((String)t.value());
			case RESERVED:
				switch((String)t.value()) {
					case "true": return true;
					case "false": return false;
					default: return null;
				}
			case LIST: return new Object[] {};
			case OBJECT: return new JSON();
			default:
				a = (Array<Token>)t.value();
				Object[] list = new Object[a.size()];
				for( int i=0; i<a.size(); i++)
					list[i] = process(a.get(i));
				return list; 
		}		
	}
		
	/**
	 * 
	 * @param t Creates an object with meaning
	 * @return Semantic token (from syntactic token)
	 */
	public Token mean(Token t) {
		t = ProcessDerivationTree.eliminate_lambda(t);
		t = ProcessDerivationTree.eliminate_token(t, "<list>oper", null);
		t = ProcessDerivationTree.replace(t, ITEMLIST+"-item-1", ITEMLIST);
		t = ProcessDerivationTree.replace(t, ATTRLIST+"-item-1", ATTRLIST);
		t = ProcessDerivationTree.reduce_size_1(t);
		t = ProcessDerivationTree.reduce_exp(t, LIST);
		t = ProcessDerivationTree.reduce_exp(t, ITEMLIST);
		t = ProcessDerivationTree.reduce_exp(t, ATTRLIST);
		Object json = process(t);
		t.value(json);
		return t;
	}	
	
	/**
	 * Parses a String for a JSON/JXON object
	 * @param input Input string
	 * @return The JSON/JSON represented by the input String
	 * @throws IOException If the input String does not represent a JSON/JXON object
	 */
	public static JSON apply(String input) throws IOException{
		JSONReader parser = new JSONReader();
		return parser.get(new Source("noname",input)) ;
	}     	
}
