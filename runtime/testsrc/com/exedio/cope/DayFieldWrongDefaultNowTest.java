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

package com.exedio.cope;

import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DayFieldWrongDefaultNowTest
{
	private final LogRule log = new LogRule(DayField.class);

	@Test void testIt()
	{
		newType(AnItem.class);
		log.assertError(
				"Very probably you called \"DayField.defaultTo(new Day())\" on field AnItem.wrong. " +
				"This will not work as expected, use \"defaultToNow()\" instead.");
		log.assertEmpty();
	}

	@WrapperIgnore
	static final class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}

		@SuppressWarnings("unused") // OK: test bad API usage
		static final DayField wrong = new DayField().defaultTo(new Day(TimeZone.getDefault()));
		@SuppressWarnings("unused") // OK: test bad API usage
		static final DayField ok = new DayField().defaultTo(new Day(2005, 10, 10));
	}
}
