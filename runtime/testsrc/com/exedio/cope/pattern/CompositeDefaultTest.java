/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.CompositeDefaultItem.field;
import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.CheckViolationException;
import com.exedio.cope.Model;

public class CompositeDefaultTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(CompositeDefaultItem.TYPE);

	static
	{
		MODEL.enableSerialization(CompositeDefaultTest.class, "MODEL");
	}

	public CompositeDefaultTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		try
		{
			new CompositeDefaultItem();
			fail();
		}
		catch(final CheckViolationException e)
		{
			// TODO this is a bug
			assertSame(field.getUnison(), e.getFeature());
			assertSame(null, e.getItem());
		}

		final CompositeDefaultItem isNull =
			deleteOnTearDown(new CompositeDefaultItem(null));
		assertNull("normalValue", isNull.getField());

		final CompositeDefaultItem isNotNull =
			deleteOnTearDown(new CompositeDefaultItem(new CompositeDefaultValue("normalValue")));
		assertEquals("normalValue", isNotNull.getField().getNormal());
		assertEquals(5, isNotNull.getField().getDeflt());
	}
}
