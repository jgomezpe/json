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

// JSON Reader with lifya (partially generated (grammar part using lifya demo)
class JSONReader extends Language{
  constructor(){
    super( ParserGenerator.parser("<string> = \"(-[\\\\|\"]|\\\\([\\\\|n|r|t|\"]|u[A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d]))*\"\n<number> = [\\+|\\-]?\\d+(\\.\\d+)?([e|E][\\+|\\-]?\\d+)?\n<reserved>  = {true|false|null}\n<%space> = [\\n|\\r|\\t|\\s]+\n<object> :- \\{ <attrlist>? \\}.\n<attrlist> :- <attribute> (, <attribute>)*.\n<attribute> :- <string> \\: <value>.\n<value> :- <object> | <list> | <reserved> | <number> | <string>.\n<list> :- \\[ <itemlist>? \\].\n<itemlist> :- <value> (, <value>)*.\n", '<object>'))
  }
  process(t) { 
    Array<Token> a
    switch(t.type) {
      case '<attrlist>':
        var json = {}
        var a = t.value
        for( var i=0; i<a.length; i++ ) {
          var b = a[i].value
          var key = ParserGenerator.raw_string(b[0].value,'"')
          var attr = this.process(b[1])
          json[key] = attr
        }
        return json
      case '<string>': return ParserGenerator.raw_string(t.value,'"')
      case '<number>': return parseFloat(t.value)
      case '<reserved>':
        switch(t.value) {
          case 'true': return true
          case 'false': return false
          default: return null
        }
      case '<list>': return []
      case '<object>': return {}
      default:
        var a = t.value
        var list = []
        for( var i=0; i<a.length; i++)
          list.push(this.process(a[i]))
        return list
    }
  }
  mean(t) {
    t = ProcessDerivationTree.apply(t, [["LAMBDA"],["DEL","<list>oper"],["REPLACE","<itemlist>-item-1","<itemlist>"],["REPLACE","<attrlist>-item-1","<attrlist>"],["REDUCE"],["REDUCE","<list>"],["REDUCE","<itemlist>"],["REDUCE","<attrlist>"]])
    var obj = this.process(t)
    t.value = obj
    return t
  }
  apply(input){
    return this.get(new Source('noname',input))
  }
}

