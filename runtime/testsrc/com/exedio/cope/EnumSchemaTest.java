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

package com.exedio.cope;

import com.exedio.cope.junit.CopeAssert;
import static com.exedio.cope.SchemaInfo.getColumnValue;

public class EnumSchemaTest extends CopeAssert
{
	public void testNormal()
	{
		final EnumField<Normal> normal = Item.newEnumField(Normal.class);
		assertEquals(10, getColumnValue(normal, Normal.Eins));
		assertEquals(20, getColumnValue(normal, Normal.Zwei));
		assertEquals(30, getColumnValue(normal, Normal.Drei));
	}
	
	enum Normal
	{
		Eins, Zwei, Drei;
	}
	
	public void testNormal2()
	{
		final EnumField<Normal2> normal = Item.newEnumField(Normal2.class);
		assertEquals(10, getColumnValue(normal, Normal2.Eins));
		assertEquals(20, getColumnValue(normal, Normal2.Zwei));

		try
		{
			getColumnValue(normal, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
	enum Normal2
	{
		Eins, Zwei;
	}
	
	@SuppressWarnings({"unchecked","cast"}) // OK: test bad api usage
	public void testUnchecked()
	{
		final EnumField<Normal2> normal = Item.newEnumField(Normal2.class);
		try
		{
			getColumnValue(((EnumField)normal), (Enum)Normal.Eins);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expected " + Normal2.class.getName() + ", but was a " + Normal.class.getName(), e.getMessage());
		}
	}
}
