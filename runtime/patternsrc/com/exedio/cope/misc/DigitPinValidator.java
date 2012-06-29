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
	private final int min;
	private final int max;

	public DigitPinValidator(int pinLen)
	{
		if (pinLen<1)
			throw new IllegalArgumentException("pinLen must be greater 0");

		if (pinLen>MAX_PIN_LEN)
			throw new IllegalArgumentException("pinLen exceeds limit of max " + MAX_PIN_LEN);

		this.pinLen = pinLen;
		this.min = (int) Math.pow(10, pinLen -1);
		this.max = (int) Math.pow(10, pinLen); // exclusive
	}

	@Override public void validate(String plainText, Item exceptionItem, Hash hash) throws
		Hash.InvalidPlainTextException
	{
		if(plainText==null)
			throw new NullPointerException();

		plainText = plainText.trim();

		if (plainText.length() < pinLen)
			throw new Hash.InvalidPlainTextException("Pin less than " + pinLen + " digits",
				exceptionItem, hash);

		if (plainText.length() > pinLen)
			throw new Hash.InvalidPlainTextException("Pin greater than " + pinLen + " digits",
				exceptionItem, hash);

		// throws an number format exception the text contains others than digits
		try
		{
			Integer.parseInt(plainText);
		}
		catch (NumberFormatException e)
		{
			// use another message than the default one, is displayed in copaiba
			throw new Hash.InvalidPlainTextException("Pin '"+plainText+"' is not a number",
				exceptionItem, hash);
		}
	}

	@Override public String newRandomPlainText(SecureRandom secureRandom)
	{
		int l = 0;

		while (l < min)
			l = Math.abs(secureRandom.nextInt(max));

		return  Integer.toString(l);
	}
}
