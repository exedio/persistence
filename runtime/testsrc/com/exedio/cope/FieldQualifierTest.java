/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.AttributeEmptyItem;
import com.exedio.cope.testmodel.EmptyItem;


public class FieldQualifierTest extends FieldTest
{
	public void testSomeQualifiedAttribute()
			throws IntegrityViolationException, MandatoryViolationException, LengthViolationException, FinalViolationException
	{
		assertEquals(AttributeEmptyItem.TYPE, AttributeEmptyItem.someQualifiedString.getType());
		final EmptyItem someItem2 = new EmptyItem();
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, "someQualifiedValue");
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		restartTransaction();
		assertEquals("someQualifiedValue", item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));
		item.setSomeQualifiedString(someItem, null);
		assertEquals(null, item.getSomeQualifiedString(someItem));
		assertEquals(null, item.getSomeQualifiedString(someItem2));

		assertDelete(someItem2);
	}

}
