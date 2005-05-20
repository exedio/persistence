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

import java.util.Collection;

import com.exedio.cope.search.Condition;
import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;


public abstract class AttributeTest extends DatabaseLibTest
{

	protected EmptyItem someItem, someItem2;
	protected AttributeItem item;
	protected AttributeItem item2;

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(someItem2 = new EmptyItem());
		deleteOnTearDown(item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("someString2", 6, 7l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue2));
	}
	
	protected static Collection search(final ObjectAttribute selectAttribute)
	{
		return search(selectAttribute, null);
	}
	
	protected static Collection search(final ObjectAttribute selectAttribute, final Condition condition)
	{
		return new Query(selectAttribute, condition).search();
	}
	
}