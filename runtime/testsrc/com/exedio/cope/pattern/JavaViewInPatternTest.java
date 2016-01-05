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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.TypesBound;
import org.junit.Test;

public class JavaViewInPatternTest
{
	private static class Muster extends Pattern
	{
		Muster()
		{
			addSource(new JavaView(), "schau");
		}

		private static final long serialVersionUID = 1l;
	}

	private static final class AnItem extends Item
	{
		@SuppressWarnings("unused") // accessed by reflection
		static final Muster muster = new Muster();

		@SuppressWarnings({"unused", "static-method"}) // accessed by reflection
		String getMusterschau()
		{
			return "zack";
		}

		private AnItem(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	@Test public void testIt()
	{
		TypesBound.newType(AnItem.class);
	}
}
