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
public class DocCommentType {

	/**
	 * Constructor
	 */
	public DocCommentType() {

	}



	/**
	 * Check whether given String looks like a doc comment line
	 *
	 * @param   line       Line the caret is at
	 * @return  Boolean.
	 */
	public Boolean isDocCommentTypeLineContext(String line) {
		line  = line.toLowerCase();

		String allTags = new DocCommentTag().getAllTagsPiped();
		String regExPatternLine = "\\s+\\*\\s+@(" + allTags + ")\\s";

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
	public Boolean isDocCommentType(String word, String prefixChar, String line) {
		if ( prefixChar.equals("#") || prefixChar.equals("@") ) {
			return false;
		}

		return this.isDocCommentTypeLineContext(line);
	}



	/**
	 * @param   word          String to be shifted
	 * @param   editorText    Whole text of edited file
	 * @param   isUp          Shift up or down?
	 * @param   filename      Filename of the edited file
	 * @return                Shifting result
	 */
	public String getShifted(String word, CharSequence editorText, boolean isUp, String filename) {
		return new DataType().getShifted(word, filename, isUp);
	}

}