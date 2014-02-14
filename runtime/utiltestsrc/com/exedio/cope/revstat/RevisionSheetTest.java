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

package com.exedio.cope.revstat;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;
import com.exedio.cope.TypeSet;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.AssertionErrorJobContext;
import java.io.File;
import java.util.Iterator;

public class RevisionSheetTest extends CopeAssert
{
	private static final TestRevisionsFactory revisions = new TestRevisionsFactory();

	private static final Model MODEL = new Model(
			revisions,
			new TypeSet[]{RevisionStatistics.types});

	private static final ConnectProperties props = new ConnectProperties(new File("runtime/utiltest.properties"));

	public void testIt()
	{
		revisions.assertEmpty();

		MODEL.connect(props);
		MODEL.tearDownSchema();
		revisions.put(new Revisions(
			new Revision(1, "comment1", "sql 1/0")
		));
		MODEL.createSchema();
		revisions.assertEmpty();

		MODEL.disconnect();
		MODEL.connect(props);
		final String bodyPrefix = "delete from \"CopeRevisionStatistics\" where \"comment\"=";
		revisions.put(new Revisions(
			new Revision(3, "comment3", bodyPrefix + "'sql 3/0'", bodyPrefix + "'sql 3/1'", bodyPrefix + "'sql 3/2'"),
			new Revision(2, "comment2", bodyPrefix + "'sql 2/0'"),
			new Revision(1, "comment1", "sql 1/0")
		));
		MODEL.revise();
		revisions.assertEmpty();

		{
			final Context ctx = new Context();
			RevisionStatistics.write(MODEL, ctx);
			ctx.assertProgress(2);
		}
		{
			final Context ctx = new Context();
			RevisionStatistics.write(MODEL, ctx);
			ctx.assertProgress(0);
		}

		MODEL.startTransaction("RevisionSheetTest");
		final Iterator<CopeRevisionSheet> sheetIterator =
				CopeRevisionSheet.TYPE.search(null, CopeRevisionSheet.TYPE.getThis(), true).iterator();
		{
			final CopeRevisionSheet sheet = sheetIterator.next();
			assertEquals(2, sheet.getNumber());
			assertNotNull(sheet.getDate());
			assertEquals("comment2", sheet.getComment());
			final Iterator<CopeRevisionSheetBody> bodyIterator = sheet.getBody().iterator();
			{
				final CopeRevisionSheetBody body = bodyIterator.next();
				assertEquals(0, body.getBodyNumber());
				assertEquals(bodyPrefix + "'sql 2/0'", body.getSQL());
			}
			assertFalse(bodyIterator.hasNext());
		}
		{
			final CopeRevisionSheet sheet = sheetIterator.next();
			assertEquals(3, sheet.getNumber());
			assertNotNull(sheet.getDate());
			assertEquals("comment3", sheet.getComment());
			final Iterator<CopeRevisionSheetBody> bodyIterator = sheet.getBody().iterator();
			{
				final CopeRevisionSheetBody body = bodyIterator.next();
				assertEquals(0, body.getBodyNumber());
				assertEquals(bodyPrefix + "'sql 3/0'", body.getSQL());
			}
			{
				final CopeRevisionSheetBody body = bodyIterator.next();
				assertEquals(1, body.getBodyNumber());
				assertEquals(bodyPrefix + "'sql 3/1'", body.getSQL());
			}
			{
				final CopeRevisionSheetBody body = bodyIterator.next();
				assertEquals(2, body.getBodyNumber());
				assertEquals(bodyPrefix + "'sql 3/2'", body.getSQL());
			}
			assertFalse(bodyIterator.hasNext());
		}
		assertFalse(sheetIterator.hasNext());
	}

	@Override
	protected void tearDown() throws Exception
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.disconnect();
		super.tearDown();
	}

	private static final class TestRevisionsFactory implements Revisions.Factory
	{
		private Revisions revisions = null;

		TestRevisionsFactory()
		{
			// make non-private
		}

		void put(final Revisions revisions)
		{
			assertNotNull(revisions);
			assertNull(this.revisions);
			this.revisions = revisions;
		}

		void assertEmpty()
		{
			assertNull(revisions);
		}

		public Revisions create(final Context ctx)
		{
			assertNotNull(ctx);
			assertNotNull(ctx.getEnvironment());
			assertNotNull(this.revisions);
			final Revisions revisions = this.revisions;
			this.revisions = null;
			return revisions;
		}
	}

	private static final class Context extends AssertionErrorJobContext
	{
		private int progress = 0;

		Context()
		{
		}

		@Override
		public void stopIfRequested()
		{
			// do nothing
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}

		void assertProgress(final int expected)
		{
			assertEquals(expected, progress);
		}
	}
}
