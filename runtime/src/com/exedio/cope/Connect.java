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

package com.exedio.cope;

import java.util.Date;

final class Connect
{
	final ConnectProperties propertiesIfConnected;
	final Database databaseIfConnected;
	final ItemCache itemCacheIfConnected;
	final QueryCache queryCacheIfConnected;
	final ClusterSender clusterSender;
	final ClusterListener clusterListener;
	final Date connectDate;
	final boolean logTransactions;
	
	Connect(
			final Types types,
			final Revisions revisions,
			final ConnectProperties properties)
	{
				// do this at first, to avoid half-connected model if probe connection fails
				final Database db = properties.createDatabase(revisions);
				this.propertiesIfConnected = properties;
				this.databaseIfConnected = db;
				
				this.itemCacheIfConnected = new ItemCache(types.concreteTypeList, properties.getItemCacheLimit());
				this.queryCacheIfConnected = new QueryCache(properties.getQueryCacheLimit());
				
				if(db.cluster)
				{
					final ClusterConfig config = ClusterConfig.get(properties);
					if(config!=null)
					{
						this.clusterSender   = new ClusterSender  (config, properties);
						this.clusterListener = new ClusterListener(config, properties, clusterSender, types.concreteTypeCount, itemCacheIfConnected, queryCacheIfConnected);
					}
					else
					{
						this.clusterSender   = null;
						this.clusterListener = null;
					}
				}
				else
				{
					this.clusterSender   = null;
					this.clusterListener = null;
				}
				
				this.logTransactions = properties.getTransactionLog();
				this.connectDate = new Date();
	}
	
	void close()
	{
				final Database db = this.databaseIfConnected;
				
				if(this.clusterSender!=null)
					this.clusterSender.close();
				if(this.clusterListener!=null)
					this.clusterListener.close();
				
				db.close();
	}
}
