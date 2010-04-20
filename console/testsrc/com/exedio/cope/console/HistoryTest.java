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

package com.exedio.cope.console;

import static com.exedio.cope.console.HistoryThread.HISTORY_MODEL;
import static java.util.Arrays.asList;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.util.Properties;

public class HistoryTest extends TestCase
{
	private static final Model MODEL = new Model(HistoryItem.TYPE);
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final Properties.Source s = new Properties.Source(){

			public String get(final String key)
			{
				if(key.equals("database.url"))
					return "jdbc:hsqldb:mem:copetest";
				else if(key.equals("database.user"))
					return "sa";
				else if(key.equals("database.password"))
					return "";
				else if(key.equals("cache.query.limit"))
					return "0";
				else
					return null;
			}

			public String getDescription()
			{
				return "HistoryTest Properties.Source";
			}

			public Collection<String> keySet()
			{
				return null;
			}
		};
		MODEL.connect(new ConnectProperties(s, null));
		HISTORY_MODEL.connect(new ConnectProperties(s, null));
		HISTORY_MODEL.createSchema();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		MODEL.disconnect();
		HISTORY_MODEL.dropSchema();
		HISTORY_MODEL.disconnect();
		super.tearDown();
	}
	
	public void testIt()
	{
		final HistoryThread thread = new HistoryThread(MODEL, "zack", 0);

		HISTORY_MODEL.startTransaction("HistoryTest");
		assertEquals(0, HistoryModel.TYPE.search().size());
		assertEquals(0, HistoryItemCache.TYPE.search().size());
		assertEquals(0, HistoryMedia.TYPE.search().size());
		HISTORY_MODEL.commit();
		assertEquals(0, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryMedia.TYPE));
		assertEquals(asList((Date)null, null), asList(HistoryThread.analyzeDate(HistoryModel.TYPE)));
		assertEquals(asList((Date)null, null), asList(HistoryThread.analyzeDate(HistoryItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(HistoryThread.analyzeDate(HistoryClusterNode.TYPE)));
		assertEquals(asList((Date)null, null), asList(HistoryThread.analyzeDate(HistoryMedia.TYPE)));
		
		final Date before55 = new Date();
		thread.store(55);
		final Date after55 = new Date();
		HISTORY_MODEL.startTransaction("HistoryTest2");
		final HistoryModel model55;
		{
			final Iterator<HistoryModel> iter = HistoryModel.TYPE.search().iterator();
			model55 = assertIt(thread, before55, after55, 55, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date55 = HistoryModel.date.get(model55);
		final HistoryItemCache itemCache55;
		{
			final Iterator<HistoryItemCache> iter = HistoryItemCache.TYPE.search().iterator();
			itemCache55 = assertIt(model55, thread, iter.next());
			assertFalse(iter.hasNext());
		}
		final HistoryMedia media55;
		{
			final Iterator<HistoryMedia> iter = HistoryMedia.TYPE.search().iterator();
			media55 = assertIt(model55, thread, iter.next());
			assertFalse(iter.hasNext());
		}
		HISTORY_MODEL.commit();
		assertEquals(1, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(1, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(1, HistoryThread.analyzeCount(HistoryMedia.TYPE));
		assertEquals(asList(date55, date55  ), asList(HistoryThread.analyzeDate(HistoryModel.TYPE)));
		assertEquals(asList(date55, date55  ), asList(HistoryThread.analyzeDate(HistoryItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(HistoryThread.analyzeDate(HistoryClusterNode.TYPE)));
		assertEquals(asList(date55, date55  ), asList(HistoryThread.analyzeDate(HistoryMedia.TYPE)));
		
		final Date before66 = new Date();
		thread.store(66);
		final Date after66 = new Date();
		HISTORY_MODEL.startTransaction("HistoryTest2");
		final HistoryModel model66;
		{
			final Iterator<HistoryModel> iter = iter(HistoryModel.TYPE);
			assertEquals(model55, iter.next());
			model66 = assertIt(thread, before66, after66, 66, iter.next());
			assertFalse(iter.hasNext());
		}
		final Date date66 = HistoryModel.date.get(model66);
		{
			final Iterator<HistoryItemCache> iter = iter(HistoryItemCache.TYPE);
			assertEquals(itemCache55, iter.next());
			assertIt(model66, thread, iter.next());
			assertFalse(iter.hasNext());
		}
		{
			final Iterator<HistoryMedia> iter = iter(HistoryMedia.TYPE);
			assertEquals(media55, iter.next());
			assertIt(model66, thread, iter.next());
			assertFalse(iter.hasNext());
		}
		HISTORY_MODEL.commit();
		assertEquals(2, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(2, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(2, HistoryThread.analyzeCount(HistoryMedia.TYPE));
		assertEquals(asList(date55, date66  ), asList(HistoryThread.analyzeDate(HistoryModel.TYPE)));
		assertEquals(asList(date55, date66  ), asList(HistoryThread.analyzeDate(HistoryItemCache.TYPE)));
		assertEquals(asList((Date)null, null), asList(HistoryThread.analyzeDate(HistoryClusterNode.TYPE)));
		assertEquals(asList(date55, date66  ), asList(HistoryThread.analyzeDate(HistoryMedia.TYPE)));
	}
	
	public void testPurge()
	{
		final HistoryThread thread = new HistoryThread(MODEL, "zack", 0);
		assertEquals(0, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryMedia.TYPE));
		assertEquals(0, HistoryPurge.purge(new Date()));
		
		thread.store(66);
		assertEquals(1, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(1, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(1, HistoryThread.analyzeCount(HistoryMedia.TYPE));
		
		sleepLongerThan(1);
		assertEquals(3, HistoryPurge.purge(new Date()));
		assertEquals(0, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryMedia.TYPE));
		
		sleepLongerThan(1);
		assertEquals(0, HistoryPurge.purge(new Date()));
		assertEquals(0, HistoryThread.analyzeCount(HistoryModel.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryItemCache.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryClusterNode.TYPE));
		assertEquals(0, HistoryThread.analyzeCount(HistoryMedia.TYPE));
	}
	
	private static final HistoryModel assertIt(
			final HistoryThread thread,
			final Date before, final Date after,
			final int running,
			final HistoryModel model)
	{
		assertWithin(before, after, HistoryModel.date.get(model));
		assertEquals(MODEL.getInitializeDate(), HistoryModel.initializeDate.get(model));
		assertEquals(MODEL.getConnectDate(), HistoryModel.connectDate.get(model));
		assertEquals(System.identityHashCode(thread), HistoryModel.thread.getMandatory(model));
		assertEquals(running, HistoryModel.running.getMandatory(model));
		return model;
	}
	private static final HistoryItemCache assertIt(
			final HistoryModel model,
			final HistoryThread thread,
			final HistoryItemCache itemCache)
	{
		assertEquals(model, itemCache.getModel());
		assertEquals("HistoryItem", itemCache.getType());
		assertEquals(HistoryModel.date.get(model), itemCache.getDate());
		assertEquals(MODEL.getInitializeDate(), itemCache.getInitalizeDate());
		assertEquals(MODEL.getConnectDate(), itemCache.getConnectDate());
		assertEquals(System.identityHashCode(thread), itemCache.getThread());
		assertEquals(HistoryModel.running.getMandatory(model), itemCache.getRunning());
		return itemCache;
	}
	
	private static final HistoryMedia assertIt(
			final HistoryModel model,
			final HistoryThread thread,
			final HistoryMedia media)
	{
		assertEquals(model, media.getModel());
		assertEquals("HistoryItem.media", media.getMedia());
		assertEquals(HistoryModel.date.get(model), media.getDate());
		assertEquals(MODEL.getInitializeDate(), media.getInitalizeDate());
		assertEquals(MODEL.getConnectDate(), media.getConnectDate());
		assertEquals(System.identityHashCode(thread), media.getThread());
		assertEquals(HistoryModel.running.getMandatory(model), media.getRunning());
		return media;
	}
	
	private static final <E extends Item> Iterator<E> iter(final Type<E> type)
	{
		final Query<E> q = new Query<E>(type.getThis());
		q.setOrderBy(type.getThis(), true);
		return q.search().iterator();
	}
	
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";
	
	public static final void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
	}

	/**
	 * This method will not return until the result of System.currentTimeMillis() has increased
	 * by the given amount of milli seconds.
	 */
	private static void sleepLongerThan(final long millis)
	{
		final long start = System.currentTimeMillis();
		// The loop double-checks that currentTimeMillis() really returns a sufficiently higher
		// value ... needed for Windows.
		try
		{
			do
			{
				Thread.sleep(millis+1);
			}
			while((System.currentTimeMillis()-start)<=millis);
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
