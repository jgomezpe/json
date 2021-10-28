

import json.JSONReader;
import lifya.Source;
import lifya.stringify.Stringifier;
import speco.json.JSON;

public class JSONTest {
	public static void language() {
		System.out.println("****************Language********************");
		JSONReader language = new JSONReader(); 
		JSON json = new JSON();
		json.set("id", 123456);
		json.set("price", 34.56);
		json.set("void", new double[] {});
		json.set("discount", new double[] {123.45,-23.56,67.88e-12});
		json.set("empty", new JSON());
		JSON jx = new JSON();
		jx.set("sss", null);
		json.set("dummy", jx);
		String program = Stringifier.apply(json);
		try {
			System.out.println("*****Testing with****");
			System.out.println(program);
			Source source = new Source("view", program);
			JSON t = language.get(source);
			System.out.println("*****Obtained****");
			System.out.println(Stringifier.apply(t));
		} catch (Exception e) { e.printStackTrace(); }			
	}
	
	
	public static void main(String[] args) {
		language();
	}	

}
