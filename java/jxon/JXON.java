package jxon;

import java.io.IOException;
import java.util.HashMap;

import jxon.language.JXONLanguage;
import kopii.Copier;
import kopii.Copyable;
import lifya.stringify.Stringifier;

public class JXON implements Castable, Configurable, Copyable{
    protected HashMap<String, Object> attributes = new HashMap<String, Object>();

    protected boolean extended;
    public JXON(){ this(true); }
    public JXON(boolean extended){ this.extended = extended; }
   
    public boolean valid(String key) { return attributes.containsKey(key); }

    public Iterable<String> keys(){ return attributes.keySet(); }
    
    public boolean set(String key, Object obj ){
	if( storable(obj) ){
	    if( obj instanceof double[] ){
		double[] a = (double[])obj;
		Object[] x = new Object[a.length];
		for( int i=0; i<a.length; i++ ) x[i] = a[i];
		obj = x;
	    }else if( obj instanceof int[] ){
		int[] a = (int[])obj;
		Object[] x = new Object[a.length];
		for( int i=0; i<a.length; i++ ) x[i] = a[i];
		obj = x;
	    }
	    attributes.put(key, obj);
	    return true;
	}
	return false;
    } 
	
    public Object get(String tag) { return attributes.get(tag); }
    
    public double real( String tag ){
	try{
	    Object obj = get(tag);
	    if( obj instanceof Double ) return (Double)obj;
	    if( obj instanceof Integer ) return (Integer)obj;
	}catch(Exception e){}
	return 0;
    }
	
    public int integer( String tag ){ try{ return (Integer)get(tag); }catch(Exception e){ return 0; } } 
	
    public boolean bool( String tag ){ try{ return (Boolean)get(tag); }catch(Exception e){ return false; } }

    public byte[] blob( String tag ){ try{ return (byte[])get(tag);  }catch(Exception e){ return null; } }

    public String string( String tag ){ try{ return (String)get(tag); }catch(Exception e){ return null; } }

    public Object[] array( String tag ){ try{ return (Object[])get(tag); }catch(Exception e){ return null; } }

    public int[] integers_array( String tag ){ 
	Object[] a = array(tag);
	int[] x = null;
	if( a!=null ){
	    x = new int[a.length];
	    try{ 
		for(int i=0; i<a.length; i++ )
		    x[i] = (Integer)a[i]; 
	    }catch(Exception e){ x = null; }
	} 
	return x;
    }
    
    public double[] reals_array( String tag ){
	Object[] a = array(tag);
	double[] x = null;
	if( a!=null ){
	    x = new double[a.length];
	    try{ 
		for(int i=0; i<a.length; i++ ) 
		    x[i] = (a[i] instanceof Double)?(Double)a[i]:(Integer)a[i];
	    }catch(Exception e){ x = null; }
	} 
	return x;
    }

    public JXON object( String tag ){ try{ return (JXON)get(tag); }catch(Exception e){ return null; } }
	
    public boolean storable(Object obj){
	if( obj instanceof Object[] ){
	    Object[] v = (Object[])obj;
	    int i=0;
	    while( i<v.length && storable(v[i]) ){ i++; }
	    return i==v.length;
	}
	return ( obj == null || obj instanceof JXON || 
		(extended && obj instanceof byte[]) ||
		obj instanceof String || obj instanceof Integer || obj instanceof Double || 
		obj instanceof Boolean || obj instanceof double[] || obj instanceof int[] );
    }

    public void remove(String tag) { attributes.remove(tag); }
    
    public void clear() { attributes.clear(); }
    
    public int size() { return attributes.size(); }
    
    public boolean isEmpty() { return size()==0; }
    
    @Override
    public String stringify() { return Stringifier.apply(attributes); }

    @Override
    public Copyable copy(){ 
	JXON json = new JXON(extended);
	json.attributes = Copier.apply(attributes);
	return json; 
    }
    
    @Override
    public JXON jxon() { return this; }

    @Override
    public void config(JXON json){
	attributes.clear();
	extended = json.extended;
	attributes = Copier.apply(json.attributes);
    }
    
    public static JXON parse(String input) throws IOException{
	JXONLanguage parser = new JXONLanguage();
	return parser.get(input);
    }     
}