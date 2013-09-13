/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.misc;

import com.exedio.cope.Item;
import com.exedio.cope.pattern.Hash;
import java.security.SecureRandom;

/**
 * Allow only digits as pin, pin len can be specified, reference implementation
 *
 * @author baumgaertel
 */
public final class DigitPinValidator extends Hash.PlainTextValidator
{
	private static final int MAX_PIN_LEN = Integer.toString(Integer.MAX_VALUE).length();

	private final int pinLen;

	public DigitPinValidator(final int pinLen)
	{
		if (pinLen<1)
			throw new IllegalArgumentException("pinLen must be greater 0");

		if (pinLen>MAX_PIN_LEN)
			throw new IllegalArgumentException("pinLen exceeds limit of max " + MAX_PIN_LEN);

		this.pinLen = pinLen;
	}

	@Override public void validate(String pinString, final Item exceptionItem, final Hash hash) throws
		Hash.InvalidPlainTextException
	{
		if(pinString==null)
			throw new NullPointerException();

		pinString = pinString.trim();

		if (pinString.length() < pinLen)
			throw new Hash.InvalidPlainTextException("Pin less than " + pinLen + " digits",
				pinString, exceptionItem, hash);

		if (pinString.length() > pinLen)
			throw new Hash.InvalidPlainTextException("Pin greater than " + pinLen + " digits",
				pinString, exceptionItem, hash);

		for (final char c : pinString.toCharArray())
		{
			if (c < '0' || c > '9')
				throw new Hash.InvalidPlainTextException("Pin is not a number",	pinString, exceptionItem, hash);
		}
	}

	@Override public String newRandomPlainText(final SecureRandom secureRandom)
	{
		final char[] result = new char[pinLen];
		for(int i = 0; i<pinLen; i++)
		{
			result[i] = (char)('0' + secureRandom.nextInt(10));
			assert result[i]>='0' : result[i];
			assert result[i]<='9' : result[i];
		}
		return new String(result);
	}
}
