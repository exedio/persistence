/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

/**
 * Test for bug 30
 *
 * @author Ralf Wiebicke
 */
public class UniqueHierarchyTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(UniqueHierarchySuperItem.TYPE, UniqueHierarchySubItem.TYPE);
	
	public UniqueHierarchyTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		assertEquals(list(), UniqueHierarchySuperItem.TYPE.search());
		assertEquals(list(), UniqueHierarchySubItem.TYPE.search());
		
		final UniqueHierarchySubItem item = deleteOnTearDown(new UniqueHierarchySubItem("super1", "sub1"));
		assertEquals(list(item), UniqueHierarchySuperItem.TYPE.search());
		assertEquals(list(item), UniqueHierarchySubItem.TYPE.search());

		try
		{
			new UniqueHierarchySubItem("super2", "sub1");
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item.subField.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(list(item), UniqueHierarchySuperItem.TYPE.search());
		assertEquals(list(item), UniqueHierarchySubItem.TYPE.search());

		final UniqueHierarchySubItem item2 = new UniqueHierarchySubItem("super2", "sub2");
		deleteOnTearDown(item2);
		assertEquals(list(item, item2), UniqueHierarchySuperItem.TYPE.search(null, UniqueHierarchySuperItem.TYPE.getThis(), true));
		assertEquals(list(item, item2), UniqueHierarchySubItem.TYPE.search(null, item.TYPE.getThis(), true));
		
		try
		{
			item2.setSubField("sub1");
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item.subField.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(item2, e.getItem());
		}
		assertEquals("sub2", item2.getSubField());
		
		// test setting the value already set
		item.setSubField("sub1");
		assertEquals("sub1", item.getSubField());
	}
}
