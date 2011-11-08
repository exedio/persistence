/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import static java.util.Arrays.asList;

import java.util.Date;
import java.util.Iterator;

import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;

public class SampleTest extends ConnectedTest
{
	public void testIt()
	{
		samplerModel.createSchema();
		sampler.checkInternal();
		samplerModel.startTransaction("HistoryTest");
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

		final Date before55 = new Date();
		sampler.sample();
		final Date after55 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		final SamplerModel model55;
		{
			final Iterator<SamplerModel> iter = SamplerModel.TYPE.search().iterator();
			model55 = assertIt(sampler, before55, after55, 0, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date55 = SamplerModel.date.get(model55);
		final SamplerItemCache itemCache55;
		{
			final Iterator<SamplerItemCache> iter = SamplerItemCache.TYPE.search().iterator();
			itemCache55 = c ? assertIt(model55, sampler, iter.next()) : null;
			assertFalse(iter.hasNext());
		}
		final SamplerMedia media55;
		{
			final Iterator<SamplerMedia> iter = SamplerMedia.TYPE.search().iterator();
			media55 = assertIt(model55, sampler, iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(1, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(c?1:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(1, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList(date55, date55  ), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(c?asList(date55, date55):asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList(date55, date55  ), asList(sampler.analyzeDate(SamplerMedia.TYPE)));

		waitForSystemTimeChange();
		final Date before66 = new Date();
		sampler.sample();
		final Date after66 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		final SamplerModel model66;
		{
			final Iterator<SamplerModel> iter = iter(SamplerModel.TYPE);
			assertEquals(model55, iter.next());
			model66 = assertIt(sampler, before66, after66, 1, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date66 = SamplerModel.date.get(model66);
		{
			final Iterator<SamplerItemCache> iter = iter(SamplerItemCache.TYPE);
			if(c)
			{
				assertEquals(itemCache55, iter.next());
				assertIt(model66, sampler, iter.next());
			}
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<SamplerMedia> iter = iter(SamplerMedia.TYPE);
			assertEquals(media55, iter.next());
			assertIt(model66, sampler, iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();
		assertEquals(2, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(c?2:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(2, sampler.analyzeCount(SamplerMedia.TYPE));
		assertEquals(asList(date55, date66  ), asList(sampler.analyzeDate(SamplerModel.TYPE)));
		assertEquals(c?asList(date55, date66):asList((Date)null, null), asList(sampler.analyzeDate(SamplerItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(sampler.analyzeDate(SamplerClusterNode.TYPE)));
		assertEquals(asList(date55, date66  ), asList(sampler.analyzeDate(SamplerMedia.TYPE)));
	}

	/**
	 * Wait for new Date() to return a different value to avoid unique violation on SamplerModel.date. Especially useful for Windows systems which have
	 * a low system time resolution.
	 * @see Sampler#sample()
	 */
	private void waitForSystemTimeChange()
	{
		try
		{
			CopeAssert.sleepLongerThan(1);
		}
		catch (final InterruptedException e)
		{
			fail("Correctness of following code is not asserted.");
		}
	}

	private static final SamplerModel assertIt(
			final Sampler thread,
			final Date before, final Date after,
			final int running,
			final SamplerModel model)
	{
		assertWithin(before, after, SamplerModel.date.get(model));
		assertEquals(MODEL.getInitializeDate(), SamplerModel.initializeDate.get(model));
		assertEquals(MODEL.getConnectDate(), SamplerModel.connectDate.get(model));
		assertEquals(System.identityHashCode(thread), SamplerModel.thread.getMandatory(model));
		assertEquals(running, SamplerModel.running.getMandatory(model));
		return model;
	}

	private static final SamplerItemCache assertIt(
			final SamplerModel model,
			final Sampler thread,
			final SamplerItemCache itemCache)
	{
		assertEquals(model, itemCache.getModel());
		assertEquals("SampledModelItem", itemCache.getType());
		assertEquals(SamplerModel.date.get(model), itemCache.getDate());
		assertEquals(MODEL.getInitializeDate(), itemCache.getInitalizeDate());
		assertEquals(MODEL.getConnectDate(), itemCache.getConnectDate());
		assertEquals(System.identityHashCode(thread), itemCache.getThread());
		assertEquals(SamplerModel.running.getMandatory(model), itemCache.getRunning());
		return itemCache;
	}

	private static final SamplerMedia assertIt(
			final SamplerModel model,
			final Sampler thread,
			final SamplerMedia media)
	{
		assertEquals(model, media.getModel());
		assertEquals("SampledModelItem.media", media.getMedia());
		assertEquals(SamplerModel.date.get(model), media.getDate());
		assertEquals(MODEL.getInitializeDate(), media.getInitalizeDate());
		assertEquals(MODEL.getConnectDate(), media.getConnectDate());
		assertEquals(System.identityHashCode(thread), media.getThread());
		assertEquals(SamplerModel.running.getMandatory(model), media.getRunning());
		return media;
	}

	private static final <E extends Item> Iterator<E> iter(final Type<E> type)
	{
		final Query<E> q = new Query<E>(type.getThis());
		q.setOrderBy(type.getThis(), true);
		return q.search().iterator();
	}
}
