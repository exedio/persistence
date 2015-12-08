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

import com.exedio.cope.Query;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.pattern.Media;
import java.util.ArrayList;
import java.util.Date;

public class PurgeTest extends ConnectedTest
{
	public void testPurge() throws InterruptedException
	{
		assertEquals(0, samplerModel.getConnectProperties().getItemCacheLimit());
		assertEquals(0, samplerModel.getConnectProperties().getQueryCacheLimit());
		samplerModel.createSchema();
		sampler.checkInternal();

		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
		assertPurge(new Date(), 0, 0, 0, 0, 0);

		sampler.sampleInternal(); // just initializes lastStep
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));

		touch();
		sampler.sampleInternal();
		assertEquals(1, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(1, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(c?2:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(2, sampler.analyzeCount(SamplerMedia.TYPE));

		touch();
		sleepLongerThan( 1 );
		sampler.sampleInternal();
		assertEquals(2, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(2, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(c?4:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(4, sampler.analyzeCount(SamplerMedia.TYPE));

		final Date date;
		try(TransactionTry tx = samplerModel.startTransactionTry(PurgeTest.class.getName()))
		{
			date = new Query<>(SamplerModel.date.min(), SamplerModel.TYPE, null).searchSingletonStrict();
			tx.commit();
		}
		assertPurge(date, 0, 0, 0, 0, 0);
		assertEquals(2, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(2, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(c?4:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(4, sampler.analyzeCount(SamplerMedia.TYPE));


		final Date dateMax;
		try(TransactionTry tx = samplerModel.startTransactionTry(PurgeTest.class.getName()))
		{
			dateMax = new Query<>(SamplerModel.date.max(), SamplerModel.TYPE, null).searchSingletonStrict();
			tx.commit();
		}
		final Date purgeDate = new Date(dateMax.getTime()+1);
		assertPurge(purgeDate, 2, c?4:0, 0, 4, 2);
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));

		assertPurge(purgeDate, 0, 0, 0, 0, 0);
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerTransaction.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
	}

	private static void touch()
	{
		SampledModelItem .TYPE.newItem(
				SampledModelItem .code.map("zack"),
				SampledModelItem.mediaA.map(Media.toValue(new byte[]{1,2,3}, "zick/zack")),
				SampledModelItem.mediaB.map(Media.toValue(new byte[]{1,2,3}, "zick/zack"))
		);
		SampledModelItem2.TYPE.newItem(SampledModelItem2.code.map("zack"));
		MODEL.commit();
		SampledModelItem.mediaA.incrementDelivered();
		SampledModelItem.mediaB.incrementDelivered();
		MODEL.startTransaction("HistoryTest2");
	}

	private static final void assertPurge(final Date date, final int... progress)
	{
		final MockJobContext ctx = new MockJobContext();
		sampler.purge(date, ctx);
		assertEquals(5, ctx.getRequestedToStopCount());
		final ArrayList<Integer> progressList = new ArrayList<>();
		for(final int p : progress)
			progressList.add(p);
		assertEquals(progressList, ctx.getProgress());
	}
}
