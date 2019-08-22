package com.exedio.cope.instrument;

import java.util.Locale;
import java.util.Objects;

final class LiteralHelper
{
	private LiteralHelper()
	{
	}

	static String getLiteralFor(final Object constantValue)
	{
		Objects.requireNonNull(constantValue);
		if (constantValue instanceof String)
			return "\""+escapeJavaStyleString((String)constantValue)+"\"";
		else if (constantValue instanceof Long)
			return constantValue+"L";
		else if (constantValue instanceof Integer)
			return String.valueOf(constantValue);
		else if (constantValue instanceof Short)
			return "(short)"+constantValue;
		else if (constantValue instanceof Byte)
			return "(byte)"+constantValue;
		else if (constantValue instanceof Double)
			return getDoubleLiteralFor((double)constantValue);
		else if (constantValue instanceof Float)
			return getFloatLiteralFor((float)constantValue);
		else if (constantValue instanceof Boolean)
			return String.valueOf(constantValue);
		else if (constantValue instanceof Character)
			return "'"+escapeCharacter((char)constantValue)+"'";
		else
			throw new RuntimeException(constantValue.getClass().getName());
	}

	private static String getFloatLiteralFor(final float constantValue)
	{
		if (Float.isNaN(constantValue))
			return "java.lang.Float.NaN";
		else if (constantValue==Float.POSITIVE_INFINITY)
			return "java.lang.Float.POSITIVE_INFINITY";
		else if (constantValue==Float.NEGATIVE_INFINITY)
			return "java.lang.Float.NEGATIVE_INFINITY";
		return constantValue+"f";
	}

	private static String getDoubleLiteralFor(final double constantValue)
	{
		if (Double.isNaN(constantValue))
			return "java.lang.Double.NaN";
		else if (constantValue==Double.POSITIVE_INFINITY)
			return "java.lang.Double.POSITIVE_INFINITY";
		else if (constantValue==Double.NEGATIVE_INFINITY)
			return "java.lang.Double.NEGATIVE_INFINITY";
		return String.valueOf(constantValue);
	}

	private static CharSequence hexFourCharacters(final char ch)
	{
		final StringBuilder result = new StringBuilder(4);
		result.append( Integer.toHexString(ch).toUpperCase(Locale.ENGLISH) );
		while (result.length()<4)
			result.insert(0, "0");
		return result;
	}

	private static String escapeJavaStyleString(final String str)
	{
		final StringBuilder result = new StringBuilder();
		final int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			final char ch = str.charAt(i);
			appendCharacter(result, ch);
		}
		return result.toString();
	}

	private static CharSequence escapeCharacter(final char c)
	{
		final StringBuilder sb = new StringBuilder();
		appendCharacter(sb, c);
		return sb;
	}

	@SuppressWarnings("HardcodedLineSeparator")
	private static void appendCharacter(final StringBuilder result, final char ch)
	{
		if (ch > 0x7f)
		{
			// all characters beyond 7-bit ASCII are escaped as unicode
			result.append("\\u").append(hexFourCharacters(ch));
		}
		else if (ch < 32)
		{
			// ASCII control characters
			switch (ch)
			{
				case '\b':
					result.append('\\');
					result.append('b');
					break;
				case '\n':
					result.append('\\');
					result.append('n');
					break;
				case '\t':
					result.append('\\');
					result.append('t');
					break;
				case '\f':
					result.append('\\');
					result.append('f');
					break;
				case '\r':
					result.append('\\');
					result.append('r');
					break;
				default:
					result.append("\\u").append(hexFourCharacters(ch));
					break;
			}
		}
		else
		{
			// 7-bit ASCII non-control
			if (ch=='"' || ch=='\\' || ch=='\'')
				result.append('\\');
			result.append(ch);
		}
	}
}
