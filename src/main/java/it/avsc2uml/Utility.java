package it.avsc2uml;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utility {

	public final static String LINE_SEPARATOR = System.lineSeparator();

	public static void appendAndNewLine(StringBuilder stringBuilder, String toBeAppended) {
		stringBuilder.append(toBeAppended).append(LINE_SEPARATOR);
	}

	public static void appendAndNewLine(StringBuilder stringBuilder, CharSequence toBeAppended) {
		stringBuilder.append(toBeAppended).append(LINE_SEPARATOR);
	}
}
