/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class CreateTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(CreateSuperItem.TYPE, CreateItem.TYPE);

	public CreateTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		{
			final CreateItem item = new CreateItem("normal");
			assertEquals("normal.preCreate.preCreateSuper.postCreate", item.getText());
		}
		{
			final CreateItem item = new CreateItem(CreateSuperItem.text.map("generic"));
			assertEquals("generic.preCreate.preCreateSuper.postCreate", item.getText());
		}
		{
			final CreateItem item = CreateItem.TYPE.newItem(CreateSuperItem.text.map("type"));
			assertEquals("type.preCreate.preCreateSuper.postCreate", item.getText());
		}
		try
		{
			new CreateItem("fail");
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(CreateSuperItem.text, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			new CreateItem(CreateSuperItem.text.map("fail"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(CreateSuperItem.text, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			CreateItem.TYPE.newItem(CreateSuperItem.text.map("fail"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(CreateSuperItem.text, e.getFeature());
			assertEquals(null, e.getItem());
		}
		// super
		{
			final CreateSuperItem item = new CreateSuperItem("normal");
			assertEquals("normal.preCreateSuper.postCreateSuper", item.getText());
		}
		{
			final CreateSuperItem item = new CreateSuperItem(CreateSuperItem.text.map("generic"));
			assertEquals("generic.preCreateSuper.postCreateSuper", item.getText());
		}
		{
			final CreateSuperItem item = CreateSuperItem.TYPE.newItem(CreateSuperItem.text.map("type"));
			assertEquals("type.preCreateSuper.postCreateSuper", item.getText());
		}
		try
		{
			new CreateSuperItem("fail");
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(CreateSuperItem.text, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			new CreateSuperItem(CreateSuperItem.text.map("fail"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(CreateSuperItem.text, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			CreateSuperItem.TYPE.newItem(CreateSuperItem.text.map("fail"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(CreateSuperItem.text, e.getFeature());
			assertEquals(null, e.getItem());
		}
	}
}
