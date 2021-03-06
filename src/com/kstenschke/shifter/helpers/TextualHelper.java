/*
 * Copyright 2012 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kstenschke.shifter.helpers;

import com.intellij.openapi.editor.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Static helper methods for analysis and manipulation of texts
 */
public class TextualHelper {

	/**
	 * Check whether given string is fully lower case
	 *
	 * @param   str      String to be checked
	 * @return  Boolean
	 */
	public static boolean isAllLowercase(String str) {
		return str.equals(str.toLowerCase());
	}



	/**
	 * Check whether given string is fully lower case
	 *
	 * @param   str      String to be checked
	 * @return  Boolean
	 */
	public static boolean isAllUppercase(String str) {
		return str.equals(str.toUpperCase());
	}



	/**
	 * Check whether the given string is a comma separated list
	 *
	 * @param   str         String to be checked
	 * @return  Boolean
	 */
	public static boolean isCommaSeparatedList(String str) {
		return str.contains(",");
	}



	/**
	 * Check whether given character is a newline character
	 *
	 * @param   charValue     A character
	 * @return  Boolean
	 */
	public static boolean charIsNewline(char charValue) {
		String str = String.valueOf(charValue);

		return str.matches("\n") || str.matches("\r") || str.matches("\r\n");
	}



	/**
	 * Convert given string to lower case with only first char in upper case
	 *
	 * @param   str      String to be converted
	 * @return           String with conversion result
	 */
	public static String toUcFirst(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
	}



	/**
	 * Check whether given string is lower case with only first char in upper case
	 *
	 * @param   str      String to be checked
	 * @return  Boolean  If the string is lower case with only first char in upper case.
	 */
	public static Boolean isUcFirst(String str) {
		return str.equals(TextualHelper.toUcFirst(str));
	}



	/**
	 * Get word at caret offset out of given text
	 *
	 * @param   text           The full text
	 * @param   cursorOffset   Character offset of caret
	 * @return                 The extracted word or null
	 */
	public static String getWordAtOffset(CharSequence text, int cursorOffset) {
		if (text.length() == 0) return null;

		if (cursorOffset > 0
				  && !Character.isJavaIdentifierPart(text.charAt(cursorOffset))
				  && Character.isJavaIdentifierPart(text.charAt(cursorOffset - 1))
				  ) {
			cursorOffset--;
		}

		if (Character.isJavaIdentifierPart(text.charAt(cursorOffset))) {
			int start = cursorOffset;
			int end = cursorOffset;


			while (start > 0 && Character.isJavaIdentifierPart(text.charAt(start - 1))) {
				start--;
			}

			while (end < text.length() && Character.isJavaIdentifierPart(text.charAt(end))) {
				end++;
			}

			return text.subSequence(start, end).toString();
		}

		return null;
	}



	/**
	 * Get sub sequence of given offsets out of given text
	 *
	 * @param   text           Text containing the sequence
	 * @param   offsetStart    Sub sequence start character offset
	 * @param   offsetEnd      Sub sequence end character offset
	 * @return                 Sub sequence
	 */
	public static String getSubString(CharSequence text, int offsetStart, int offsetEnd) {
		if (text.length() == 0) return null;

		return text.subSequence(offsetStart, offsetEnd).toString();
	}



	/**
	 * Get character BEFORE word at given caret offset
	 *
	 * @param   text     Full text
	 * @param   offset   Offset from before which to extract one character
	 * @return  String
	 */
	public static String getCharBeforeOffset(CharSequence text, int offset) {
		if (text.length() == 0 || offset == 0) return "";

		if (offset > 0 ) {
			return text.subSequence(offset-1, offset).toString();
		}

		return "";
	}



	/**
	 * Get character AFTER word at caret offset
	 *
	 * @param   text     Full text
	 * @param   offset   Offset from after which to extract one character
	 * @return  String
	 */
	public static String getCharAfterOffset(CharSequence text, int offset) {
		if (text.length() < offset+2 || offset == 0) return "";

		if (offset > 0 ) {
			return text.subSequence(offset+1, offset+2).toString();
		}

		return "";
	}



	/**
	 * Get starting position offset of word at given offset in given CharSequence
	 *
	 * @param   text
	 * @param   offset
	 * @return  int
	 */
	public static int getStartOfWordAtOffset(CharSequence text, int offset) {
		if (text.length() == 0) return 0;

		if (offset > 0
				  && !Character.isJavaIdentifierPart(text.charAt(offset))
				  && Character.isJavaIdentifierPart(text.charAt(offset - 1))
				  ) {
			offset--;
		}

		if (Character.isJavaIdentifierPart(text.charAt(offset))) {
			int start = offset;

			while (start > 0 && Character.isJavaIdentifierPart(text.charAt(start - 1))) {
				start--;
			}

			return start;
		}

		return 0;
	}



	/**
	 * @param   doc
	 * @param   startLine
	 * @param   endLine
	 * @return
	 */
	public static List<String> extractLines(Document doc, int startLine, int endLine) {
		List<String> lines = new ArrayList<String>(endLine - startLine);

		for (int i = startLine; i <= endLine; i++) {
			String line = TextualHelper.extractLine(doc, i);

			lines.add(line);
		}

		return lines;
	}



	/**
	 * @param   doc
	 * @param   lineNumber
	 * @return
	 */
	public static String extractLine(Document doc, int lineNumber) {
		int lineSeparatorLength = doc.getLineSeparatorLength(lineNumber);
		int startOffset = doc.getLineStartOffset(lineNumber);
		int endOffset = doc.getLineEndOffset(lineNumber) + lineSeparatorLength;

		String line = doc.getCharsSequence().subSequence(startOffset, endOffset).toString();

		// If last line has no \n, add it one
		// This causes adding a \n at the end of file when sort is applied on whole file and the file does not end
		// with \n... This is fixed after.
		if (lineSeparatorLength == 0) {
			line += "\n";
		}

		return line;
	}



	/**
	 * @param   lines
	 * @return
	 */
	public static StringBuilder joinLines(List<String> lines) {
		StringBuilder builder = new StringBuilder();

		for (String line : lines) {
			builder.append(line);
		}

		return builder;
	}

}
