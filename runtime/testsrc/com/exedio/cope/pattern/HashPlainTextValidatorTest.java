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

package com.exedio.cope.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Item;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

public class HashPlainTextValidatorTest
{
	@Test void testIt()
	{
		final Hash vanillaHash = new Hash(new ConstructorHashAlgorithm("---"));
		assertSame(null, vanillaHash.getPlainTextValidator());
		try
		{
			vanillaHash.validate(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("validator", e.getMessage());
		}

		final Hash.PlainTextValidator validator = new Hash.PlainTextValidator()
		{
			@Override
			protected void validate(final String plainText, final Item exceptionItem, final Hash hash)
			{
				throw new RuntimeException();
			}
			@Override
			@Deprecated
			protected String newRandomPlainText(final SecureRandom secureRandom)
			{
				throw new RuntimeException();
			}
		};
		final Hash hash = vanillaHash.validate(validator);
		assertEquals(validator, hash.getPlainTextValidator());
	}
}
