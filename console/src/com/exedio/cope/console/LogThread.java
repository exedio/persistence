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
import java.util.Date;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.util.ConnectToken;

final class LogThread extends Thread
{
	private static final Model loggerModel = new Model(LogModel.TYPE);
	
	private final Model loggedModel;
	private final String logPropertyFile;
	private final Object lock = new Object();
	private final String topic;
	private volatile boolean proceed = true;
	
	LogThread(final Model model, final String logPropertyFile)
	{
		this.loggedModel = model;
		this.logPropertyFile = logPropertyFile;
		this.topic = "LogThread(" + Integer.toString(System.identityHashCode(this), 36) + ") ";
		
		assert model!=null;
		assert logPropertyFile!=null;
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
		final long nextTransactionId = loggedModel.getNextTransactionId();
		
		// process data
		final SetValue[] setValues = new SetValue[]{
				LogModel.date.map(date),
				LogModel.running.map(running),
				LogModel.nextTransactionId.map(nextTransactionId)};

		// save data
		try
		{
			loggerModel.startTransaction("log " + running);
			new LogModel(setValues);
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
