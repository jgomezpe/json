package test;

import java.io.IOException;

import jxon.JXON;
import jxon.language.JXONLanguage;
import kopii.Copier;

public class JXONTest {
    public static void main( String[] args ) {
	JXON json = new JXON();
	json.set("id","Hello Motto!");
	json.set("nuip", new Object[] {123.4,"4567",null});
	String input = json.stringify();
	System.out.println(input);
	JXONLanguage parser = new JXONLanguage();
	try{
	    json = parser.get(input);
	    JXON  json2 = (JXON)Copier.apply(json);
	    json2.array("nuip")[0] = "Clear";
	    System.out.println(json.stringify());
	    System.out.println(json2.stringify());
	    //json = parser.get("{"+input);
	    //System.out.println(json.stringify());
	}catch(IOException e) { e.printStackTrace(); }
    }
}
