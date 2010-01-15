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
import java.util.Iterator;

public class ProtocolWriter extends TestmodelTest
{
	public void testProtocol() throws IOException
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
