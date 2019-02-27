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
			return getDoubleLiteralFor((double)(float)constantValue)+"f";
		else if (constantValue instanceof Boolean)
			return String.valueOf(constantValue);
		else if (constantValue instanceof Character)
			return "'"+constantValue+"'";
		else
			throw new RuntimeException(constantValue.getClass().getName());
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

	private static String hex(final char ch)
	{
		return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
	}

	@SuppressWarnings("HardcodedLineSeparator")
	private static String escapeJavaStyleString(final String str)
	{
		final StringBuilder result = new StringBuilder();
		final int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++)
		{
			final char ch = str.charAt(i);

			// handle unicode
			if (ch > 0xfff)
			{
				result.append("\\u").append(hex(ch));
			}
			else if (ch > 0xff)
			{
				result.append("\\u0").append(hex(ch));
			}
			else if (ch > 0x7f)
			{
				result.append("\\u00").append(hex(ch));
			}
			else if (ch < 32)
			{
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
						if (ch > 0xf)
						{
							result.append("\\u00").append(hex(ch));
						} else
						{
							result.append("\\u000").append(hex(ch));
						}
						break;
				}
			} else
			{
				switch (ch)
				{
					case '"':
						result.append('\\');
						result.append('"');
						break;
					case '\\':
						result.append('\\');
						result.append('\\');
						break;
					default:
						result.append(ch);
						break;
				}
			}
		}
		return result.toString();
	}
}
