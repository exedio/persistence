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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.util.Properties.Field;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Test;

@ProtocolWriterTag
public class ProtocolWriter extends TestWithEnvironment
{
	public ProtocolWriter()
	{
		super(CompareConditionTest.MODEL);
	}

	@Test void testProtocol() throws IOException
	{
		final String prefix = System.getProperty("com.exedio.cope.testprotocol.prefix");
		assertNotNull(prefix);

		final Properties databaseInfo = model.getEnvironmentInfo().asProperties();
		final Properties prefixed = new Properties();
		final File file = new File(System.getProperty("com.exedio.cope.testprotocol.file"));
		for(final Object nameObject : databaseInfo.keySet())
		{
			final String name = (String)nameObject;
			prefixed.setProperty(prefix+'.'+name, databaseInfo.getProperty(name));
		}
		final ConnectProperties p = model.getConnectProperties();
		for(final Field<?> field : p.getFields())
		{
			if(field.getDefaultValue()!=null
				&& !field.hasHiddenValue()
				&& field.isSpecified())
				prefixed.setProperty(prefix+".cope."+field.getKey(), field.getValue().toString());
		}
		try(FileOutputStream out = new FileOutputStream(file, true))
		{
			prefixed.store(out, null);
		}
	}
}
