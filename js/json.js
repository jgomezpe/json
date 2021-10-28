/**
*
* json.js
* <P>Java Script for processing JSON using lifya</P>
* <P> Requires base64.js, kompari.js, and lifya.js </P>
* <P>A numtseng module <A HREF="https://numtseng.com/modules/lifya.js">https://numtseng.com/modules/json.js</A> 
*
* Copyright (c) 2021 by Jonatan Gomez-Perdomo. <br>
* All rights reserved. See <A HREF="https://github.com/jgomezpe/lifya">License</A>. <br>
*
* @author <A HREF="https://disi.unal.edu.co/~jgomezpe/"> Professor Jonatan Gomez-Perdomo </A>
* (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
* @version 1.0
*/

// JSON Reader with lifya
class JSONReader extends Language{
    
    static number="<number> = [\\+|\\-]?\\d+(\\.\\d+)?([e|E][\\+|\\-]?\\d+)?\n"
    static space ="<%space> = [\\n|\\r|\\t|\\s]+\n"
    static string="<string> = \"(-[\\\\|\"]|\\\\([\\\\|n|r|t|\"]|u[A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d]))*\"\n"
    static reserved ="<reserved>  = {true|false|null}\n"
    
    static object ="<object> :- \\{ <attrlist>? \\}.\n"
    static attrlist ="<attrlist> :- <attribute> (, <attribute>)*.\n"
    static attribute ="<attribute> :- <string> \\: <value>.\n"
    static value ="<value> :- <object> | <list> | <reserved> | <number> | <string>.\n"
    static list ="<list> :- \\[ <itemlist>? \\].\n"
    static itemlist ="<itemlist> :- <value> (, <value>)*.\n"
    
    static ATTRLIST = "<attrlist>"
    static ITEMLIST = "<itemlist>"
    static OBJECT = "<object>"
    static LIST = "<list>"
    static ATTRIBUTE = "<attribute>"
    static STRING = "<string>"
    static RESERVED = "<reserved>"
    static NUMBER = "<number>"

    static parser=JSONReader.string+JSONReader.number+JSONReader.reserved+JSONReader.space+
                    JSONReader.object+JSONReader.attrlist+JSONReader.attribute+JSONReader.value+
                    JSONReader.list+JSONReader.itemlist
    
    static init() { return ParserGenerator.parser(this.parser,JSONReader.OBJECT) }

    
    constructor() { super(JSONReader.init()) }

    process(t) {
        Array<Token> a;
        switch(t.type) {
            case JSONReader.ATTRLIST:
                var json = {}
                var a = t.value
                for( var i=0; i<a.length; i++ ) {
                    var b = a[i].value
                    var key = ParserGenerator.raw_string(b[0].value,'"')
                    var attr = this.process(b[1])
                    json[key] = attr
                }
                return json
            case JSONReader.STRING: return ParserGenerator.raw_string(t.value,'"')
            case JSONReader.NUMBER: return parseFloat(t.value)
            case JSONReader.RESERVED:
                switch(t.value) {
                    case "true": return true
                    case "false": return false
                    default: return null
                }
            case JSONReader.LIST: return []
            case JSONReader.OBJECT: return {}
            default:
                var a = t.value
                var list = []
                for( var i=0; i<a.length; i++)
                    list.push(this.process(a[i]))
                return list
        }       
    }
        
    /**
     * 
     * @param t Creates an object with meaning
     * @return Semantic token (from syntactic token)
     */
    mean(t) {
        t = ProcessDerivationTree.eliminate_lambda(t)
        t = ProcessDerivationTree.eliminate_token(t, "<list>oper", null)
        t = ProcessDerivationTree.replace(t, JSONReader.ITEMLIST+"-item-1", JSONReader.ITEMLIST)
        t = ProcessDerivationTree.replace(t, JSONReader.ATTRLIST+"-item-1", JSONReader.ATTRLIST)
        t = ProcessDerivationTree.reduce_size_1(t)
        t = ProcessDerivationTree.reduce_exp(t, JSONReader.LIST)
        t = ProcessDerivationTree.reduce_exp(t, JSONReader.ITEMLIST)
        t = ProcessDerivationTree.reduce_exp(t, JSONReader.ATTRLIST)
        var json = this.process(t)
        t.value = json
        return t
    }   
    
    /**
     * Parses a String for a JSON/JXON object
     * @param input Input string
     * @return The JSON/JSON represented by the input String
     * @throws IOException If the input String does not represent a JSON/JXON object
     */
    apply(input){
        var parser = new JSONReader()
        return parser.get(new Source("noname",input))
    }       
}