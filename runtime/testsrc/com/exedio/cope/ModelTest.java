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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;

import com.exedio.cope.testmodel.Main;

/**
 * Tests the model itself, without creating/using any persistent data.
 * @author Ralf Wiebicke
 */
public class ModelTest extends TestmodelTest
{
	
	public void testSupportsReadCommitted()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		assertEquals(dialect.supportsReadCommitted, model.supportsReadCommitted());
	}
	
	public static final void assertEquals(final String expected, final String actual)
	{
		assertEquals("-----"+expected+"-----"+actual+"-----", expected, actual);
	}
	
	public void testConnect()
	{
		final ConnectProperties defaultProps = getConnectProperties();
		// test duplicate call of connect
		final Date connectDate = model.getConnectDate();
		assertNotNull(connectDate);
		try
		{
			model.connect(defaultProps);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model already been connected", e.getMessage());
		}
		try
		{
			model.connect(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("properties", e.getMessage());
		}
		assertNotSame(connectDate, model.getConnectDate());
		assertEquals(!postgresql, model.supportsSequences());
	}

	public void testModel() throws IOException
	{
		try
		{
			new Model((Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("types", e.getMessage());
		}
		try
		{
			new Model(new Type[]{});
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}

		// TODO put this into an extra test called an extra ant target
		{
			final String prefix = System.getProperty("com.exedio.cope.testprotocol.prefix");
			if(prefix!=null)
			{
				final java.util.Properties databaseInfo = model.getDatabaseInfo();
				final java.util.Properties prefixed = new java.util.Properties();
				final File file = new File(System.getProperty("com.exedio.cope.testprotocol.file"));
				for(Iterator i = databaseInfo.keySet().iterator(); i.hasNext(); )
				{
					final String name = (String)i.next();
					prefixed.setProperty(prefix+'.'+name, databaseInfo.getProperty(name));
				}
				final ConnectProperties p = model.getProperties();
				for(final ConnectProperties.Field field : p.getFields())
				{
					if(field.getDefaultValue()!=null
						&& field!=p.mediaRooturl
						&& !field.hasHiddenValue()
						&& field.isSpecified()
						&& field.getValue()!=null)
						prefixed.setProperty(prefix+".cope."+field.getKey(), field.getValue().toString());
				}
				final PrintStream out = new PrintStream(new FileOutputStream(file, true));
				prefixed.store(out, null);
				out.close();
			}
		}
	}
	
	public void testDisconnect()
	{
		assertWithin(Main.beforeModel, Main.afterModel, model.getInitializeDate());
		
		model.commit();
		final ConnectProperties p = model.getProperties();
		assertNotNull(p);
		
		model.disconnect();
		try
		{
			model.getProperties();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not yet connected, use Model#connect", e.getMessage());
		}
		assertEquals(null, model.getConnectDate());

		try
		{
			model.disconnect();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not yet connected, use Model#connect", e.getMessage());
		}
		assertEquals(null, model.getConnectDate());

		final Date before = new Date();
		model.connect(p);
		final Date after = new Date();
		assertSame(p, model.getProperties());
		assertWithin(before, after, model.getConnectDate());
		model.startTransaction("ModelTest.testDisconnect");
	}
}
