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

package com.exedio.cope.revsheet;

import static com.exedio.cope.util.Properties.SYSTEM_PROPERTY_SOURCE;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;
import com.exedio.cope.TypeSet;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RevisionSheetTest extends CopeAssert
{
	private static final TestRevisionsFactory revisions = new TestRevisionsFactory();

	private static final Model MODEL = new Model(
			revisions,
			new TypeSet[]{RevisionSheet.types});

	private static final ConnectProperties props = new ConnectProperties(new File("runtime/utiltest.properties"));

	public void testIt()
	{
		final TestSource testSource = new TestSource();
		testSource.putOverride("revise.auto.enabled", "true");
		final ConnectProperties propsx = new ConnectProperties(testSource, SYSTEM_PROPERTY_SOURCE);
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
		final String bodyPrefix = "delete from \"CopeRevisionSheet\" where \"comment\"=";
		revisions.put(new Revisions(
			new Revision(3, "comment3", bodyPrefix + "'sql 3/0'", bodyPrefix + "'sql 3/1'", bodyPrefix + "'sql 3/2'"),
			new Revision(2, "comment2", bodyPrefix + "'sql 2/0'"),
			new Revision(1, "comment1", "sql 1/0")
		));
		MODEL.revise();
		revisions.assertEmpty();

		RevisionSheet.write(MODEL, JobContexts.EMPTY);

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
				assertEquals(0, body.getNumber());
				assertEquals(bodyPrefix + "'sql 2/0'", body.getSql());
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
				assertEquals(0, body.getNumber());
				assertEquals(bodyPrefix + "'sql 3/0'", body.getSql());
			}
			{
				final CopeRevisionSheetBody body = bodyIterator.next();
				assertEquals(1, body.getNumber());
				assertEquals(bodyPrefix + "'sql 3/1'", body.getSql());
			}
			{
				final CopeRevisionSheetBody body = bodyIterator.next();
				assertEquals(2, body.getNumber());
				assertEquals(bodyPrefix + "'sql 3/2'", body.getSql());
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

	static final class TestSource implements Source
	{
		Source fallback;
		Map<String,String> overrides = new HashMap<String, String>();

		TestSource()
		{
			fallback = Sources.load(ConnectProperties.getDefaultPropertyFile());
		}

		@Override()
		public String get( final String key )
		{
			final String override = overrides.get( key );
			return override==null ? fallback.get( key ) : override;
		}

		@Override()
		public Collection<String> keySet()
		{
			final Set<String> keys = new HashSet<String>();
			keys.addAll( overrides.keySet() );
			keys.addAll( fallback.keySet() );
			return keys;
		}

		@Override()
		public String getDescription()
		{
			return "TestSource";
		}

		void putOverride( final String key, final String value )
		{
			overrides.put( key, value );
		}
	}
}
