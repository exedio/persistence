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

package com.exedio.cope;

import com.exedio.cope.junit.CopeAssert;

public class ModelCharSetTest extends CopeAssert
{
	public void testType()
	{
		new Type<>(AnItem.class, AnItem.class, false, "Zack-Zick123", null, false, null, new Features());
		try
		{
			new Type<>(AnItem.class, AnItem.class, false, "Zack.Zick123", null, false, null, new Features());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("id >Zack.Zick123< of type contains illegal character >.< at position 4", e.getMessage());
		}
	}

	public void testFeature()
	{
		final StringField f = new StringField();
		final Features fs = new Features();
		try
		{
			fs.put("Zack.Zick123", f);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name >Zack.Zick123< contains illegal character >.< at position 4", e.getMessage());
		}
	}

	public void testFeatureNaked()
	{
		final Type<AnItem> t = new Type<>(AnItem.class, AnItem.class, false, "Type123", null, false, null, new Features());
		final StringField f = new StringField();
		try
		{
			f.mount(t, "Zack.Zick123", null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name >Zack.Zick123< of feature in type Type123 contains illegal character >.< at position 4", e.getMessage());
		}
	}

	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}
	}
}
