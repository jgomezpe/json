/**
*
* jxon.js
* <P>Java Script for JSON and JXON (JSON with blob encoding) processing.</P>
* <P> Requires base64.js, kompari.js, and lifya.js (lifya_wrap.js). </P>
* <P>A numtseng module <A HREF="https://numtseng.com/modules/jxon.js">https://numtseng.com/modules/jxon.js</A> 
*
* Copyright (c) 2021 by Jonatan Gomez-Perdomo. <br>
* All rights reserved. See <A HREF="https://github.com/jgomezpe/jxon">License</A>. <br>
*
* @author <A HREF="https://disi.unal.edu.co/~jgomezpe/"> Professor Jonatan Gomez-Perdomo </A>
* (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
* @version 1.0
*/

////////// JXON //////////////////

/**
 * Rule for JXON/JSON attributes. Rule for JXON/JSON attributes
 */
class JXONAttribute extends Rule{
    /**
     * Type of the Syntactic Rule for JXON attributes (pairs key/value)
     */
    static TAG = "ATTRIBUTE"
    
    /**
     * Creates a JXON attribute rule
     * @param parser Syntactic parser using the rule
     */
    constructor(parser) { super(JXONAttribute.TAG, parser) }
    
    /**
     * Determines if the JXON attribute rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) { return t.type == StringParser.TAG }
    
    /**
     * Creates a JXON attribute rule token using the <i>current</i> token as first token to analyze
     * @param lexer Lexer 
     * @param current Initial token
     * @return Rule token
     */
    analyze(lexer, current=lexer.next()) {
        if(!this.startsWith(current)) return current.toError()
        var input = current.input
        var start = current.start
        var end = current.end
        var pair = [current,null]
        current = lexer.next()
        if(current==null) return this.eof(input,end)
        if(!this.check_symbol(current, ':')) return current.toError()
        end = current.end
        pair[1] = this.parser.analyze(lexer,JXONValue.TAG)
        if(pair[1].isError()) return pair[1]
        return this.token(input,start,pair[1].end,pair)
    }
}

/**
 * Rule for JXON/JSON lists.
 */
class JXONList extends ListRule{
    /**
     * Type of the Syntactic Rule for JXON lists
     */
    static TAG = "LIST"
    /**
     * Creates a JXON list rule
     * @param parser Syntactic parser using the rule
     */
    constructor(parser) { super(JXONList.TAG, parser, JXONValue.TAG) }
}

/**
 * <p>Title: JXONObj</p>
 *
 * <p>Description: Rule for JXON/JSON objects.</p>
 *
 */
class JXONObj extends ListRule{
    /**
     * Type of the Syntactic Rule for JXON attributes (pairs key/value)
     */
    static TAG = "OBJ" 
    /**
     * Creates a JXON object rule
     * @param parser Syntactic parser using the rule
     */ 
    constructor(parser) { super(JXONObj.TAG, parser, JXONAttribute.TAG, '{', '}', ',') }
}

/**
 * Lexema for JXON/JSON reserved words (true, false, null).
 */
class JXONReserved extends ID{
    /**
     * Type of the lexema for JXON reserved words
     */
    static TAG = "reserved"

    /**
     * Default constructor
     */
    constructor(){ super(JXONReserved.TAG) } 
    
    /**
     * Creates a token with the JXON reserved words type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return ID token
     */
    match(input, start, end) {
        var t = super.match(input, start, end)
        switch(t.value) {
            case "true":
                t.value = true
                return t
            case "false":
                t.value = false
                return t
            case "null":
                t.value = null
                return t
            default:
                t.type = Token.ERROR
                t.value = this.type
                return t
        }
    }

    /**
     * Determines if the lexeme can start with the given character (a letter or '_')
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return c=='t' || c=='f' || c=='n' }
}

/**
 * Rule for JXON/JSON values
 */
class JXONValue extends Rule{
    /**
     * Type of the Syntactic Rule for JXON values
     */
    static TAG = "VALUE" 

    /**
     * Creates a JXON value rule
     * @param parser Syntactic parser using the rule
     */
    constructor(parser) { super(JXONValue.TAG, parser) }
    
