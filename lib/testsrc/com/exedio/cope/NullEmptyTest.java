/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

/**
 * Test, whether database converts empty strings to null,
 * and how the framework hides such behaviour from the user.
 * @author Ralf Wiebicke
 */
public class NullEmptyTest extends TestmodelTest
{
	EmptyItem someItem;
	AttributeItem item;
	AttributeItem item2;
	String emptyString;

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("someString", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue2));

		if(model.supportsEmptyStrings())
			emptyString = "";
		else
			emptyString = null;
	}

	public void testNullEmpty()
			throws IntegrityViolationException
	{
		assertEquals(null, item.getSomeString());

		item.setSomeString("");
		assertEquals("", item.getSomeString()); // TODO empty to null conversion must take effect immediately
		restartTransaction();
		assertEquals(emptyString, item.getSomeString());

		item.setSomeString(null);
		assertEquals(null, item.getSomeString());
		restartTransaction();
		assertEquals(null, item.getSomeString());
	}

}
