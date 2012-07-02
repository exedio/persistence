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

import com.exedio.cope.pattern.Hash;
import junit.framework.TestCase;

import java.security.SecureRandom;

/**
 * @author baumgaertel
 */
public class DigitPinValidatorTest extends TestCase
{
	private DigitPinValidator validator;

	public void setUp() throws Exception
	{
		super.setUp();
		this.validator = new DigitPinValidator(4);
	}

	public void testValidate() throws Exception
	{
		// test some valid pins
		for (final String validPin : new String[] {"1233", "0000", "9999", "8376"})
		{
			validator.validate(validPin, null, null);
		}

		// test some invalid pins
		for (final String invalidPin : new String[] {"", "1", "12", "384e", "39394", "999", "000", "0.0", "00.0", "000."})
		{
			try
			{
				validator.validate(invalidPin, null, null);
				fail();
			}
			catch (final Hash.InvalidPlainTextException e)
			{
				assertNotNull(e.getMessage());
			}
		}
	}

	public void testExceptions()
	{
		final DigitPinValidator validator = new DigitPinValidator(3);
		try
		{
			validator.validate("12", null, null);
			fail();
		}
		catch (final Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin less than 3 digits for null", e.getMessage());
			assertEquals("Pin less than 3 digits", e.getMessage(false));
			assertEquals("12", e.getPlainText());
		}
		try
		{
			validator.validate("1234", null, null);
			fail();
		}
		catch (final Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin greater than 3 digits", e.getMessage(false));
			assertEquals("1234", e.getPlainText());
		}
		try
		{
			validator.validate("12a", null, null);
			fail();
		}
		catch (final Hash.InvalidPlainTextException e)
		{
			assertEquals("Pin is not a number", e.getMessage(false));
			assertEquals("12a", e.getPlainText());
		}
	}

	public void testNewRandomPlainText() throws Exception
	{
		final SecureRandom random = new SecureRandom();
		for (int pinLen = 1; pinLen < 6; pinLen++)
		{
			final DigitPinValidator pinValidator = new DigitPinValidator(pinLen);
			for (int i=0; i<1000; i++)
			{
				final String newPin = pinValidator.newRandomPlainText(random);
				assertEquals(pinLen, newPin.length());
			}
		}
	}

	public void testConstruction()
	{
		try
		{
			new DigitPinValidator(0);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("pinLen must be greater 0", e.getMessage());
		}

		try
		{
			new DigitPinValidator(24);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("pinLen exceeds limit of max 10", e.getMessage());
		}
	}
}