    /**
     * Determines if the JXON value rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) {
        if(t.type == Token.ERROR) return false
        if(t.type == Symbol.TAG) return t.value=='[' || t.value== '{'
        return true 
    }
    
    /**
     * Creates a JXON value rule token using the <i>current</i> token as first token to analyze
     * @param lexer Lexer 
     * @param current Initial token
     * @return Rule token
     */
    analyze(lexer, current=lexer.next()) {
        if(current.type==Symbol.TAG) {
            switch(current.value) {
                case '[': return this.parser.rule(JXONList.TAG).analyze(lexer, current)
                case '{': return this.parser.rule(JXONObj.TAG).analyze(lexer, current)
                default: return current.toError();
            }
        }
        return current
    }
}

/**
 * Definition of the lexer for a language which parses JXON/JSON objects
 */
class JXONLexer extends LookAHeadLexer{
    /**
     * Set of lexema used by the JXON parser
     */
    static lexemes = [
        new NumberParser(),
        new StringParser(),
        new BlobParser(true),
        new JXONReserved(),
        new Symbol("[]{},:"),
        new Space()
    ]
    
    /**
     * Creates a Lexer for JXON objects
     */
    constructor() { super([Space.TAG], JXONLexer.lexemes) }
}

/**
 * <A JXON/JSON syntactic parser.
 */
class JXONParser extends Parser{
    /**
     * Rules defining the JXON syntactic parser
     * @return Rules defining the JXON syntactic parser
     */
    static rules(){ 
        return [
            new JXONObj(null),
            new JXONList(null),
            new JXONValue(null),
            new JXONAttribute(null)
        ]
    }
    
    /**
     * Creates a JXON/JSON Syntactic parser
     */
    constructor(){ super(JXONParser.rules(), JXONObj.TAG) }    
}

/**
 * Produces (if possible) a JXON/JSON object from a JXON syntactic token.
 */
class JXONMeaner extends Meaner{
    /**
     * JXON objects TAG
     */
    static TAG = "JXON"
    
    /**
     * Create a JXON/JSON meaner
     */    
    constructor() { super() }
        
    /**
     * Creates a JXON token (token with a JXON object as value) from a Syntactic token
     * @param obj Syntactic token 
     * @return JXON token from a Syntactic token
     */
    apply(obj){
        if( obj.isError() ) return obj
        return new Token(obj.input, obj.start, obj.end, this.inner_apply(obj), JXONMeaner.TAG)
    }

    /**
     * Creates a JXON value from a value hold by a syntactic token 
     * @param obj Token been analyzed
     * @return JXON value from a value hold by a syntactic token
     */
    inner_apply(obj){
        switch( obj.type ) {
            case JXONObj.TAG:
                var json = {}
                for(var i=0; i<obj.value.length; i++) {
                    var p = this.inner_apply(obj.value[i])
                    json[p[0]] = p[1]
                }
                return json
            case JXONAttribute.TAG:
                var pair = obj.value
                var value = this.inner_apply(pair[1])
                return [pair[0].value, value]
            case JXONList.TAG:
                var a = []
                for(var i=0; i<obj.value.length; i++)
                    a.push(this.inner_apply(obj.value[i]))            
                return a
            default:
                return obj.value
        }
    }
}

/**
 * Definition of a language for parsing JXON/JSON objects
 */
class JXONLanguage extends Language{
    /**
     * Creates a JXONLanguage
     */
    constructor() {
        super(new JXONLexer(), new JXONParser(), new JXONMeaner())
    }
    
    /**
     * Parses a String for a JSON/JXON object
     * @param str Input string
     * @return The JSON/JSON represented by the input String
     */
    parse(str){ return this.get(str) }

    /**
     * Stringifies an object
     * @param obj Object to be stringified
     * @return A stringified version of the object
     */
    stringify( thing ){
        if( thing == null || typeof thing == 'number' || 
            typeof thing == 'boolean' ) return ""+thing
        
        if( typeof thing == 'string' ) return JSON.stringify(thing)
            
        if( thing.byteLength !== undefined ) 
            return BlobParser.STARTER + Base64.encode(thing)
           
        var txt 
        var comma=""
        if(Array.isArray(thing) ){
            txt = "["
            for( var i=0; i<thing.length; i++ ){
                txt += comma + this.stringify( thing[i] )
                comma = ','
            }    
            txt += ']'    
            return txt
        }
        
        txt = '{'
        comma=""
        for( var c in thing ){
            txt += comma + this.stringify(c) + ":" + this.stringify( thing[c] )
            comma = ','
        } 
        txt += '}'
        return txt
    }
}

/**
 * JXON/JSON global object
 */
JXON = new JXONLanguage()

/**
 * Object that is configurable by using a JXON object and can provided a JXON version of itself</p>
 *
 */
class Configurable{
    /**
     * Configures the object with the information provided by the JXON object
     * @param jxon COnfiguration information
     */
    config(jxon){}
    /**
     * Creates a JXON version of itself
     * @return A JXON version of itself
     */
    jxon(){}
}