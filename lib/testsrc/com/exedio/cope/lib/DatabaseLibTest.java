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

package com.exedio.cope.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.exedio.cope.testmodel.AttributeItem;

/**
 * An abstract test class for tests creating/using some persistent data.
 * @author ralf.wiebicke@exedio.com
 */
public abstract class DatabaseLibTest extends AbstractLibTest
{
	protected boolean hsqldb;
	protected boolean mysql;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		hsqldb = model.getDatabase().hsqldb;
		mysql  = model.getDatabase().mysql;
	}

	protected InputStream stream(byte[] data)
	{
		return new ByteArrayInputStream(data);
	}
	
	protected void assertData(final byte[] expectedData, final InputStream actualData)
	{
		try
		{
			final byte[] actualDataArray = new byte[2*expectedData.length];
			final int actualLength = actualData.read(actualDataArray);
			actualData.close();
			assertEquals(expectedData.length, actualLength);
			for(int i = 0; i<actualLength; i++)
				assertEquals(expectedData[i], actualDataArray[i]);
		}
		catch(IOException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	protected void assertMediaMime(final AttributeItem item,
											final String mimeMajor,
											final String mimeMinor,
											final byte[] data,
											final String url)
	{
		try
		{
			item.setSomeDataData(new ByteArrayInputStream(data), mimeMajor, mimeMinor);
		}
		catch(IOException e)
		{
			throw new NestingRuntimeException(e);
		}
		final String prefix = "data/AttributeItem/someData/";
		final String pkString = pkString(item);
		final String expectedURL = prefix+pkString+'.'+url;
		final String expectedURLSomeVariant = prefix+"SomeVariant/"+pkString+'.'+url;
		//System.out.println(expectedURL);
		//System.out.println(item.getSomeMediaURL());
		assertEquals(expectedURL, item.getSomeDataURL());
		assertEquals(expectedURLSomeVariant, item.getSomeDataURLSomeVariant());
		//System.out.println(expectedURLSomeVariant);
		//System.out.println(item.getSomeMediaURL());
		assertData(data, item.getSomeDataData());
		assertEquals(mimeMajor, item.getSomeDataMimeMajor());
		assertEquals(mimeMinor, item.getSomeDataMimeMinor());
	}

	protected void assertNotEquals(final Item item1, final Item item2)
	{
		assertFalse(item1.equals(item2));
		assertFalse(item2.equals(item1));
		assertFalse(item1.getCopeID().equals(item2.getCopeID()));
		assertFalse(item1.hashCode()==item2.hashCode());
	}
	
	protected void assertID(final int id, final Item item)
	{
		assertTrue(item.getCopeID()+"/"+id, item.getCopeID().endsWith("."+id));
	}

	protected void assertDelete(final Item item)
			throws IntegrityViolationException
	{
		assertTrue(!item.isCopeItemDeleted());
		item.deleteCopeItem();
		assertTrue(item.isCopeItemDeleted());
	}

}
