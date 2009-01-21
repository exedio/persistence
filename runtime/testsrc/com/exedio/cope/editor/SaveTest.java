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

public class SaveTest extends AbstractRuntimeTest
{
	public SaveTest()
	{
		super(DraftTest.MODEL);
	}
	
	static final Session SESSION = new Session()
	{
		public String getName()
		{
			throw new RuntimeException();
		}
	};
	
	DraftedItem i;
	Draft d;
	Anchor anchor;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		i = deleteOnTearDown(new DraftedItem());
		d = deleteOnTearDown(new Draft("user", "name", "comment"));
		i.setString("oldString1");
		anchor = new Anchor("anchorUser", SESSION, "anchorSessionName");
		anchor.modify("newString1", DraftedItem.string, i);
	}
	
	public void testDraft()
	{
		assertEquals(list(), d.getItems());
		new TargetDraft(d).save(anchor);
		assertEquals(1, d.getItemsCount());
		final DraftItem di1 = d.getItems().get(0);
		assertEquals("DraftedItem.string", di1.getFeature());
		assertEquals(i.getCopeID(), di1.getItem());
		assertEquals("oldString1", di1.getOldValue());
		assertEquals("newString1", di1.getNewValue());
	}
	
	public void testLive()
	{
		assertEquals("oldString1", i.getString());
		TargetLive.INSTANCE.save(anchor);
		assertEquals("newString1", i.getString());
	}
}
