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

package com.exedio.cope.vault;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.util.Sources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.Properties;
import org.junit.Test;

@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
public class VaultFileServiceErrorTest
{
	@Test public void ok()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.directory.length", "31");
		source.setProperty("service.temp", "t");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		final VaultFileService service = (VaultFileService)properties.newService();

		assertEquals(31, service.directoryLength);
		assertEquals(new File("rootDir/t"), service.tempDir);
	}

	@Test public void directoryLengthTooLong()
	{
		final Properties source = new Properties();
		source.setProperty("algorithm", "MD5");
		source.setProperty("service", VaultFileService.class.getName());
		source.setProperty("service.root", "rootDir");
		source.setProperty("service.directory.length", "32");

		final VaultProperties properties = VaultProperties.factory().create(Sources.view(source, "DESC"));
		try
		{
			properties.newService();
			fail();
		}
		catch(final RuntimeException e2)
		{
			final IllegalArgumentException e = (IllegalArgumentException)e2.getCause().getCause();
			assertEquals(
					"directory.length must be less the length of algorithm, but was 32>=32",
					e.getMessage());
		}
	}
}
