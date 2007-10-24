/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.Model;

public final class ConnectToken
{
	private final Manciple manciple;
	private final Model model;
	private final int id;
	private final long issueDate = System.currentTimeMillis();
	private final String name;
	private final boolean didConnect;
	private volatile boolean returned = false;
	private final Object returnedLock = new Object();
	
	private ConnectToken(final Manciple manciple, final Model model, final int id, final String name, final boolean didConnect)
	{
		assert manciple!=null;
		assert model!=null;
		assert id>=0;
		
		this.manciple = manciple;
		this.model = model;
		this.id = id;
		this.name = name;
		this.didConnect = didConnect;
	}

	public Model getModel()
	{
		return model;
	}

	public int getID()
	{
		return id;
	}

	public Date getIssueDate()
	{
		return new Date(issueDate);
	}

	public String getName()
	{
		return name;
	}
	
	public boolean didConnect()
	{
		return didConnect;
	}
	
	public boolean isReturned()
	{
		return returned;
	}
	
	public boolean returnIt()
	{
		synchronized(returnedLock)
		{
			if(returned)
				throw new IllegalStateException("connect token " + id + " already returned");
			
			returned = true;
		}
		
		return manciple.returnIt(this);
	}

	private static final class Manciple
	{
		private final ArrayList<ConnectToken> tokens = new ArrayList<ConnectToken>();
		private int nextId = 0;
		private final Object lock = new Object();
		
		private ConnectToken issue(final Model model, final com.exedio.cope.ConnectProperties properties, final String tokenName)
		{
			synchronized(lock)
			{
				final boolean connect = tokens.isEmpty();
				if(connect)
					model.connect(properties);
				else
					model.getProperties().ensureEquality(properties);
					
				final ConnectToken result = new ConnectToken(this, model, nextId++, tokenName, connect);
				tokens.add(result);

				if(Model.isLoggingEnabled())
					System.out.println(
							"ConnectToken " + Integer.toString(System.identityHashCode(model), 36) +
							": issued " + result.id + (tokenName!=null ? (" (" + tokenName + ')') : "") +
							(connect ? " CONNECT" : ""));
				return result;
			}
		}
		
		private boolean returnIt(final ConnectToken token)
		{
			synchronized(lock)
			{
				final boolean removed = tokens.remove(token);
				assert removed;
				final boolean disconnect = tokens.isEmpty();
				if(disconnect)
					token.model.disconnect();

				if(Model.isLoggingEnabled())
					System.out.println(
							"ConnectToken " + Integer.toString(System.identityHashCode(token.model), 36) +
							": returned " + token.id + (token.name!=null ? (" (" + token.name + ')') : "") +
							(disconnect ? " DISCONNECT" : ""));
				return disconnect;
			}
		}

		List<ConnectToken> getTokens()
		{
			final ConnectToken[] result;
			synchronized(lock)
			{
				result = tokens.toArray(new ConnectToken[tokens.size()]);
			}
			return Collections.unmodifiableList(Arrays.asList(result));
		}
	}
	
	private static final HashMap<Model, Manciple> manciples = new HashMap<Model, Manciple>();
	
	private static final Manciple manciple(final Model model)
	{
		synchronized(manciples)
		{
			Manciple result = manciples.get(model);
			if(result!=null)
				return result;

			result = new Manciple();
			manciples.put(model, result);
			return result;
		}
	}
	
	/**
	 * Connects the model to the database described in the properties,
	 * if the model is not already connected.
	 * Can be called multiple times, but only the first time
	 * takes effect.
	 * Any subsequent calls must give properties equal to properties given
	 * on the first call, otherwise a RuntimeException is thrown.
	 * <p>
	 * Usually you may want to use this method, if you want to connect this model
	 * from different servlets with equal properties in an undefined order.
	 *
	 * @throws IllegalArgumentException if a subsequent call provides properties different
	 * 									to the first call.
	 */
	public static final ConnectToken issue(final Model model, final com.exedio.cope.ConnectProperties properties, final String tokenName)
	{
		return manciple(model).issue(model, properties, tokenName);
	}

	/**
	 * Returns the collection of open {@link ConnectToken}s
	 * on the model.
	 * <p>
	 * Returns an unmodifiable snapshot of the actual data,
	 * so iterating over the collection on a live server cannot cause
	 * {@link java.util.ConcurrentModificationException}s.
	 */
	public static final List<ConnectToken> getTokens(final Model model)
	{
		return manciple(model).getTokens();
	}
}
