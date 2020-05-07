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

package com.exedio.cope.revstat;

import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.AssertionErrorJobContext;
import java.time.Duration;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class RevisionStatisticsTest
{
	private static final TestRevisionsFactory revisions = new TestRevisionsFactory();

	static final Model MODEL = Model.builder().
			add(revisions).
			add(RevisionStatistics.types).
			add(RevisionStatisticsItem.TYPE).
			build();

	private static final ConnectProperties props = ConnectProperties.create(TestSources.minimal());

	@Test void testIt()
	{
		revisions.assertEmpty();

		MODEL.connect(props);
		MODEL.tearDownSchema();
		revisions.put(new Revisions(
			new Revision(1, "comment1", "sql 1/0")
		));
		setupSchemaMinimal(MODEL);
		revisions.assertEmpty();

		MODEL.disconnect();
		MODEL.connect(props);
		final String bodyPrefix = "delete from \"RevisionStatisticsItem\" where \"field\"=";
		revisions.put(new Revisions(
			new Revision(3, "comment3", bodyPrefix + "'sql 3/0' or \"num\"=0", bodyPrefix + "'sql 3/1' or \"num\"=1", bodyPrefix + "'sql 3/2' or \"num\"=2"),
			new Revision(2, "comment2", bodyPrefix + "'sql 2/0' or \"num\"=3"),
			new Revision(1, "comment1", "sql 1/0")
		));
		MODEL.startTransaction("RevisionSheetTest");
		new RevisionStatisticsItem(1);
		new RevisionStatisticsItem(2);
		new RevisionStatisticsItem(2);
		new RevisionStatisticsItem(3);
		new RevisionStatisticsItem(3);
		new RevisionStatisticsItem(3);
		MODEL.commit();
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
		final Iterator<Revstat> sheetIterator =
				Revstat.TYPE.search(null, Revstat.TYPE.getThis(), true).iterator();
		{
			final Revstat sheet = sheetIterator.next();
			assertEquals(2, sheet.getNumber());
			assertNotNull(sheet.getDate());
			assertEquals(1, sheet.getSize());
			assertEquals(3, sheet.getRows());
			assertEquals("comment2", sheet.getComment());
			final Iterator<RevstatBody> bodyIterator = sheet.getBody().iterator();
			{
				final RevstatBody body = bodyIterator.next();
				assertEquals(0, body.getBodyNumber());
				assertEquals(3, body.getRows());
				assertEquals(bodyPrefix + "'sql 2/0' or \"num\"=3", body.getSQL());
			}
			assertFalse(bodyIterator.hasNext());
		}
		{
			final Revstat sheet = sheetIterator.next();
			assertEquals(3, sheet.getNumber());
			assertNotNull(sheet.getDate());
			assertEquals(3, sheet.getSize());
			assertEquals(3, sheet.getRows());
			assertEquals("comment3", sheet.getComment());
			final Iterator<RevstatBody> bodyIterator = sheet.getBody().iterator();
			{
				final RevstatBody body = bodyIterator.next();
				assertEquals(0, body.getBodyNumber());
				assertEquals(0, body.getRows());
				assertEquals(bodyPrefix + "'sql 3/0' or \"num\"=0", body.getSQL());
			}
			{
				final RevstatBody body = bodyIterator.next();
				assertEquals(1, body.getBodyNumber());
				assertEquals(1, body.getRows());
				assertEquals(bodyPrefix + "'sql 3/1' or \"num\"=1", body.getSQL());
			}
			{
				final RevstatBody body = bodyIterator.next();
				assertEquals(2, body.getBodyNumber());
				assertEquals(2, body.getRows());
				assertEquals(bodyPrefix + "'sql 3/2' or \"num\"=2", body.getSQL());
			}
			assertFalse(bodyIterator.hasNext());
		}
		assertFalse(sheetIterator.hasNext());
	}

	@AfterEach final void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.disconnect();
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

		@Override
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
			assertFalse(MODEL.hasCurrentTransaction());
		}

		@Override
		public Duration requestsDeferral()
		{
			assertFalse(MODEL.hasCurrentTransaction());
			return Duration.ZERO;
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
