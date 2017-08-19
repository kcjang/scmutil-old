package com.kichang.util.fortify;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtil {
	public static final String DEFAULT_ENCODING = "UTF8";
	public static final String LINE_SEP = System.getProperty("line.separator");
	public static final String HEX = "0123456789abcdef";
	private static final char[] RegexMetachars = { '\\', '*', '+', '?', '|',
			'{', '[', '(', ')', '^', '$', '.', '#' };

	public static byte[] getBytes(String s) {
			byte[] rt = {};
			try {
				rt = s.getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			return rt;

	}

	public static String[] split(String s) {
		if (s == null) {
			return null;
		}
		ArrayList result = new ArrayList();
		char[] chars = s.toCharArray();
		int seqStart = 0;
		for (int i = 0; i < chars.length; ++i) {
			if ((chars[i] == ' ') || (chars[i] == '\t') || (chars[i] == '\n')) {
				if (i - seqStart > 0) {
					result.add(new String(chars, seqStart, i - seqStart));
				}
				seqStart = i + 1;
			}
		}
		if (seqStart < chars.length) {
			result.add(new String(chars, seqStart, chars.length - seqStart));
		}

		return ((String[]) result.toArray(new String[result.size()]));
	}

	public static String[] splitToArray(String s, char c) {
		List result = split(s, c);
		return ((String[]) result.toArray(new String[result.size()]));
	}

	/* FIXME 디버깅해야 함 */
	public static List<String> split(String s, char c) {
		List l = new ArrayList();
		int pos = 0;

		int mark = 0;
		while (true) {
			if (mark >= s.length()) {
				return l;
			}
			if (s.charAt(mark) != c) {
				break;
			}
			++mark;
		}

		for (pos = mark + 1; pos < s.length(); ++pos) {
			if (s.charAt(pos) == c) {
				l.add(s.substring(mark, pos));

				mark = pos + 1;
				while (true) {
					if (mark >= s.length()) {
						return l;
					}
					if (s.charAt(mark) != c) {
						break;
					}
					++mark;
				}
				pos = mark;
			}
		}

		l.add(s.substring(mark, pos));
		return l;
	}

	public static String join(String[] arr, String sep) {
		if (arr == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; ++i) {
			if (i != 0) {
				sb.append(sep);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static String join(Object[] arr, String sep) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; ++i) {
			sb.append(arr[i]);
			sb.append(sep);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - sep.length());
		}
		return sb.toString();
	}

	public static String join(String[] arr) {
		return join(arr, " ");
	}

	public static String join(Collection<?> values, String sep) {
		String[] arr = new String[values.size()];
		int i = 0;
		for (Iterator it = values.iterator(); it.hasNext();) {
			arr[(i++)] = String.valueOf(it.next());
		}
		return join(arr, sep);
	}

	public static String join(Collection<?> values) {
		return join(values, " ");
	}

	public static String stackTraceToString(Throwable t) {
		if (t == null) {
			return null;
		}
		StringWriter sp = new StringWriter();
		PrintWriter pw = new PrintWriter(sp);
		t.printStackTrace(pw);
		pw.flush();
		return sp.toString();
	}

	/* FIXME 디버깅해야 함 */
	public static String mergeConcat(String s1, String s2) {
		if (s1 == null)
			return s2;
		if (s2 == null) {
			return s1;
		}

		int maxSubstrLen = Math.min(s1.length(), s2.length());
		boolean foundMatch = false;
		
		int substrLen = 1;

		for (substrLen = 1; substrLen <= maxSubstrLen; ++substrLen) {
			String sub1 = s1.substring(s1.length() - substrLen, s1.length());
			String sub2 = s2.substring(0, substrLen);
			if (sub1.equals(sub2)) {
				foundMatch = true;
				break;
			}
		}
		if (foundMatch) {
			return s1.substring(0, s1.length() - substrLen) + s2;
		}
		return s1 + s2;
	}

	public static String[] parseArguments(String args) {
		return parseArguments(args, false);
	}

	public static String[] parseArguments(String args,
			boolean useWindowsSemantics) {
		args = args.trim();
		if (args.length() == 0) {
			return new String[0];
		}
		ArrayList result = new ArrayList();

		StringBuffer currentArg = null;
		boolean escape = false;
		boolean inQuote = false;
		for (int i = 0; i < args.length(); ++i) {
			char c = args.charAt(i);
			if (!(escape)) {
				if ((Character.isWhitespace(c)) && (!(inQuote))) {
					if (currentArg != null) {
						result.add(currentArg.toString());
						currentArg = null;
					}
				} else if ((c == '\\')
						&& (((!(useWindowsSemantics)) || (inQuote)))) {
					escape = true;
				} else if (c == '"') {
					inQuote = !(inQuote);
				}
			} else {
				if (currentArg == null) {
					currentArg = new StringBuffer();
				}

				boolean allowEscaped = (!(useWindowsSemantics)) && (c == '\\');
				allowEscaped |= c == '"';
				allowEscaped |= ((Character.isWhitespace(c)) && (!(inQuote)));
				if ((escape) && (!(allowEscaped))) {
					currentArg.append('\\');
				}

				currentArg.append(c);
				escape = false;
			}
		}
		if (currentArg != null) {
			result.add(currentArg.toString());
		}

		return ((String[]) result.toArray(new String[result.size()]));
	}

	public static void writeExecArgs(OutputStream out, List<String> args) {
		PrintWriter pw = new PrintWriter(out);
		for (Iterator iter = args.iterator(); iter.hasNext();) {
			String arg = (String) iter.next();
			if (arg.indexOf(32) != -1) {
				char[] chars = arg.toCharArray();
				StringBuilder argBuilder = new StringBuilder();
				int backslashCount = 0;
				for (int i = 0; i < chars.length; ++i) {
					if (chars[i] == '"')
						argBuilder.append("\\\"");
					else {
						argBuilder.append(chars[i]);
					}
					if (chars[i] == '\\')
						++backslashCount;
					else {
						backslashCount = 0;
					}
				}

				if (backslashCount % 2 != 0) {
					argBuilder.append("\\");
				}
				arg = '"' + argBuilder.toString() + '"';
			}
			pw.println(arg);
		}
		pw.flush();
	}

	public static String sanitizeForReplace(String val) {
		char[] chars = val.toCharArray();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < chars.length; ++i) {
			if (chars[i] == '\\')
				result.append("\\\\");
			else if (chars[i] == '$')
				result.append("\\$");
			else {
				result.append(chars[i]);
			}
		}
		return result.toString();
	}

	public static String formatForCommandLine(String message) {
		String[] words = split(message);
		StringBuilder result = new StringBuilder();

		String lineSep = System.getProperty("line.separator");
		int lineLength = 0;
		for (int i = 0; i < words.length; ++i) {
			String word = words[i];
			if ((lineLength != 0) && (lineLength + word.length() + 1 > 80)) {
				result.append(lineSep);
				lineLength = 0;
			} else if (lineLength != 0) {
				result.append(' ');
				++lineLength;
			}
			int lineEndInWord = word.lastIndexOf(10);
			if (lineEndInWord != -1)
				lineLength = word.length() - lineEndInWord - 1;
			else {
				lineLength += word.length();
			}
			result.append(word);
		}
		return result.toString();
	}

	public static String escapeSpecialChars(String original) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < original.length(); ++i) {
			char c = original.charAt(i);
			if (!(Character.isISOControl(c))) {
				buf.append(c);
			} else {
				buf.append("\\");
				switch (c) {
				case '\n':
					buf.append("n");
					break;
				case '\t':
					buf.append("t");
					break;
				case '\b':
					buf.append("b");
					break;
				case '\r':
					buf.append("r");
					break;
				case '\f':
					buf.append("f");
					break;
				default:
					buf.append(c);
				}
			}
		}

		return buf.toString();
	}

	public static String toJavaAsciiLiteral(String[] s) {
		if (s == null) {
			return null;
		}

		StringBuffer b = new StringBuffer();

		b.append("[");
		for (int i = 0; i < s.length; ++i) {
			toJavaAsciiLiteral(b, s[i]);
			b.append(", ");
		}
		if (s.length > 0) {
			b.setLength(b.length() - 2);
		}
		b.append("]");

		return b.toString();
	}

	public static String toJavaAsciiLiteral(String s) {
		StringBuffer b = new StringBuffer();
		toJavaAsciiLiteral(b, s);
		return b.toString();
	}

	public static void toJavaAsciiLiteral(StringBuffer b, String s) {
		if (s == null) {
			b.append("null");
			return;
		}

		b.append('"');
		for (int i = 0; i < s.length(); ++i) {
			toJavaAsciiLiteral(b, s.charAt(i));
		}
		b.append('"');
	}

	public static String toJavaAsciiLiteral(byte[] bytes) {
		StringBuffer b = new StringBuffer();
		toJavaAsciiLiteral(b, bytes);
		return b.toString();
	}

	public static void toJavaAsciiLiteral(StringBuffer b, byte[] bytes) {
		b.append('"');
		for (int i = 0; i < bytes.length; ++i) {
			toJavaAsciiLiteral(b, (char) (bytes[i] & 0xFF));
		}
		b.append('"');
	}

	public static void toJavaAsciiLiteral(StringBuffer b, char c) {
		if ((c >= ' ') && (c <= '~')) {
			if ((c == '\\') || (c == '"')) {
				b.append('\\');
				b.append(c);
			} else {
				b.append(c);
			}
		} else {
			b.append('\\');
			switch (c) {
			case '\b':
				b.append('b');
				break;
			case '\t':
				b.append('t');
				break;
			case '\n':
				b.append('n');
				break;
			case '\f':
				b.append('f');
				break;
			case '\r':
				b.append('r');
				break;
			default:
				b.append("u");
				int v = c;
				b.append("0123456789abcdef".charAt(v >> 12));
				b.append("0123456789abcdef".charAt(v >> 8 & 0xF));
				b.append("0123456789abcdef".charAt(v >> 4 & 0xF));
				b.append("0123456789abcdef".charAt(v & 0xF));
			}
		}
	}

	public static String escapeUnixCommandArgument(String argument) {
		StringBuilder sb = new StringBuilder("'");
		int lastindex = 0;
		while (true) {
			int index = argument.indexOf(39, lastindex);
			if (index == -1) {
				break;
			}
			sb.append(argument.substring(lastindex, index));
			sb.append("'\\''");
			lastindex = index + 1;
		}
		sb.append(argument.substring(lastindex));
		sb.append("'");
		return sb.toString();
	}

	public static String getFriendlyDescriptionOfChar(char c) {
		switch (c) {
		case ' ':
			return "<Space>";
		case '\b':
			return "<Backspace>";
		case '\t':
			return "<Tab>";
		case '\n':
			return "<LineFeed> (\\n)";
		case '\f':
			return "<FormFeed> (\\f)";
		case '\r':
			return "<CarriageReturn> (\\r)";
		case '\0':
			return "<Null> (\\0)";
		case '\7':
			return "<TerminalBell>";
		case '\27':
			return "<Escape>";
		case '':
			return "<Delete>";
		case '"':
		case '\'':
		case '`':
			return "(" + c + ")";
		}

		if ((c >= ' ') && (c <= '~')) {
			return "\"" + c + "\"";
		}
		if (c <= 255) {
			int v = c;
			return "#" + "0123456789abcdef".charAt(v >> 4 & 0xF)
					+ "0123456789abcdef".charAt(v & 0xF);
		}

		int v = c;
		return "#" + "0123456789abcdef".charAt(v >> 12)
				+ "0123456789abcdef".charAt(v >> 8 & 0xF)
				+ "0123456789abcdef".charAt(v >> 4 & 0xF)
				+ "0123456789abcdef".charAt(v & 0xF);
	}

	public static boolean isEmpty(String test) {
		return ((test == null) || ("".equals(test)));
	}

	public static String truncate(String s, int length) {
		if ((s == null) || (s.length() <= length)) {
			return s;
		}
		return s.substring(0, length);
	}

	public static String firstline(String s) {
		int idx = s.indexOf(10);
		return ((idx == -1) ? s : s.substring(0, idx));
	}

	public static boolean isLowerCase(String s) {
		for (int i = 0; i < s.length(); ++i) {
			if (!(Character.isLowerCase(s.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public static String throwableToString(Throwable cause) {
		StringBuilder ret = new StringBuilder();
		if (cause.getMessage() != null) {
			ret.append(cause.getMessage()).append("\n");
		}
		String stack = stackTraceToString(cause);
		if (stack != null) {
			ret.append(stack);
		}
		return ret.toString();
	}

	public static String replaceAll(String text, String replaceVal,
			String replaceWithVal) {
		StringBuffer buf = new StringBuffer();

		int fromIndex = 0;
		int nextIndex = 0;
		while ((nextIndex = text.indexOf(replaceVal, fromIndex)) != -1) {
			buf.append(text.substring(fromIndex, nextIndex));

			buf.append(replaceWithVal);

			fromIndex = nextIndex + replaceVal.length();
		}

		if (fromIndex < text.length()) {
			buf.append(text.substring(fromIndex));
		}
		return buf.toString();
	}

	public static void replaceAll(StringBuilder stringBuilder,
			String replaceVal, String replaceWithVal) {
		int index;
		while ((index = stringBuilder.lastIndexOf(replaceVal)) >= 0) {
			
			stringBuilder.replace(index, index + replaceVal.length(),
					replaceWithVal);
		}
	}

	public static void replaceAll(StringBuilder stringBuilder,
			String replaceVal, String replaceWithVal, boolean caseInsensitive) {
		if (isEmpty(replaceVal))
			return;
		if (caseInsensitive) {
			String replaceValLower = replaceVal.toLowerCase();
			String inputStringLower = stringBuilder.toString().toLowerCase();
			int index = inputStringLower.length();
			while ((index = inputStringLower.lastIndexOf(replaceValLower,
					index - 1)) >= 0)
				stringBuilder.replace(index, index + replaceVal.length(),
						replaceWithVal);
		} else {
			replaceAll(stringBuilder, replaceVal, replaceWithVal);
		}
	}

	public static String replaceFirst(String text, String replaceVal,
			String replaceWithVal) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		if ((i = text.indexOf(replaceVal, i)) != -1) {
			sb.append(text.substring(0, i)).append(replaceWithVal);
			sb.append(text.substring(i + replaceVal.length(), text.length()));
		} else {
			sb.append(text);
		}

		return sb.toString();
	}

	public static String normalizeNewlines(String string) {
		if (string == null) {
			return null;
		}
		String result = replaceAll(string, "\r\n", "\r");
		result = replaceAll(result, "\r", "\n");
		return result;
	}

	public static String buildClassPath(List<String> paths) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < paths.size(); ++i) {
			String path = (String) paths.get(i);
			ret.append(path);
			if (i + 1 < paths.size()) {
				ret.append(File.pathSeparator);
			}
		}
		return ret.toString();
	}

	public static String rawBytesToString(byte[] bytes) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < bytes.length; ++i) {
			byte b = bytes[i];
			char c = (char) (b & 0xFF);
			buf.append(c);
		}
		return buf.toString();
	}

	public static String[] splitOnNext(String content, String divide) {
		int index = content.indexOf(divide);
		if (index == -1) {
			return new String[] { content };
		}
		String[] div = new String[2];
		div[0] = content.substring(0, index);
		div[1] = content.substring(index);
		return div;
	}

	public static String[] splitOutNext(String content, String startMatch,
			String endMatch) {
		int startIndex = content.indexOf(startMatch);
		if (startIndex == -1) {
			return new String[] { content };
		}

		String[] div = new String[2];

		int endIndex = content.indexOf(endMatch, startIndex + 1);
		if (endIndex == -1) {
			div[0] = content.substring(0, startIndex);
			div[1] = content.substring(startIndex + startMatch.length());
		} else {
			div[0] = content.substring(0, startIndex);

			int afterMatchIndex = endIndex + endMatch.length();
			if (afterMatchIndex < content.length()) {
				int tmp107_106 = 0;
				String[] tmp107_104 = div;
				tmp107_104[tmp107_106] = tmp107_104[tmp107_106]
						+ content.substring(afterMatchIndex);
			}
			div[1] = content.substring(startIndex + startMatch.length(),
					endIndex);
		}

		return div;
	}

	public static String padLeft(String stringToPad, String padder, int size) {
		if (padder.length() == 0) {
			return stringToPad;
		}
		StringBuffer strb = new StringBuffer(size);
		StringCharacterIterator sci = new StringCharacterIterator(padder);
		while (strb.length() < size - stringToPad.length()) {
			for (char ch = sci.first(); ch != 65535; ch = sci.next()) {
				if (strb.length() < size - stringToPad.length()) {
					strb.insert(strb.length(), String.valueOf(ch));
				}
			}
		}
		return stringToPad;
	}

	public static String padRight(String stringToPad, String padder, int size) {
		if (padder.length() == 0) {
			return stringToPad;
		}
		StringBuffer strb = new StringBuffer(stringToPad);
		StringCharacterIterator sci = new StringCharacterIterator(padder);
		while (strb.length() < size) {
			for (char ch = sci.first(); ch != 65535; ch = sci.next()) {
				if (strb.length() < size) {
					strb.append(String.valueOf(ch));
				}
			}
		}
		return strb.toString();
	}

	public static String escapeForRegexPattern(String input) {
		StringBuilder out = new StringBuilder();
		char[] in = input.toCharArray();
		for (int i = 0; i < in.length; ++i) {
			char c = in[i];
			for (int j = 0; j < RegexMetachars.length; ++j) {
				if (c == RegexMetachars[j]) {
					out.append('\\');
					break;
				}
			}

			out.append(c);
		}
		return out.toString();
	}

	public static String trimLeft(String s) {
		return s.replaceAll("^\\s+", "");
	}

	public static boolean isHexDigit(char c) {
		return (((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F')) || ((c >= 'a') && (c <= 'f')));
	}

	public static boolean isAlphanumeric(String s) {
		return Pattern.matches("[a-zA-Z0-9]*", s);
	}

	public static String sanitizeFilename(String filename) {
		return filename.replaceAll("[^_a-zA-Z0-9\\-\\.]", "");
	}

	public static String sanitizeLog(String text) {
		return ((text != null) ? text.replaceAll("[^\\p{Print}]", "") : null);
	}

	public static String sanitizeHtml(String text) {
		return ((text != null) ? text.replaceAll("[^\\p{Alnum}\\x20\\._-]", "")
				: null);
	}

	public static String sanitizeGuid(String text) {
		return ((text == null) ? null : text.replaceAll("[^\\s0-9A-Fa-f\\-{}]",
				""));
	}

	public static boolean beginsWith(StringBuilder text, String beginsWithText,
			boolean caseInsensitive) {
		if (beginsWithText.equals(""))
			return false;
		int idx;

		if (caseInsensitive)
			idx = text.toString().toLowerCase()
					.indexOf(beginsWithText.toLowerCase());
		else {
			idx = text.indexOf(beginsWithText);
		}
		return (idx == 0);
	}

	public static boolean endsWith(StringBuilder text, String endsWithText,
			boolean caseInsensitive) {
		if (endsWithText.equals(""))
			return false;
		int idx;

		if (caseInsensitive)
			idx = text.toString().toLowerCase()
					.lastIndexOf(endsWithText.toLowerCase());
		else {
			idx = text.lastIndexOf(endsWithText);
		}
		if (idx == -1) {
			return false;
		}
		return (idx == text.length() - endsWithText.length());
	}

	public static String removeSuffix(String string, String suffix) {
		if (string.endsWith(suffix)) {
			return string.substring(0, string.length() - suffix.length());
		}
		return string;
	}
}