/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;

/**
 * This tests makes sure, that no properties are changed by accident.
 * Adapt if necessary.
 */
public class ConnectPropertiesTest extends TestCase
{
	public void testIt() throws IOException
	{
		final ConnectProperties p = new ConnectProperties(
				loadProperties(getClass().getResourceAsStream("connectPropertiesTest.properties")),
				null);

		for(final Field field : p.getFields())
		{
			assertTrue(field.getKey(), field.isSpecified());
		}

		p.ensureValidity();
	}

	private static Source loadProperties(final InputStream stream) throws IOException
	{
		assertNotNull(stream);
		try
		{
			final java.util.Properties result = new java.util.Properties();
			result.load(stream);
			return Sources.view(result, "test");
		}
		finally
		{
			stream.close();
		}
	}
}
