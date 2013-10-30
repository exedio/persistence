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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.SetValueUtil.getFirst;

import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import java.util.ArrayList;

public class SetValueUtilTest extends CopeAssert
{
	public void testGetFirst()
	{
		final StringField f1 = new StringField();
		final StringField f2 = new StringField();
		final ArrayList<SetValue<?>> l = new ArrayList<SetValue<?>>();

		l.add(f1.map("value1a"));
		assertEquals("value1a", getFirst(l, f1));
		assertEquals(null, getFirst(l, f2));

		l.add(f1.map("value1b"));
		assertEquals("value1a", getFirst(l, f1));
		assertEquals(null, getFirst(l, f2));

		l.add(f2.map("value2"));
		assertEquals("value1a", getFirst(l, f1));
		assertEquals("value2", getFirst(l, f2));
	}
}
