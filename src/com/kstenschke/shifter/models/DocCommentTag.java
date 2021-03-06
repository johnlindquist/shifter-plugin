package com.kstenschke.shifter.models;

import com.kstenschke.shifter.helpers.ArrayHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DocCommentType class
 */
public class DocCommentTag {

	private String[] tagsJavaScript;

	private String[] tagsJava;

	private String[] tagsPHP;



	/**
	 * Constructor
	 */
	public DocCommentTag() {
		tagsJavaScript = new String[]{ "author", "class", "constructor", "deprecated", "exception", "method", "module", "namespace", "param", "private", "property", "return", "see", "this", "throws", "type", "version"};
		tagsJava       = new String[]{ "author", "version", "param", "return", "exception", "throws", "see", "since", "serial", "deprecated"};
		tagsPHP        = new String[]{ "abstract", "access", "author", "constant", "deprecated", "final", "global", "magic", "module", "param", "package", "return", "see", "static", "subpackage", "throws", "todo", "var", "version" };
	}


	/**
	 * Returns string array with all recognized doc comment tags
	 *
	 * @return  Array
	 */
	private String[]   getAllTags() {
		return ArrayHelper.mergeStringArrays(this.tagsJavaScript, this.tagsJava, this.tagsPHP);
	}



	/**
	 * Returns pipe-separated list (as string) with all recognized doc comment tags
	 *
	 * @return  String
	 */
	public String   getAllTagsPiped() {
		String[] allTags = this.getAllTags();

		return ArrayHelper.implode(allTags, "|");
	}



	/**
	 * Check whether given String looks like a doc comment line
	 *
	 * @param   line       Line the caret is at
	 * @return  Boolean.
	 */
	public Boolean isDocCommentLineContext(String line) {
		line  = line.toLowerCase();

		String allTagsPiped = this.getAllTagsPiped();
		String regExPatternLine = "\\s+\\*\\s+@(" + allTagsPiped + ")";

		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile(regExPatternLine).matcher(line);
		while (m.find()) {
			if( !allMatches.contains(m.group())) {
				allMatches.add(m.group());
			}
		}

		return ( allMatches.size() > 0);
	}



	/**
	 * Check whether given String represents a data type (number / integer / string /...) from a doc comment (param / return /...)
	 *
	 * @param   word          String to be checked
	 * @param   prefixChar    Prefix character
	 * @param   line          Whole line containing the word
	 * @return  Boolean.
	 */
	public Boolean isDocCommentTag(String word, String prefixChar, String line) {
		if ( !prefixChar.equals("@") ) {
			return false;
		}

		return this.isDocCommentLineContext(line);
	}



	/**
	 * @param   word              String to be shifted
	 * @param   isUp              Shift up or down?
	 * @param   filename          Filename of the edited file
	 * @param   textAfterCaret    Document text after the caret
	 * @return                    Shifting result
	 */
	public String getShifted(String word, boolean isUp, String filename, String textAfterCaret) {
		String[] commentTags   = this.getTagsByFilename(filename);
		int amountTags   = commentTags.length;

		if( amountTags > 0 ) {
			word  = word.toLowerCase();
			List<String> commentTagsList = Arrays.asList(commentTags);
			int curIndex   =  commentTagsList.indexOf(word);

			if( curIndex > -1 ) {
				if( isUp ) {
					curIndex++;
					if( curIndex == amountTags ) {
						curIndex = 0;
					}
				} else {
					curIndex--;
					if( curIndex == -1 ) {
						curIndex = amountTags - 1;
					}
				}

				String shiftedWord   = commentTagsList.get(curIndex);

				if( shiftedWord.equals("method") ) {
					shiftedWord = shiftedWord + parseNextMethod(textAfterCaret);
				}

				return shiftedWord;
			}
		}

		return word;
	}



	/**
	 * Find first JavaScript function's name out of given code
	 *
	 * @param   jsCode
	 * @return  String   JavaScript Method name
	 */
	private String parseNextMethod(String jsCode) {
		List<String> allMatches = new ArrayList<String>();

		String regExPattern = "[a-zA-Z_$][0-9a-zA-Z_$]*\\s*:\\s*function";

		Matcher m = Pattern.compile(regExPattern).matcher(jsCode);
		while (m.find()) {
			if( !allMatches.contains(m.group())) {
				allMatches.add(m.group());
			}
		}

		String methodName  = "";

		if(allMatches.size() > 0) {
			methodName  = allMatches.get(0).replace("function", "").replace(":", "").trim();
			methodName  = "\t" + methodName;
		}

		return methodName;
	}



	/**
	 * Return array of data types of detected language of edited file
	 *
	 * @param   filename   Filename of edited file
	 * @return  String[]
	 */
	private String[] getTagsByFilename(String filename) {
		if( filename != null ) {
			filename = filename.toLowerCase();

			if( filename.endsWith(".js") ) {
				// JavaScript comment types
				return this.tagsJavaScript;
			} else if (filename.endsWith(".java") ) {
				// Java comment tags in the recommended order
				return this.tagsJava;
			}
		}
			// Default, e.g. PHP
		return this.tagsPHP;
	}

}