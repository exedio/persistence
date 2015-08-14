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

import com.exedio.cope.EnumField;
import junit.framework.TestCase;

public class MoneyFieldUtilTest extends TestCase
{
	public void testFixedCurrencyNull()
	{
		try
		{
			MoneyField.fixed(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("currency", e.getMessage());
		}
	}

	public void testSharedCurrencyNull()
	{
		try
		{
			MoneyField.shared(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("currency", e.getMessage());
		}
	}

	enum CurrencyEnum implements Money.Currency
	{
		// empty enum
	}

	public void testSharedCurrencyOptional()
	{
		try
		{
			MoneyField.shared(EnumField.create(CurrencyEnum.class).optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("currency must be mandatory", e.getMessage());
		}
	}

	public void testExclusiveCurrencyNull()
	{
		try
		{
			MoneyField.exclusive(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("currency", e.getMessage());
		}
	}
}
