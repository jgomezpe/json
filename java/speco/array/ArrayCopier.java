package speco.array;

import kopii.Copier;

public class ArrayCopier {
    public static Object apply(Object buffer) {
	return apply(buffer, 0, java.lang.reflect.Array.getLength(buffer));
    }
    
    public static Object apply(Object buffer, int start, int end) {
	int n = end-start;
	Object clone = java.lang.reflect.Array.newInstance(Object.class,n);
	for( int i=0; i<n; i++)
	    java.lang.reflect.Array.set(clone, i, 
		    Copier.apply(java.lang.reflect.Array.get(buffer, start++)));
	return clone;
    }
    
    public static Array<?> apply(Array<?> array){
	Array<?> clone = array.instance(array.size());
	clone.buffer = apply(array.buffer);
	return clone;
    }
}
