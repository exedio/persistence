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
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.Collection;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class SuspicionsLogTest
{
	private final LogRule log = new LogRule(AFeature.class);

	@Test void testIt()
	{
		assertEquals(asList(), AnItem.none.getSuspicions());
		assertEquals(asList("SuspicionOnly"), AnItem.one.getSuspicions());
		assertEquals(asList("SuspicionA", "SuspicionB"), AnItem.two.getSuspicions());

		log.assertEmpty();
		final Type<?> type = newType(AnItem.class);
		log.assertEmpty();

		new Model(type);
		log.assertError("AnItem.one: SuspicionOnly");
		log.assertError("AnItem.two: SuspicionA");
		log.assertError("AnItem.two: SuspicionB");
		log.assertEmpty();
	}

	static final class AFeature extends Feature
	{
		private final String[] suspicions;

		AFeature(final String... suspicions)
		{
			this.suspicions = suspicions.clone();
		}

		@Override
		public Collection<String> getSuspicions()
		{
			return asList(suspicions);
		}

		private static final long serialVersionUID = 1l;
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	static final class AnItem extends Item
	{
		static final AFeature none = new AFeature();
		static final AFeature one = new AFeature("SuspicionOnly");
		static final AFeature two = new AFeature("SuspicionA", "SuspicionB");

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
