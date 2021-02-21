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
 * <li> Redistributions of source code must retain the above applyright notice,
 * this list of conditions and the following disclaimer.
 * <li> Redistributions in binary form must reproduce the above applyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <li> Neither the name of the applyright owners, their employers, nor the
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
package kopii;

import java.util.HashMap;

import speco.array.Array;
import speco.array.ArrayCopier;

/**
 * <p>Title: Cloner</p>
 *
 * <p>Description: A cloning method</p>
 *
 */
public class Copier {
    public static int apply(int obj) { return obj; }
    public static Integer apply(Integer obj) { return obj; }
    public static double apply(double obj) { return obj; }
    public static Double apply(Double obj) { return obj; }
    public static boolean apply(boolean obj) { return obj; }
    public static Boolean apply(Boolean obj) { return obj; }
    public static char apply(char obj) { return obj; }
    public static Character apply(Character obj) { return obj; }
    public static long apply(long obj) { return obj; }
    public static Long apply(Long obj) { return obj; }
    public static String apply(String obj) { return obj; }
    
    public static HashMap<String,Object> apply(HashMap<String,Object> obj){
	HashMap<String,Object> clone = new HashMap<String, Object>();
	for( String key:obj.keySet())
	    clone.put(key, apply(obj.get(key)));
	return clone;
    }
   
    public static Object apply(Array<?> obj) {
	return ArrayCopier.apply(obj);
    }
    
    public static Copyable apply(Copyable obj) {
	return obj.copy();
    }
    
    @SuppressWarnings("unchecked")
    public static Object apply(Object obj) {
	if(obj==null) return null;
	if(obj.getClass().isArray()) return ArrayCopier.apply(obj);
	if(obj instanceof Copyable) return apply((Copyable)obj);
	if(obj instanceof Array) return apply((Array<?>)obj);
	if(obj instanceof HashMap) return apply((HashMap<String,Object>)obj);
	return obj; 
    }    
}