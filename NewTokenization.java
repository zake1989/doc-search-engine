/**
 * this class will handle all tokenizaton, keep email address,
 * two or more words start with begin with a capital letter as a single charecter.
 */

import java.util.regex.*;
import java.util.*;
import java.io.*;

public class NewTokenization {
	public static ArrayList<String> singleAddress = new ArrayList<String>();

	private Pattern pattern;
	private Matcher matcher;

	private String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" 
									+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
                                    +"(.)?";
	private String captialWordPattern = "[A-Z]{1}[a-z]+(\\s[A-Z]{1}[a-z]+)?(\\s[A-Z]{1}[a-z]+)?(\\s[A-Z]{1}[a-z]+)?(\\s[A-Z]{1}[a-z]+)?(\\s[A-Z]{1}[a-z]+)?(\\s[A-Z]{1}[a-z]+)?(,)?";
    private String urlPattern = "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
            "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
            "|mil|biz|info|mobi|name|aero|jobs|museum" + 
            "|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
            "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
            "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
            "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
            "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
            "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b"+
            "(\\/\\/)?"+
            "(.)?";
            
	public NewTokenization() {

	}

    public void joinLineBreak(ArrayList<String> list) {
        for (int i = 0; i < list.size() ; i++ ) {
            if (list.get(i).endsWith("-")) {
                list.set(i, combineTwoBreakString(list.get(i), list.get(i + 1)));
                list.remove(i+1);
            }
        }
    }

    public void joinAllCaptialInAnArray(ArrayList<String> list) {       
        for (int i = 0; i < list.size(); i++) {            
            if (captialValidator(list.get(i)) && !list.get(i).endsWith(",")) {
                boolean bl = true;
               do {
                    if (captialValidator(list.get(i + 1))) {
                        list.set(i, combineTwoString(list.get(i), list.get(i + 1)));
                        if (list.get(i+1).endsWith(",")) {
                            list.remove(i+1);
                            bl = false;
                        } else {
                            list.remove(i+1);
                        }  
                    } else {
                        bl = false;
                    }
               }while (bl);
            }
        }
    }

    public boolean emailValidator(String s) {
		pattern = Pattern.compile(emailPattern);
		matcher = pattern.matcher(s);
		return matcher.matches();
	}

    public boolean urlValidator(String s) {
        pattern = Pattern.compile(urlPattern);
        matcher = pattern.matcher(s);
        return matcher.matches();
    }

    public boolean sigleCharacterValidator(String s) {
        boolean bl = false;
        if (s.length() == 1) {
            bl = true;            
        }    
        return bl;
    }

	public boolean captialValidator(String s) {
		pattern = Pattern.compile(captialWordPattern);
		matcher = pattern.matcher(s);
		return matcher.matches();
	}

    public String combineTwoBreakString(String a, String b) {
        String c = a + b;
        return c;
    }
   

	public String combineTwoString(String a, String b) {
		String c = a + " " + b;
		return c;
	}

	public String removeSymbolCharacters(String t) {
		t = t.replace("[","");
        t = t.replace("]","");
        t = t.replace("{","");
        t = t.replace("}","");
        t = t.replace("(","");
        t = t.replace(")","");
        t = t.replace(":","");
        t = t.replace(",","");
        t = t.replace("|","");
        t = t.replace(".","");
        t = t.replace("-","");
        t = t.replace("'","");
        t = t.replace("\\s+","");
        t = t.replace("\\n","");
        t = t.replace("\\r","");
        t = t.replace("\"","");
        t = t.replace("\"","");
        t = t.replace("=","");
        t = t.replace("+","");
        t = t.replace("_","");
        t = t.replace("\\","");
        t = t.replace("/","");
        t = t.replace("^","");
        t = t.replace("%","");
        t = t.replace("!","");
        t = t.replace("?","");
        t = t.replace("!","");
        t = t.replace("—","");
        t = t.replace(";","");
        t = t.replace("@","");
        t = t.replace("$","");
        t = t.replace("#","");
        return t;
	}

}
