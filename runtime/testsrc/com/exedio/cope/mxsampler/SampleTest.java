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

package com.exedio.cope.mxsampler;

import static com.exedio.cope.mxsampler.Stuff.sampler;
import static com.exedio.cope.mxsampler.Stuff.samplerModel;

import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.JobContexts;
import java.util.Date;
import java.util.Iterator;

public class SampleTest extends ConnectedTest
{
	public void testIt()
	{
		samplerModel.startTransaction("HistoryTest1");
		assertEquals(0, MxSamplerGlobal.TYPE.search().size());
		assertEquals(0, MxSamplerMemoryPool.TYPE.search().size());
		samplerModel.commit();

		final Date before55 = new Date();
		final MxSamplerGlobal global55 = sampler.sampleInternal();
		final Date after55 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		{
			final Iterator<MxSamplerGlobal> iter = MxSamplerGlobal.TYPE.search().iterator();
			assertIt(global55, sampler, before55, after55, 0, iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();

		waitForSystemTimeChange();
		final Date before66 = new Date();
		final MxSamplerGlobal global66 = sampler.sampleInternal();
		final Date after66 = new Date();
		samplerModel.startTransaction("HistoryTest2");
		{
			final Iterator<MxSamplerGlobal> iter = iter(MxSamplerGlobal.TYPE);
			assertEquals(global55, iter.next());
			assertIt(global66, sampler, before66, after66, 1, iter.next());
			assertFalse(iter.hasNext());
		}
		samplerModel.commit();

		sampler.purge(new Date(), JobContexts.EMPTY);
	}

	/**
	 * Wait for new Date() to return a different value to avoid unique violation on SamplerModel.date. Especially useful for Windows systems which have
	 * a low system time resolution.
	 * @see MxSampler#sample()
	 */
	private static void waitForSystemTimeChange()
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

	private static final MxSamplerGlobal assertIt(
			final MxSamplerGlobal expected,
			final MxSampler sampler,
			final Date before, final Date after,
			final int running,
			final MxSamplerGlobal model)
	{
		assertEquals(expected, model);
		assertWithin(before, after, MxSamplerGlobal.date.get(model));
		assertEquals(System.identityHashCode(sampler), MxSamplerGlobal.sampler.getMandatory(model));
		assertEquals(running, MxSamplerGlobal.running.getMandatory(model));
		return model;
	}

	private static final <E extends Item> Iterator<E> iter(final Type<E> type)
	{
		final Query<E> q = new Query<>(type.getThis());
		q.setOrderBy(type.getThis(), true);
		return q.search().iterator();
	}
}
