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

class JXONAttribute extends Rule{
    static TAG = "ATTRIBUTE"
    
    constructor(parser) { super(JXONAttribute.TAG, parser) }
    
    startsWith(t) { return t.type == StringParser.TAG }
    
    analize(lexer, current=lexer.next()) {
        if(!this.startsWith(current)) return current.toError()
        var input = current.input
        var start = current.start
        var end = current.end
        var pair = [current,null]
        current = lexer.next()
        if(current==null) return this.eof(input,end)
        if(!this.check_symbol(current, ':')) return current.toError()
        end = current.end
        pair[1] = this.parser.analize(lexer,JXONValue.TAG)
        if(pair[1].isError()) return pair[1]
        return this.token(input,start,pair[1].end,pair)
    }
}

class JXONList extends ListRule{
    static TAG = "LIST"
    constructor(parser) { super(JXONList.TAG, parser, JXONValue.TAG) }
}

class JXONObj extends ListRule{
    static TAG = "OBJ" 
    constructor(parser) { super(JXONObj.TAG, parser, JXONAttribute.TAG, '{', '}', ',') }
}

class JXONReserved extends ID{
    static TAG = "reserved"

    constructor(){ super(JXONReserved.TAG) } 
    
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

    startsWith(c) { return c=='t' || c=='f' || c=='n' }
}

class JXONValue extends Rule{
    static TAG = "VALUE" 
    constructor(parser) { super(JXONValue.TAG, parser) }
    
    startsWith(t) {
        if(t.type == Token.ERROR) return false
        if(t.type == Symbol.TAG) return t.value=='[' || t.value== '{'
        return true 
    }
    
    analize(lexer, current=lexer.next()) {
        if(current.type==Symbol.TAG) {
            switch(current.value) {
                case '[': return this.parser.rule(JXONList.TAG).analize(lexer, current)
                case '{': return this.parser.rule(JXONObj.TAG).analize(lexer, current)
                default: return current.toError();
            }
        }
        return current
    }
}

class JXONLexer extends LookAHeadLexer{
    static lexemes = [
        new NumberParser(),
        new StringParser(),
        new BlobParser(true),
        new JXONReserved(),
        new Symbol("[]{},:"),
        new Space()
    ]
    
    constructor() { super([Space.TAG], JXONLexer.lexemes) }
}

class JXONParser extends Parser{
    static rules(){ 
        return [
            new JXONObj(null),
            new JXONList(null),
            new JXONValue(null),
            new JXONAttribute(null)
        ]
    }
    
    constructor(){ super(JXONParser.rules(), JXONObj.TAG) }    
}

class JXONMeaner extends Meaner{
    static TAG = "JSON"
    constructor() { super() }
        
    apply(obj){
        if( obj.isError() ) return obj
        return new Token(obj.input, obj.start, obj.end, this.inner_apply(obj), JXONMeaner.TAG)
    }

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

class JXONLanguage extends Language{
    constructor() {
        super(new JXONLexer(), new JXONParser(), new JXONMeaner())
    }
}

/**
 * <p>Title: Stringifier</p>
 *
 * <p>Description: Stringifies (Stores into a String) an object</p>
 *
 */
JXON = {
    parse(str){ return new JXONLanguage().get(str) },
    
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
                txt += comma + JXON.stringify( thing[i] )
                comma = ','
            }    
            txt += ']'    
            return txt
        }
        
        txt = '{'
        comma=""
        for( var c in thing ){
            txt += comma + JXON.stringify(c) + ":" + JXON.stringify( thing[c] )
            comma = ','
        } 
        txt += '}'
        return txt
    }
}

class Configurable{
    config(json){}
    jxon(){}
}