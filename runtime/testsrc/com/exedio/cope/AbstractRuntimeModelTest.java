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

import com.exedio.cope.junit.CopeModelTest;
import com.exedio.cope.pattern.MediaPath;
import java.io.File;

public abstract class AbstractRuntimeModelTest extends CopeModelTest
{
	public AbstractRuntimeModelTest(final Model model)
	{
		super(model);
	}

	protected boolean postgresql;
	protected String mediaRootUrl = null;
	private final FileFixture files = new FileFixture();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final String database = model.getConnectProperties().getDialect();

		postgresql = "com.exedio.cope.PostgresqlDialect".equals(database);

		mediaRootUrl = model.getConnectProperties().getMediaRootUrl();

		files.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		files.tearDown();

		mediaRootUrl = null;

		super.tearDown();
	}

	public void assertLocator(
			final MediaPath feature,
			final String path,
			final MediaPath.Locator locator)
	{
		// locator methods must work without transaction
		final Transaction tx = model.leaveTransaction();
		try
		{
			assertSame(feature, locator.getFeature());
			assertEquals(path, locator.getPath());
			assertEquals(mediaRootUrl + path, locator.getURLByConnect());
			assertEquals(path, locator.toString());

			final StringBuilder bf = new StringBuilder();
			locator.appendPath(bf);
			assertEquals(path, bf.toString());

			bf.setLength(0);
			locator.appendURLByConnect(bf);
			assertEquals(mediaRootUrl + path, bf.toString());
		}
		finally
		{
			model.joinTransaction(tx);
		}
	}

	protected final File deleteOnTearDown(final File file)
	{
		return files.deleteOnTearDown(file);
	}
}
