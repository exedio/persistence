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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Selectable;
import com.exedio.cope.SetValue;
import com.exedio.cope.TransactionCounters;
import com.exedio.cope.Type;
import com.exedio.cope.info.ClusterListenerInfo;
import com.exedio.cope.info.ClusterSenderInfo;
import com.exedio.cope.info.ItemCacheInfo;
import com.exedio.cope.info.QueryCacheInfo;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.Pool;

final class HistoryThread extends Thread
{
	static final Model HISTORY_MODEL =
		new Model(
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	void store(final int running) // non-private for testing
	{
		// prepare
		final int thread = System.identityHashCode(this);
		final int MEDIAS_STAT_LENGTH = 7;
		final int[][] mediaValues = new int[medias.length][];
		for(int i = 0; i<mediaValues.length; i++)
			mediaValues[i] = new int[MEDIAS_STAT_LENGTH];
		
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
		{
			mediaValues[mediaValuesIndex][0] = path.exception.get();
			mediaValues[mediaValuesIndex][1] = path.notAnItem.get();
			mediaValues[mediaValuesIndex][2] = path.noSuchItem.get();
			mediaValues[mediaValuesIndex][3] = path.isNull.get();
			mediaValues[mediaValuesIndex][4] = path.notComputable.get();
			mediaValues[mediaValuesIndex][5] = path.notModified.get();
			mediaValues[mediaValuesIndex][6] = path.delivered.get();
			mediaValuesIndex++;
		}
		final ClusterSenderInfo clusterSenderInfo = watchedModel.getClusterSenderInfo();
		final ClusterListenerInfo clusterListenerInfo = watchedModel.getClusterListenerInfo();
		
		// process data
		final ItemCacheSummary itemCacheSummary = new ItemCacheSummary(itemCacheInfos);
		final SetValue[][] itemCacheSetValues = new SetValue[itemCacheInfos.length][];
		int itemCacheSetValuesIndex = 0;
		for(final ItemCacheInfo ci : itemCacheInfos)
		{
			final ArrayList<SetValue> list = new ArrayList<SetValue>();
			list.add(null); // will be HistoryItemCache.model
			list.add(HistoryItemCache.date.map(date));
			list.add(HistoryItemCache.initializeDate.map(initializeDate));
			list.add(HistoryItemCache.connectDate.map(connectDate));
			list.add(HistoryItemCache.thread.map(thread));
			list.add(HistoryItemCache.running.map(running));
			list.addAll(HistoryItemCache.map(ci));
			itemCacheSetValues[itemCacheSetValuesIndex] = list.toArray(new SetValue[list.size()]);
			itemCacheSetValuesIndex++;
		}
		
		final int[] mediaTotal = new int[MEDIAS_STAT_LENGTH];
		final SetValue[][] mediaSetValues = new SetValue[medias.length][];
		int mediaSetValuesIndex = 0;
		for(int[] mediaValue : mediaValues)
		{
			for(int i = 0; i<MEDIAS_STAT_LENGTH; i++)
				mediaTotal[i] += mediaValue[i];
			mediaSetValues[mediaSetValuesIndex] =
				new SetValue[]{
					null, // will be HistoryMedia.model
					HistoryMedia.media.map(medias[mediaSetValuesIndex].getID()),
					HistoryMedia.date.map(date),
					HistoryMedia.initializeDate.map(initializeDate),
					HistoryMedia.connectDate.map(connectDate),
					HistoryMedia.thread.map(thread),
					HistoryMedia.running.map(running),
					HistoryMedia.exception    .map(mediaValue[0]),
					HistoryMedia.notAnItem    .map(mediaValue[1]),
					HistoryMedia.noSuchItem   .map(mediaValue[2]),
					HistoryMedia.isNull       .map(mediaValue[3]),
					HistoryMedia.notComputable.map(mediaValue[4]),
					HistoryMedia.notModified  .map(mediaValue[5]),
					HistoryMedia.delivered    .map(mediaValue[6]),
			};
			mediaSetValuesIndex++;
		}
		
		final SetValue[][] clusterInfoSetValues;
		if(clusterListenerInfo!=null)
		{
			final List<ClusterListenerInfo.Node> nodes = clusterListenerInfo.getNodes();
			clusterInfoSetValues = new SetValue[nodes.size()][];
			int nodesIndex = 0;
			for(final ClusterListenerInfo.Node node : nodes)
			{
				final ArrayList<SetValue> list = new ArrayList<SetValue>();
				list.add(null); // will be HistoryClusterNode.model
				list.add(HistoryClusterNode.date.map(date));
				list.add(HistoryClusterNode.initializeDate.map(initializeDate));
				list.add(HistoryClusterNode.connectDate.map(connectDate));
				list.add(HistoryClusterNode.thread.map(thread));
				list.add(HistoryClusterNode.running.map(running));
				HistoryClusterNode.map(node);
				clusterInfoSetValues[nodesIndex++] = list.toArray(new SetValue[list.size()]);
			}
		}
		else
		{
			clusterInfoSetValues = new SetValue[0][];
		}
		
		final ArrayList<SetValue> setValueList = new ArrayList<SetValue>();
		setValueList.add(HistoryModel.date.map(date));
		setValueList.add(HistoryModel.initializeDate.map(initializeDate));
		setValueList.add(HistoryModel.connectDate.map(connectDate));
		setValueList.add(HistoryModel.thread.map(thread));
		setValueList.add(HistoryModel.running.map(running));
		setValueList.addAll(HistoryModel.map(connectionPoolInfo));
		setValueList.add(HistoryModel.nextTransactionId.map(nextTransactionId));
		setValueList.addAll(HistoryModel.map(transactionCounters));
		setValueList.addAll(HistoryModel.map(itemCacheSummary));
		setValueList.addAll(HistoryModel.map(queryCacheInfo));
		setValueList.add(HistoryModel.mediasNoSuchPath.map(mediasNoSuchPath));
		setValueList.add(HistoryModel.mediasException    .map(mediaTotal[0]));
		setValueList.add(HistoryModel.mediasNotAnItem    .map(mediaTotal[1]));
		setValueList.add(HistoryModel.mediasNoSuchItem   .map(mediaTotal[2]));
		setValueList.add(HistoryModel.mediasIsNull       .map(mediaTotal[3]));
		setValueList.add(HistoryModel.mediasNotComputable.map(mediaTotal[4]));
		setValueList.add(HistoryModel.mediasNotModified  .map(mediaTotal[5]));
		setValueList.add(HistoryModel.mediasDelivered    .map(mediaTotal[6]));
		setValueList.addAll(HistoryModel.map(clusterSenderInfo));
		setValueList.addAll(HistoryModel.map(clusterListenerInfo));
		final SetValue[] setValues = setValueList.toArray(new SetValue[setValueList.size()]);

		// save data
		try
		{
			HISTORY_MODEL.startTransaction(topic + running);
			final HistoryModel model = new HistoryModel(setValues);
			{
				final SetValue modelSetValue = HistoryItemCache.model.map(model);
				for(final SetValue[] itemCacheSetValue : itemCacheSetValues)
				{
					assert itemCacheSetValue[0]==null : itemCacheSetValue[0];
					itemCacheSetValue[0] = modelSetValue;
					new HistoryItemCache(itemCacheSetValue);
				}
			}
			{
				final SetValue modelSetValue = HistoryMedia.model.map(model);
				for(SetValue[] mediaSetValue : mediaSetValues)
				{
					assert mediaSetValue[0]==null : mediaSetValue[0];
					mediaSetValue[0] = modelSetValue;
					new HistoryMedia(mediaSetValue);
				}
			}
			{
				final SetValue modelSetValue = HistoryClusterNode.model.map(model);
				for(SetValue[] clusterInfoSetValue : clusterInfoSetValues)
				{
					assert clusterInfoSetValue[0]==null : clusterInfoSetValue[0];
					clusterInfoSetValue[0] = modelSetValue;
					new HistoryClusterNode(clusterInfoSetValue);
				}
			}
			HISTORY_MODEL.commit();
		}
		finally
		{
			HISTORY_MODEL.rollbackIfNotCommitted();
		}
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
			dates = new Query<List>(new Selectable[]{date.min(), date.max()}, type, null).searchSingleton();
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
