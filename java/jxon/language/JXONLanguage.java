package jxon.language;

import jxon.JXON;
import lifya.Language;

public class JXONLanguage extends Language<JXON>{
    public JXONLanguage() {
	super(new JXONLexer(), new jxon.language.JXONParser(), new JXONMeaner());
    }
}