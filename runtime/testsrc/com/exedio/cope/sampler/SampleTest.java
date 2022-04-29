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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Stuff.MODEL;
import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;
import static com.exedio.cope.tojunit.Assert.assertWithin;
import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.Transaction;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Media;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SampleTest extends ConnectedTest
{
	@Test void testIt() throws InterruptedException
	{
		final Schema schema = samplerModel.getSchema();
		final Table revisionTable = schema.getTable("SamplerRevision");
		assertNotNull(revisionTable);
		final Constraint revisionPrimaryKey = revisionTable.getConstraint("SamplerRevisionUnique");
		assertNotNull(revisionPrimaryKey);
		assertEquals(Constraint.Type.PrimaryKey, revisionPrimaryKey.getType());

		samplerModel.createSchema();
		sampler.checkInternal();
		samplerModel.startTransaction("HistoryTest");
		assertEquals(0, SamplerEnvironment.TYPE.search().size());
		assertEquals(0, SamplerModel.TYPE.search().size());
		assertEquals(0, SamplerItemCache.TYPE.search().size());
		assertEquals(0, SamplerMedia.TYPE.search().size());
		samplerModel.commit();
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		final Transaction leftTransactionInSampledModel = MODEL.leaveTransaction();
		final int distinctDates = getNumberOfDistinctDates();
		MODEL.joinTransaction(leftTransactionInSampledModel);

		touch();
		final Date before55 = new Date();
		assertEquals(null, sampler.sampleInternal());
		final Date after55 = new Date();
		samplerModel.startTransaction("HistoryTest2");

		assertEquals(distinctDates, SamplerEnvironment.TYPE.search().size());
		{
			final Iterator<SamplerModel> iter = SamplerModel.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerTransaction> iter = SamplerTransaction.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerItemCache> iter = SamplerItemCache.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerMedia> iter = SamplerMedia.TYPE.search().iterator();
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		sleepLongerThan(1);
		touch();
		final Date before66 = new Date();
		final SamplerModel model66 = sampler.sampleInternal();
		final Date after66 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		assertEquals(distinctDates, SamplerEnvironment.TYPE.search().size());
		assertWithin(before55, after55, SamplerModel.from.get(model66));
		{
			final Iterator<SamplerModel> iter = iter(SamplerModel.TYPE);
			assertIt(model66, before66, after66, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date66 = SamplerModel.date.get(model66);
		final SamplerTransaction transaction66;
		{
			final Iterator<SamplerTransaction> iter = SamplerTransaction.TYPE.search().iterator();
			transaction66 = assertIt(model66, iter.next());
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerItemCache> iter = iter(SamplerItemCache.TYPE);
			assertFalse(iter.hasNext());
		}
		final SamplerMedia media66a;
		final SamplerMedia media66b;
		{
			final Iterator<SamplerMedia> iter = iter(SamplerMedia.TYPE);
			media66a = assertIt(model66, "SampledModelItem.mediaA", iter.next());
			media66b = assertIt(model66, "SampledModelItem.mediaB", iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(1, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(2, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList(date66, date66  ), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList(date66, date66  ), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		sleepLongerThan(1);
		touch();
		final Date before77 = new Date();
		final SamplerModel model77 = sampler.sampleInternal();
		final Date after77 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		assertEquals(distinctDates, SamplerEnvironment.TYPE.search().size());
		assertEquals(date66, SamplerModel.from.get(model77));
		{
			final Iterator<SamplerModel> iter = iter(SamplerModel.TYPE);
			assertEquals(model66, iter.next());
			assertIt(model77, before77, after77, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date77 = SamplerModel.date.get(model77);
		{
			final Iterator<SamplerTransaction> iter = SamplerTransaction.TYPE.search().iterator();
			assertEquals(transaction66, iter.next());
			assertIt(model77, iter.next());
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerItemCache> iter = iter(SamplerItemCache.TYPE);
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerMedia> iter = iter(SamplerMedia.TYPE);
			assertEquals(media66a, iter.next());
			assertEquals(media66b, iter.next());
			assertIt(model77, "SampledModelItem.mediaA", iter.next());
			assertIt(model77, "SampledModelItem.mediaB", iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(2, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(4, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList(date66, date77  ), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList(date66, date77  ), asList(sampler.analyzeDate(SamplerMedia.TYPE)));
	}

	private static void touch()
	{
		SampledModelItem.TYPE.newItem(
				SampledModelItem.code.map("zack"),
				SampledModelItem.mediaA.map(Media.toValue(new byte[]{1,2,3}, "zick/zack")),
				SampledModelItem.mediaB.map(Media.toValue(new byte[]{1,2,3}, "zick/zack"))
		);
		SampledModelItem2.TYPE.newItem(SampledModelItem2.code.map("zack"));
		MODEL.commit();
		SampledModelItem.mediaA.incrementDelivered();
		SampledModelItem.mediaB.incrementDelivered();
		MODEL.startTransaction("HistoryTest2");
	}

	private static void assertIt(
			final SamplerModel expected,
			final Date before, final Date after,
			final SamplerModel model)
	{
		assertEquals(expected, model);
		assertWithin(before, after, SamplerModel.date.get(model));
		assertEquals(MODEL.getInitializeDate(), SamplerModel.initialized.get(model));
		assertEquals(MODEL.getConnectDate(), SamplerModel.connected.get(model));
	}

	private static SamplerTransaction assertIt(
			final SamplerModel model,
			final SamplerTransaction transaction)
	{
		assertEquals(model, transaction.getModel());
		assertEquals(SamplerModel.date.get(model), transaction.getDate());
		final Transaction tx = MODEL.currentTransaction();
		assertEquals(tx.getID(), transaction.getID());
		assertEquals(tx.getName(), transaction.getName());
		assertEquals(tx.getStartDate(), transaction.getStartDate());
		assertNotNull(transaction.getThread());
		return transaction;
	}

	private static SamplerMedia assertIt(
			final SamplerModel model,
			final String id,
			final SamplerMedia media)
	{
		assertEquals(model, media.getModel());
		assertEquals(id, media.getMedia());
		assertEquals(SamplerModel.date.get(model), media.getDate());
		return media;
	}

	private static <E extends Item> Iterator<E> iter(final Type<E> type)
	{
		final Query<E> q = new Query<>(type.getThis());
		q.setOrderBy(type.getThis(), true);
		return q.search().iterator();
	}

	private static int getNumberOfDistinctDates()
	{
		final Set<Date> distinctDates = new HashSet<>();
		distinctDates.add( MODEL.getConnectDate() );
		for ( final byte[] revInfoBytes: MODEL.getRevisionLogs().values() )
		{
			final RevisionInfo info = RevisionInfo.read(revInfoBytes);
			distinctDates.add( info.getDate() );
		}
		return distinctDates.size();
	}
}
