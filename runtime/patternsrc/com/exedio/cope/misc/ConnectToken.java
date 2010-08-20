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

package com.exedio.cope.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;

public final class ConnectToken
{
	private final Manciple manciple;
	private final Model model;
	private final int id;
	private final long issueDate = System.currentTimeMillis();
	private final String name;
	private final boolean conditional;
	private final boolean didConnect;
	private volatile boolean returned = false;
	private final Object returnedLock = new Object();

	ConnectToken(
			final Manciple manciple,
			final Model model,
			final int id,
			final String name,
			final boolean conditional,
			final boolean didConnect)
	{
		assert manciple!=null;
		assert model!=null;
		assert id>=0;
		assert !(conditional && didConnect) : name;

		this.manciple = manciple;
		this.model = model;
		this.id = id;
		this.name = name;
		this.conditional = conditional;
		this.didConnect = didConnect;

		if(Model.isLoggingEnabled())
		{
			final StringBuilder bf = new StringBuilder();
			bf.append("ConnectToken ").
				append(Integer.toString(System.identityHashCode(model), Character.MAX_RADIX)).
				append(": issued ").append(id);
			if(name!=null)
				bf.append(" (").
					append(name).
					append(')');
			if(conditional)
				bf.append(" conditional");
			if(didConnect)
				bf.append(" CONNECT");
			System.out.println(bf.toString());
		}
	}

	void onReturn(final boolean disconnect)
	{
		if(disconnect)
			model.disconnect();

		if(Model.isLoggingEnabled())
		{
			final StringBuilder bf = new StringBuilder();
			bf.append("ConnectToken ").
				append(Integer.toString(System.identityHashCode(model), Character.MAX_RADIX)).
				append(": returned ").append(id);
			if(name!=null)
				bf.append(" (").
					append(name).
					append(')');
			if(disconnect)
				bf.append(" DISCONNECT");
			System.out.println(bf.toString());
		}
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

	/**
	 * Returns true, if this token was issued by {@link #issueIfConnected(Model, String)}.
	 * Returns false, if this token was issued by {@link #issue(Model, ConnectProperties, String)}.
	 */
	public boolean wasConditional()
	{
		return conditional;
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

		Manciple()
		{
			// make constructor non-private
		}

		ConnectToken issue(
				final Model model,
				final ConnectProperties properties,
				final String tokenName)
		{
			synchronized(lock)
			{
				final boolean connect = tokens.isEmpty();
				if(connect)
					model.connect(properties);
				else
					model.getConnectProperties().ensureEquality(properties);

				final ConnectToken result = new ConnectToken(this, model, nextId++, tokenName, false, connect);
				tokens.add(result);
				return result;
			}
		}

		ConnectToken issueIfConnected(
				final Model model,
				final String tokenName)
		{
			synchronized(lock)
			{
				if(tokens.isEmpty())
					return null;

				final ConnectToken result = new ConnectToken(this, model, nextId++, tokenName, true, false);
				tokens.add(result);
				return result;
			}
		}

		boolean returnIt(final ConnectToken token)
		{
			synchronized(lock)
			{
				final boolean removed = tokens.remove(token);
				assert removed;
				final boolean result = tokens.isEmpty();
				token.onReturn(result);
				return result;
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
	public static final ConnectToken issue(
			final Model model,
			final ConnectProperties properties,
			final String tokenName)
	{
		return manciple(model).issue(model, properties, tokenName);
	}

	/**
	 * Issues a ConnectToken, if the model is already connected.
	 * Otherwise the method returns null.
	 * <p>
	 * Usually you may want to use this method, if you want to do something
	 * if the model is already connected, but in that case with a guarantee,
	 * that the model is not disconnected while doing those things.
	 * <p>
	 * Tokens returned by this method always do have {@link #didConnect()}==false.
	 * @see #wasConditional()
	 */
	public static final ConnectToken issueIfConnected(
			final Model model,
			final String tokenName)
	{
		return manciple(model).issueIfConnected(model, tokenName);
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
