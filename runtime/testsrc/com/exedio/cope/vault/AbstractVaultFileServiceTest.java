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

import static com.exedio.cope.util.StrictFile.mkdir;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.cope.vaulttest.VaultServiceTest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public abstract class AbstractVaultFileServiceTest extends VaultServiceTest
{
	@Override
	protected final Class<? extends VaultService> getServiceClass()
	{
		return VaultFileService.class;
	}

	private File root;

	@Rule
	public final TemporaryFolder files = new TemporaryFolder();

	@Override
	protected Properties getServiceProperties() throws IOException
	{
		root = files.newFolder();
		final Properties result = new Properties();
		result.setProperty("root", root.getAbsolutePath());
		result.setProperty("bufferSize", "2");
		return result;
	}

	final File getRoot()
	{
		return root;
	}

	@Before
	public final void setUpAbstractVaultFileServiceTest()
	{
		mkdir(((VaultFileService)getService()).tempDir);
	}

	@SuppressWarnings("JUnit3StyleTestMethodInJUnit4Class") // bug in inspection
	@Test public final void testToString()
	{
		assertEquals("VaultFileService:" + root.getAbsolutePath(), getService().toString());
	}

	protected static final void assertContains(final File directory, final File... content)
	{
		final File[] actual = directory.listFiles();
		assertNotNull(actual);
		assertEquals(
				new TreeSet<>(asList(content)),
				new TreeSet<>(asList(actual)));
	}
}
