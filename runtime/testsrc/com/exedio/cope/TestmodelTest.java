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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.Main;

public abstract class TestmodelTest extends AbstractRuntimeTest
{
	public static final Type[] modelTypes = Main.modelTypes;
	
	public TestmodelTest()
	{
		super(Main.model);
	}
	
	protected void assertDataMime(final AttributeItem item,
											final String contentType,
											final byte[] data,
											final String url)
	{
		try
		{
			item.setSomeData(new ByteArrayInputStream(data), contentType);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		final String prefix = model.getConnectProperties().getMediaRootUrl() + "AttributeItem/someData/";
		final String expectedURL = prefix + item.getCopeID() + (url!=null ? ('.' + url) : "");
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeDataURL());
		assertEquals(expectedURL, item.getSomeDataURL());
		assertData(data, item.getSomeDataBody());
		assertEquals(contentType, item.getSomeDataContentType());
	}

	protected void assertNotEquals(final Item item1, final Item item2)
	{
		assertFalse(item1.equals(item2));
		assertFalse(item2.equals(item1));
		assertFalse(item1.getCopeID().equals(item2.getCopeID()));
		assertFalse(item1.hashCode()==item2.hashCode());
	}
	
}
