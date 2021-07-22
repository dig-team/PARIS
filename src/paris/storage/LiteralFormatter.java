package paris.storage;

import javatools.parsers.Char;
import javatools.parsers.DateParser;
import javatools.parsers.NumberParser;

/** Formats a literal*/
public enum LiteralFormatter {

	CUT_DATATYPE {
		@Override
		public String format(String literal) {
			return getString(literal);
		}
	},	
	
	NORMALIZE {
		@Override
		public String format(String literal) {
			return Char.normalize(literal);
		}		
	},
	
	TRIM_TO_YEAR {

		@Override
		public String format(String literal) {
			return(literal.replaceAll("-[#\\d][#\\d]-[#\\d][#\\d]", ""));
		}
		
	}
	;
	/** Formats a string*/
	public abstract String format(String literal);
	
	/** Formats a string with the formatters*/
	public static String format(String object, LiteralFormatter... formatters) {
		if (NumberParser.isNumberAndUnit(object) || DateParser.isDate(object))
			object = '"' + object + '"';
		else
			if(!object.startsWith("\"")) return(object);
		for (int i=0;i<formatters.length;i++)
			object=formatters[i].format(object);
		return object;
	}
	
	/** returns the string part of a literal (with quotes)*/
  public static String getString(String stringLiteral) {
    String[] split = literalAndDatatypeAndLanguage(stringLiteral);
    if (split == null) return (null);
    return (split[0]);
  }
  
  /** Splits a literal into literal (with quotes) and datatype, followed by the language. Non-existent components are NULL*/
  public static String[] literalAndDatatypeAndLanguage(String s) {
    if (s == null || !s.startsWith("\"")) return (null);

    // Get the language tag
    int at = s.lastIndexOf('@');
    if (at > 0 && s.indexOf('\"', at) == -1) {
      String language = s.substring(at + 1);
      String string = s.substring(0, at);
      return (new String[] { string, null, language });
    }

    // Get the data type
    int dta = s.lastIndexOf("\"^^");
    if (dta > 0 && s.indexOf('\"', dta + 1) == -1) {
      String datatype = s.substring(dta + 3);
      String string = s.substring(0, dta + 1);
      return (new String[] { string, datatype, null });
    }

    // Otherwise, return just the string
    return (new String[] { s, null, null });
  }

}
