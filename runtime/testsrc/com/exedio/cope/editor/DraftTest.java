/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.editor;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;

public class DraftTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(DraftedItem.TYPE, Draft.TYPE, DraftItem.TYPE);
	
	public DraftTest()
	{
		super(MODEL);
	}
	
	DraftedItem i1;
	DraftedItem i2;
	Draft dn;
	Draft d;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		i1 = deleteOnTearDown(new DraftedItem());
		i2 = deleteOnTearDown(new DraftedItem());
		dn = deleteOnTearDown(new Draft("userNull", null, "commentNull"));
		d  = deleteOnTearDown(new Draft("user", "name", "comment"));
	}
	
	public void testIt()
	{
		assertEquals("userNull", dn.getAuthor());
		assertEquals("commentNull", dn.getComment());
		assertEquals("userNull - commentNull", dn.getDropDownSummary());
		assertEquals(list(), dn.getItems());
		assertEquals(0, dn.getItemsCount());
		
		assertEquals("name", d.getAuthor());
		assertEquals("comment", d.getComment());
		assertEquals("name - comment", d.getDropDownSummary());
		assertEquals(list(), d.getItems());
		assertEquals(0, d.getItemsCount());
		
		i1.setString("oldString1");
		final DraftItem d0 = d.addItem(DraftedItem.string, i1, "newString1");
		assertEquals(list(d0), d.getItems());
		assertEquals(1, d.getItemsCount());
		assertEquals("newString1", new TargetDraft(d).get(DraftedItem.string, i1));
		assertEquals(null, new TargetDraft(d).get(DraftedItem.string2, i1));
		assertEquals(null, new TargetDraft(d).get(DraftedItem.string, i2));
		assertEquals(d, d0.getParent());
		assertEquals(0, d0.getPosition());
		assertEquals("DraftedItem.string", d0.getFeature());
		assertEquals(i1.getCopeID(), d0.getItem());
		assertEquals("oldString1", d0.getOldValue());
		assertEquals("newString1", d0.getNewValue());
		
		i1.setString("oldString2");
		assertSame(d0, d.addItem(DraftedItem.string, i1, "newString2"));
		assertEquals(list(d0), d.getItems());
		assertEquals(1, d.getItemsCount());
		assertEquals(d, d0.getParent());
		assertEquals(0, d0.getPosition());
		assertEquals("DraftedItem.string", d0.getFeature());
		assertEquals(i1.getCopeID(), d0.getItem());
		assertEquals("oldString1", d0.getOldValue());
		assertEquals("newString2", d0.getNewValue());
		
		i2.setString("oldString3");
		final DraftItem d1 = d.addItem(DraftedItem.string, i2, "newString3");
		assertEquals(list(d0, d1), d.getItems());
		assertEquals(2, d.getItemsCount());
		assertEquals(d, d1.getParent());
		assertEquals(1, d1.getPosition());
		assertEquals("DraftedItem.string", d1.getFeature());
		assertEquals(i2.getCopeID(), d1.getItem());
		assertEquals("oldString3", d1.getOldValue());
		assertEquals("newString3", d1.getNewValue());
	}
}
