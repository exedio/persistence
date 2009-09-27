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

public class PreCreateTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(PreCreateSuperItem.TYPE, PreCreateItem.TYPE);
	
	public PreCreateTest()
	{
		super(MODEL);
	}
	
	public void test()
	{
		{
			final PreCreateItem item = deleteOnTearDown(new PreCreateItem("normal"));
			assertEquals("normal.preCreate.preCreateSuper.postCreate", item.getText());
		}
		{
			final PreCreateItem item = deleteOnTearDown(new PreCreateItem(PreCreateSuperItem.text.map("generic")));
			assertEquals("generic.preCreate.preCreateSuper.postCreate", item.getText());
		}
		{
			final PreCreateItem item = deleteOnTearDown(PreCreateItem.TYPE.newItem(PreCreateSuperItem.text.map("type")));
			assertEquals("type.preCreate.preCreateSuper.postCreate", item.getText());
		}
		// super
		{
			final PreCreateSuperItem item = deleteOnTearDown(new PreCreateSuperItem("normal"));
			assertEquals("normal.preCreateSuper.postCreateSuper", item.getText());
		}
		{
			final PreCreateSuperItem item = deleteOnTearDown(new PreCreateSuperItem(PreCreateSuperItem.text.map("generic")));
			assertEquals("generic.preCreateSuper.postCreateSuper", item.getText());
		}
		{
			final PreCreateSuperItem item = deleteOnTearDown(PreCreateSuperItem.TYPE.newItem(PreCreateSuperItem.text.map("type")));
			assertEquals("type.preCreateSuper.postCreateSuper", item.getText());
		}
	}
}
