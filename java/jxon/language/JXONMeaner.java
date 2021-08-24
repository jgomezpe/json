/**
 * <p>Copyright: Copyright (c) 2019</p>
 *
 * <h3>License</h3>
 *
 * Copyright (c) 2019 by Jonatan Gomez-Perdomo. <br>
 * All rights reserved. <br>
 *
 * <p>Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <ul>
 * <li> Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <li> Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <li> Neither the name of the copyright owners, their employers, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * </ul>
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *
 *
 * @author <A HREF="http://disi.unal.edu.co/profesores/jgomezpe"> Jonatan Gomez-Perdomo </A>
 * (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
 * @version 1.0
 */
package jxon.language;


import speco.jxon.JXON;
import lifya.Meaner;
import lifya.Token;
import speco.array.Array;

/**
 * <p>Title: JXONMeaner</p>
 *
 * <p>Description: Produces (if possible) a JXON/JSON object from a JXON syntactic token.</p>
 *
 */
public class JXONMeaner implements Meaner{
	/**
	 * JXON objects TAG
	 */
	public static final String TAG = "JXON";
	
	/**
	 * Create a JXON/JSON meaner
	 */
	public JXONMeaner() { }
		
	/**
	 * Creates a JXON token (token with a JXON object as value) from a Syntactic token
	 * @param obj Syntactic token 
	 * @return JXON token from a Syntactic token
	 */
	@Override
	public Token apply(Token obj){
		if( obj.isError() ) return obj;
		return new Token(TAG, obj.input(), obj.start(), obj.end(), inner_apply(obj));
	}

	/**
	 * Creates a JXON value from a value hold by a syntactic token 
	 * @param obj Token been analyzed
	 * @return JXON value from a value hold by a syntactic token
	 */
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