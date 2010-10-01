/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.sample;

import static com.exedio.cope.sample.Stuff.MODEL;
import static com.exedio.cope.sample.Stuff.sampler;
import static com.exedio.cope.util.Interrupters.VAIN_INTERRUPTER;

import java.util.Date;

public class PurgeTest extends ConnectedTest
{
	public void testPurge() throws InterruptedException
	{
		assertEquals("jdbc:hsqldb:mem:sampler", sampler.getModel().getConnectProperties().getDatabaseUrl());
		assertFalse(sampler.getModel().getConnectProperties().getDatabaseUrl().equals(MODEL.getConnectProperties().getDatabaseUrl()));
		assertEquals(0, sampler.getModel().getConnectProperties().getItemCacheLimit());
		assertEquals(0, sampler.getModel().getConnectProperties().getQueryCacheLimit());
		final boolean itemCache = model.getConnectProperties().getItemCacheLimit()>0;
		sampler.getModel().createSchema();
		sampler.check();

		assertEquals(0, sampler.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryMedia.TYPE));
		assertEquals(0, HistoryPurge.purge(new Date(), VAIN_INTERRUPTER));

		sampler.store(66);
		assertEquals(1, sampler.analyzeCount(HistoryModel.TYPE));
		assertEquals(itemCache?1:0, sampler.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(1, sampler.analyzeCount(HistoryMedia.TYPE));

		sleepLongerThan(1);
		assertEquals(itemCache?3:2, HistoryPurge.purge(new Date(), VAIN_INTERRUPTER));
		assertEquals(0, sampler.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryMedia.TYPE));

		sleepLongerThan(1);
		assertEquals(0, HistoryPurge.purge(new Date(), VAIN_INTERRUPTER));
		assertEquals(0, sampler.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, sampler.analyzeCount(HistoryMedia.TYPE));
	}
}
