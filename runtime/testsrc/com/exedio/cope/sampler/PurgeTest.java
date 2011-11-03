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

import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;

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
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
		assertPurge(new Date(), 0, 0, 0, 0);

		sampler.sample();
		assertEquals(1, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(c?1:0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(1, sampler.analyzeCount(SamplerMedia.TYPE));

		sleepLongerThan(1);
		assertPurge(new Date(), c?1:0, 0, 1, 1);
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));

		sleepLongerThan(1);
		assertPurge(new Date(), 0, 0, 0, 0);
		assertEquals(0, sampler.analyzeCount(SamplerModel.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(SamplerMedia.TYPE));
	}

	private static final void assertPurge(final Date date, final int... progress)
	{
		final MockJobContext ctx = new MockJobContext();
		sampler.purge(date, ctx);
		assertEquals(4, ctx.getRequestedToStopCount());
		final ArrayList<Integer> progressList = new ArrayList<Integer>();
		for(final int p : progress)
			progressList.add(p);
		assertEquals(progressList, ctx.getProgress());
	}
}
