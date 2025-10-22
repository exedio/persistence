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

import static com.exedio.cope.RuntimeAssert.failingActivator;
import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.UsageEntryPoint;
import java.io.Serial;
import org.junit.jupiter.api.Test;

public class JavaViewInPatternTest
{
	private static class Muster extends Pattern
	{
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		Muster()
		{
			addSourceFeature(new JavaView(), "schau");
		}

		@Serial
		private static final long serialVersionUID = 1l;
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@UsageEntryPoint // accessed by reflection
		static final Muster muster = new Muster();

		@SuppressWarnings("unused") // accessed by reflection
		String getMusterschau()
		{
			return "zack";
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Test void testIt()
	{
		TypesBound.newType(AnItem.class, failingActivator());
	}
}
