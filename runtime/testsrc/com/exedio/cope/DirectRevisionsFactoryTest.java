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

import static com.exedio.cope.misc.DirectRevisionsFactory.make;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.misc.DirectRevisionsFactory;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class DirectRevisionsFactoryTest
{
	@Deprecated // OK: tests deprecated API
	@Test void testIt() throws SQLException
	{
		assertEquals(null, make(null));

		final Revisions r = new Revisions(0);
		final DirectRevisionsFactory f = make(r);
		assertSame(r, f.create(new Revisions.Factory.Context(new EnvironmentInfo(
				null,
				"getCatalog",
				new VersionDatabaseMetaData(5, 3, 6, 2)))));

		try
		{
			f.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}
