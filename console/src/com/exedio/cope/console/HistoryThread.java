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

import static com.exedio.cope.Query.newQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Selectable;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.info.ClusterListenerInfo;
import com.exedio.cope.info.ClusterSenderInfo;
import com.exedio.cope.info.ItemCacheInfo;
import com.exedio.cope.info.QueryCacheInfo;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.Pool;

final class HistoryThread extends Thread
{
	static final Model HISTORY_MODEL =
		new Model(
			HistoryRevisions.REVISIONS,
			HistoryModel.TYPE,
			HistoryItemCache.TYPE,
			HistoryClusterNode.TYPE,
			HistoryMedia.TYPE);
	
	private static final String NAME = "COPE History";
	
	private final String name;
	private final Model watchedModel;
	private final String propertyFile;
	private final Object lock = new Object();
	private final String topic;
	private final MediaPath[] medias;
	private volatile boolean proceed = true;
	
	HistoryThread(final Model watchedModel, final String propertyFile)
	{
		super(NAME);
		this.name = NAME + ' ' + '(' + Integer.toString(System.identityHashCode(this), 36) + ')';
		setName(name);
		this.watchedModel = watchedModel;
		this.propertyFile = propertyFile;
		this.topic = name + ' ';
		
		assert watchedModel!=null;
		assert propertyFile!=null;
		
		final ArrayList<MediaPath> medias = new ArrayList<MediaPath>();
		for(final Type<?> type : watchedModel.getTypes())
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
			ConnectToken connectToken = null;
			final long connecting = System.currentTimeMillis();
			try
			{
				connectToken =
					ConnectToken.issue(HISTORY_MODEL, new ConnectProperties(new File(propertyFile)), name);
				System.out.println(topic + "run() connected (" + (System.currentTimeMillis() - connecting) + "ms)");
				try
				{
					HISTORY_MODEL.startTransaction("check");
					HISTORY_MODEL.checkSchema();
					HISTORY_MODEL.commit();
				}
				finally
				{
					HISTORY_MODEL.rollbackIfNotCommitted();
				}
				
				for(int running = 0; proceed; running++)
				{
					System.out.println(topic + "run() store " + running);
					store(running);
					sleepByWait(60000l);
				}
			}
			finally
			{
				if(connectToken!=null)
				{
					System.out.println(topic + "run() disconnecting");
					final long disconnecting = System.currentTimeMillis();
					connectToken.returnIt();
					System.out.println(topic + "run() disconnected (" + (System.currentTimeMillis() - disconnecting) + "ms)");
				}
				else
					System.out.println(topic + "run() not connected");
			}
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
		}
	}
	
	void store(final int running) // non-private for testing
	{
		// prepare
		final int thread = System.identityHashCode(this);
		final MediaInfo[] mediaValues = new MediaInfo[medias.length]; // TODO rename
		
		// gather data
		final Date date = new Date();
		final Date initializeDate = watchedModel.getInitializeDate();
		final Date connectDate = watchedModel.getConnectDate();
		final Pool.Info connectionPoolInfo = watchedModel.getConnectionPoolInfo();
		final long nextTransactionId = watchedModel.getNextTransactionId();
		final TransactionCounters transactionCounters = watchedModel.getTransactionCounters();
		final ItemCacheInfo[] itemCacheInfos = watchedModel.getItemCacheInfo();
		final QueryCacheInfo queryCacheInfo = watchedModel.getQueryCacheInfo();
		final int mediasNoSuchPath = MediaPath.noSuchPath.get();
		int mediaValuesIndex = 0;
		for(final MediaPath path : medias)
			mediaValues[mediaValuesIndex++] = path.getInfo();
		final ClusterSenderInfo clusterSenderInfo = watchedModel.getClusterSenderInfo();
		final ClusterListenerInfo clusterListenerInfo = watchedModel.getClusterListenerInfo();
		
		// process data
		final ItemCacheSummary itemCacheSummary = new ItemCacheSummary(itemCacheInfos);
		final MediaSummary mediaTotal = new MediaSummary(mediaValues); // TODO rename
		
		// save data
		try
		{
			HISTORY_MODEL.startTransaction(topic + running);
			final HistoryModel model;
			{
				final ArrayList<SetValue> sv = new ArrayList<SetValue>();
				sv.add(HistoryModel.date.map(date));
				sv.add(HistoryModel.initializeDate.map(initializeDate));
				sv.add(HistoryModel.connectDate.map(connectDate));
				sv.add(HistoryModel.thread.map(thread));
				sv.add(HistoryModel.running.map(running));
				sv.addAll(HistoryModel.map(connectionPoolInfo));
				sv.add(HistoryModel.nextTransactionId.map(nextTransactionId));
				sv.addAll(HistoryModel.map(transactionCounters));
				sv.addAll(HistoryModel.map(itemCacheSummary));
				sv.addAll(HistoryModel.map(queryCacheInfo));
				sv.add(HistoryModel.mediasNoSuchPath.map(mediasNoSuchPath));
				sv.add(HistoryModel.mediasException    .map(mediaTotal.getException()));
				sv.add(HistoryModel.mediasNotAnItem    .map(mediaTotal.getNotAnItem()));
				sv.add(HistoryModel.mediasNoSuchItem   .map(mediaTotal.getNoSuchItem()));
				sv.add(HistoryModel.mediasIsNull       .map(mediaTotal.getIsNull()));
				sv.add(HistoryModel.mediasNotComputable.map(mediaTotal.getNotComputable()));
				sv.add(HistoryModel.mediasNotModified  .map(mediaTotal.getNotModified()));
				sv.add(HistoryModel.mediasDelivered    .map(mediaTotal.getDelivered()));
				sv.addAll(HistoryModel.map(clusterSenderInfo));
				sv.addAll(HistoryModel.map(clusterListenerInfo));
				model = new HistoryModel(toArray(sv));
			}
			{
				for(final ItemCacheInfo info : itemCacheInfos)
				{
					final ArrayList<SetValue> sv = new ArrayList<SetValue>();
					sv.addAll(HistoryItemCache.map(model));
					sv.addAll(HistoryItemCache.map(info));
					new HistoryItemCache(toArray(sv));
				}
			}
			{
				final SetValue modelSetValue = HistoryMedia.model.map(model);
				int mediaSetValuesIndex = 0;
				for(final MediaInfo mediaValue : mediaValues)
				{
					new HistoryMedia(
							modelSetValue,
							HistoryMedia.media.map(medias[mediaSetValuesIndex++].getID()),
							HistoryMedia.date.map(date),
							HistoryMedia.initializeDate.map(initializeDate),
							HistoryMedia.connectDate.map(connectDate),
							HistoryMedia.thread.map(thread),
							HistoryMedia.running.map(running),
							HistoryMedia.exception    .map(mediaValue.getException()),
							HistoryMedia.notAnItem    .map(mediaValue.getNotAnItem()),
							HistoryMedia.noSuchItem   .map(mediaValue.getNoSuchItem()),
							HistoryMedia.isNull       .map(mediaValue.getIsNull()),
							HistoryMedia.notComputable.map(mediaValue.getNotComputable()),
							HistoryMedia.notModified  .map(mediaValue.getNotModified()),
							HistoryMedia.delivered    .map(mediaValue.getDelivered()));
				}
			}
			if(clusterListenerInfo!=null)
			{
				for(final ClusterListenerInfo.Node node : clusterListenerInfo.getNodes())
				{
					final ArrayList<SetValue> list = new ArrayList<SetValue>();
					HistoryClusterNode.map(model);
					HistoryClusterNode.map(node);
					new HistoryClusterNode(toArray(list));
				}
			}
			HISTORY_MODEL.commit();
		}
		finally
		{
			HISTORY_MODEL.rollbackIfNotCommitted();
		}
	}
	
	private static final SetValue[] toArray(final ArrayList<SetValue> sv)
	{
		return sv.toArray(new SetValue[sv.size()]);
	}
	
	private void sleepByWait(final long millis)
	{
		synchronized(lock)
		{
			//System.out.println(topic + "run() sleeping (" + millis + "ms)");
			//final long sleeping = System.currentTimeMillis();
			try
			{
				lock.wait(millis);
			}
			catch(InterruptedException e)
			{
				throw new RuntimeException(name, e);
			}
			//System.out.println(topic + "run() slept    (" + (System.currentTimeMillis()-sleeping) + "ms)");
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
			throw new RuntimeException(name, e);
		}
		System.out.println(topic + "stopAndJoin() joined (" + (System.currentTimeMillis() - joining) + "ms)");
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	static int analyzeCount(final Type type)
	{
		final int result;
		try
		{
			HISTORY_MODEL.startTransaction("history analyze count");
			result = type.newQuery().total();
			HISTORY_MODEL.commit();
		}
		finally
		{
			HISTORY_MODEL.rollbackIfNotCommitted();
		}
		return result;
	}
	
	static Date[] analyzeDate(final Type type)
	{
		final DateField date = (DateField)type.getFeature("date");
		final List dates;
		try
		{
			HISTORY_MODEL.startTransaction("history analyze dates");
			dates = newQuery(new Selectable[]{date.min(), date.max()}, type, null).searchSingleton();
			HISTORY_MODEL.commit();
		}
		finally
		{
			HISTORY_MODEL.rollbackIfNotCommitted();
		}
		return new Date[] {
				(Date)dates.get(0),
				(Date)dates.get(1),
			};
	}
}
