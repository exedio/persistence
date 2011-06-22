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

package com.exedio.cope.junit;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.misc.TimeUtil;

final class ModelConnector implements Runnable
{
	private static Model createdSchema = null;
	private static boolean registeredDropSchemaHook = false;
	private static final Object lock = new Object();

	private ModelConnector()
	{
		// prevent instantiation
	}

	static void connectAndCreate(final Model model, final ConnectProperties properties, final boolean writeTiming)
	{
		synchronized(lock)
		{
			if(createdSchema!=model)
			{
				dropAndDisconnectIfNeeded(writeTiming);

				final long beforeConnect = writeTiming ? System.nanoTime() : 0;
				model.connect(properties);
				if(writeTiming)
					System.out.println("" + TimeUtil.toMillies(System.nanoTime(), beforeConnect) + " connect");

				final long beforeCreateSchema = writeTiming ? System.nanoTime() : 0;
				model.createSchema();
				if(writeTiming)
					System.out.println("" + TimeUtil.toMillies(System.nanoTime(), beforeCreateSchema) + " createSchema");

				createdSchema = model;
			}
			else
				model.getConnectProperties().ensureEquality(properties);
		}
	}

	static void dropAndDisconnect()
	{
		synchronized(lock)
		{
			if(!registeredDropSchemaHook)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(new ModelConnector()));
				registeredDropSchemaHook = true;
			}
		}
	}

	private static void dropAndDisconnectIfNeeded(final boolean writeTiming)
	{
		synchronized(lock)
		{
			if(createdSchema!=null)
			{
				final long beforeDropSchema = writeTiming ? System.nanoTime() : 0;
				createdSchema.dropSchema();
				if(writeTiming)
					System.out.println("" + TimeUtil.toMillies(System.nanoTime(), beforeDropSchema) + " dropSchema");

				final long beforeDisconnect = writeTiming ? System.nanoTime() : 0;
				createdSchema.disconnect();
				if(writeTiming)
					System.out.println("" + TimeUtil.toMillies(System.nanoTime(), beforeDisconnect) + " disconnect");
				createdSchema = null;
			}
		}
	}

	public void run()
	{
		dropAndDisconnectIfNeeded(false);
	}
}
