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
	private final int max;

	public DigitPinValidator(int pinLen)
	{
		if (pinLen<1)
			throw new IllegalArgumentException("pinLen must be greater 0");

		if (pinLen>MAX_PIN_LEN)
			throw new IllegalArgumentException("pinLen exceeds limit of max " + MAX_PIN_LEN);

		this.pinLen = pinLen;
		this.max = (int) Math.pow(10, pinLen); // exclusive
	}

	@Override public void validate(String pinString, Item exceptionItem, Hash hash) throws
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

		for (char c : pinString.toCharArray())
		{
			if (!Character.isDigit(c))
				throw new Hash.InvalidPlainTextException("Pin is not a number",	pinString, exceptionItem, hash);
		}
	}

	@Override public String newRandomPlainText(SecureRandom secureRandom)
	{
		StringBuilder s = new StringBuilder( Math.abs(secureRandom.nextInt(max) ));

		while (s.length() < pinLen)
			s.insert(0, '0');

		return s.toString();
	}
}
