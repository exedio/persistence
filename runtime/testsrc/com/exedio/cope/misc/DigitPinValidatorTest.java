/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.pattern.Hash;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author baumgaertel
 */
public class DigitPinValidatorTest
{
	private DigitPinValidator validator;

	@BeforeEach final void setUp()
	{
		this.validator = new DigitPinValidator(4);
	}

	@Test void testValidate()
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

	@Test void testExceptions()
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
			assertEquals(false, e.wasLimit());
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
			assertEquals(false, e.wasLimit());
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
			assertEquals(false, e.wasLimit());
		}
	}

	@Test void testNewRandomPlainText()
	{
		assertIt(1, asList("0", "1", "2", "3", "4"));
		assertIt(2, asList("01", "23", "45", "67", "89"));
		assertIt(3, asList("012", "345", "678", "901", "234"));
		assertIt(4, asList("0123", "4567", "8901", "2345", "6789"));
	}

	private static void assertIt(final int pinLen, final List<String> expected)
	{
		final SecureRandom random = new SecureRandom() {
			private static final long serialVersionUID = 1l;
			int seq=0;

			// overridden to get pre defined numbers instead of the random ones
			@Override public int nextInt(final int n) {
				assert n==10;
				return (seq++)%n;
			}
		};

		final ArrayList<String> actual = new ArrayList<>();
		final DigitPinValidator pinValidator = new DigitPinValidator(pinLen);
		for (int i=0; i<5; i++)
		{
			final String newPin = pinValidator.newRandomPlainText(random);
			assertTrue(Integer.parseInt(newPin)>=0);
			assertEquals(pinLen, newPin.length());
			actual.add(newPin);
		}
		assertEquals(expected, actual);
	}

	@Test void testToString()
	{
		assertEquals("DigitPin(4)", validator.toString());
	}

	@Test void testConstruction()
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
