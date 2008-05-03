/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.CacheInfo;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ConnectionPoolInfo;

final class LogThread extends Thread
{
	static final Model loggerModel = new Model(HistoryModel.TYPE);
	
	private final Model loggedModel;
	private final String logPropertyFile;
	private final Object lock = new Object();
	private final String topic;
	private final MediaPath[] medias;
	private volatile boolean proceed = true;
	
	LogThread(final Model model, final String logPropertyFile)
	{
		this.loggedModel = model;
		this.logPropertyFile = logPropertyFile;
		this.topic = "LogThread(" + Integer.toString(System.identityHashCode(this), 36) + ") ";
		
		assert model!=null;
		assert logPropertyFile!=null;
		
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		for(final Type<?> type : loggedModel.getTypes())
			for(final Feature feature : type.getDeclaredFeatures())
				if(feature instanceof MediaPath)
					medias.add((MediaPath)feature);
		this.medias = medias.toArray(new MediaPath[medias.size()]);
	}
	
	@Override
	public void run()
	{
		System.out.println(topic + "run() started");
		try
		{
			sleepByWait(2000l);
			if(!proceed)
				return;
			
			System.out.println(topic + "run() connecting");
			ConnectToken loggerConnectToken = null;
			final long connecting = System.currentTimeMillis();
			try
			{
				loggerConnectToken =
					ConnectToken.issue(loggerModel, new ConnectProperties(new File(logPropertyFile)), "logger");
				System.out.println(topic + "run() connected (" + (System.currentTimeMillis() - connecting) + "ms)");
				//loggerModel.tearDownDatabase(); loggerModel.createDatabase();
				try
				{
					loggerModel.startTransaction("check");
					loggerModel.checkDatabase();
					loggerModel.commit();
				}
				finally
				{
					loggerModel.rollbackIfNotCommitted();
				}
				
				for(int running = 0; proceed; running++)
				{
					System.out.println(topic + "run() LOG " + running);
					
					log(running);
					
					sleepByWait(1000l);
				}
			}
			finally
			{
				if(loggerConnectToken!=null)
				{
					System.out.println(topic + "run() disconnecting");
					final long disconnecting = System.currentTimeMillis();
					loggerConnectToken.returnIt();
					System.out.println(topic + "run() disconnected (" + (System.currentTimeMillis() - disconnecting) + "ms)");
				}
				else
					System.out.println(topic + "run() not connected");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void log(final int running)
	{
		// gather data
		final Date date = new Date();
		final ConnectionPoolInfo connectionPoolInfo = loggedModel.getConnectionPoolInfo();
		final long nextTransactionId = loggedModel.getNextTransactionId();
		final CacheInfo[] itemCacheInfos = loggedModel.getItemCacheInfo();
		final long[] queryCacheInfo = loggedModel.getQueryCacheInfo();
		int mediasException = 0;
		int mediasNotAnItem = 0;
		int mediasNoSuchItem = 0;
		int mediasIsNull = 0;
		int mediasNotComputable = 0;
		int mediasNotModified = 0;
		int mediasDelivered = 0;
		for(final MediaPath path : medias)
		{
			mediasException += path.exception.get();
			mediasNotAnItem += path.notAnItem.get();
			mediasNoSuchItem += path.noSuchItem.get();
			mediasIsNull += path.isNull.get();
			mediasNotComputable += path.notComputable.get();
			mediasNotModified += path.notModified.get();
			mediasDelivered += path.delivered.get();
		}
		
		// process data
		int itemCacheHits = 0;
		int itemCacheMisses = 0;
		int itemCacheNumberOfCleanups = 0;
		int itemCacheItemsCleanedUp = 0;
		for(final CacheInfo ci : itemCacheInfos)
		{
			itemCacheHits += ci.getHits();
			itemCacheMisses += ci.getMisses();
			itemCacheNumberOfCleanups += ci.getNumberOfCleanups();
			itemCacheItemsCleanedUp += ci.getItemsCleanedUp();
		}
		
		final SetValue[] setValues = new SetValue[]{
				HistoryModel.date.map(date),
				HistoryModel.running.map(running),
				HistoryModel.connectionPoolIdle.map(connectionPoolInfo.getIdleCounter()),
				HistoryModel.connectionPoolGet.map(connectionPoolInfo.getCounter().getGetCounter()),
				HistoryModel.connectionPoolPut.map(connectionPoolInfo.getCounter().getPutCounter()),
				HistoryModel.connectionPoolInvalidFromIdle.map(connectionPoolInfo.getInvalidFromIdle()),
				HistoryModel.connectionPoolInvalidIntoIdle.map(connectionPoolInfo.getInvalidIntoIdle()),
				HistoryModel.nextTransactionId.map(nextTransactionId),
				HistoryModel.itemCacheHits.map(itemCacheHits),
				HistoryModel.itemCacheMisses.map(itemCacheMisses),
				HistoryModel.itemCacheNumberOfCleanups.map(itemCacheNumberOfCleanups),
				HistoryModel.itemCacheItemsCleanedUp.map(itemCacheItemsCleanedUp),
				HistoryModel.queryCacheHits.map(queryCacheInfo[0]),
				HistoryModel.queryCacheMisses.map(queryCacheInfo[1]),
				HistoryModel.mediasException.map(mediasException),
				HistoryModel.mediasNotAnItem.map(mediasNotAnItem),
				HistoryModel.mediasNoSuchItem.map(mediasNoSuchItem),
				HistoryModel.mediasIsNull.map(mediasIsNull),
				HistoryModel.mediasNotComputable.map(mediasNotComputable),
				HistoryModel.mediasNotModified.map(mediasNotModified),
				HistoryModel.mediasDelivered.map(mediasDelivered)
		};

		// save data
		try
		{
			loggerModel.startTransaction("log " + running);
			new HistoryModel(setValues);
			loggerModel.commit();
		}
		finally
		{
			loggerModel.rollbackIfNotCommitted();
		}
	}
	
	private void sleepByWait(final long millis)
	{
		synchronized(lock)
		{
			System.out.println(topic + "run() sleeping (" + millis + "ms)");
			final long sleeping = System.currentTimeMillis();
			try
			{
				lock.wait(millis);
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(e);
			}
			System.out.println(topic + "run() slept    (" + (System.currentTimeMillis()-sleeping) + "ms)");
		}
	}
	
	void stopAndJoin()
	{
		System.out.println(topic + "stopAndJoin() entering");
		proceed = false;
		synchronized(lock)
		{
			System.out.println(topic + "stopAndJoin() notifying");
			lock.notify();
		}
		System.out.println(topic + "stopAndJoin() notified");
		final long joining = System.currentTimeMillis();
		try
		{
			join();
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		System.out.println(topic + "stopAndJoin() joined (" + (System.currentTimeMillis() - joining) + "ms)");
	}
}
