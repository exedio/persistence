/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConnectToken
{
	private static final Logger logger = LoggerFactory.getLogger(ConnectToken.class);

	private final Manciple manciple;
	private final Model model;
	private final int id;
	private final long issueDate = System.currentTimeMillis();
	private final String name;
	private final boolean conditional;
	private final boolean didConnect;
	private final AtomicBoolean returned = new AtomicBoolean(false);

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

		if(logger.isInfoEnabled())
			logger.info( MessageFormat.format(
					"{0}: {2} {1} ({3})",
						model, id,
						didConnect ? "connected" : conditional ? "issued conditionally" : "issued",
						name ) );
	}

	void onReturn(final boolean disconnect)
	{
		if(disconnect)
			model.disconnect();

		if(logger.isInfoEnabled())
			logger.info( MessageFormat.format(
					"{0}: {2} {1} ({3})",
							model, id,
							disconnect ? "disconnected" : "returned",
							name ) );
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
		return returned.get();
	}

	/**
	 * @throws IllegalArgumentException
	 * if token was already returned.
	 * You may want to use {@link #returnItConditionally()} instead.
	 */
	public boolean returnStrictly()
	{
		if(returned.getAndSet(true))
			throw new IllegalStateException("connect token " + id + " already returned");

		return manciple.returnIt(this);
	}

	/**
	 * Return false,
	 * if token was already returned.
	 * You may want to use {@link #returnStrictly()} instead.
	 */
	public boolean returnItConditionally()
	{
		if(returned.getAndSet(true))
		{
			logger.warn(
					"{}: returned {} excessively ({})",
					new Object[]{model, id, name});
			return false;
		}

		return manciple.returnIt(this);
	}

	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append(model.toString()).
			append('/').append(id);
		if(name!=null)
			bf.append('(').
				append(name).
				append(')');
		return bf.toString();
	}

	private static final class Manciple
	{
		final ConnectProperties properties;
		private final ArrayList<ConnectToken> tokens = new ArrayList<>();
		private int nextId = 0;
		private final Object lock = new Object();

		Manciple(final ConnectProperties properties)
		{
			assert properties!=null;
			this.properties = properties;
		}

		ConnectToken issue(
				final Model model,
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

	private static final HashMap<Model, Manciple> manciples = new HashMap<>();

	/**
	 * Sets the connect properties for the given model.
	 * Enables usage of
	 * {@link #issue(Model, String)},
	 * {@link #issueIfConnected(Model, String)},
	 * {@link #getTokens(Model)}.
	 * @see #removeProperties(Model)
	 */
	public static final void setProperties(
			final Model model,
			final ConnectProperties properties)
	{
		requireNonNull(model, "model");
		requireNonNull(properties, "properties");

		synchronized(manciples)
		{
			if(manciples.containsKey(model))
				throw new IllegalStateException("Properties already set for model " + model.toString() + '.');

			manciples.put(model, new Manciple(properties));
		}
	}

	public static final ConnectProperties getProperties(final Model model)
	{
		requireNonNull(model, "model");

		final Manciple manciple;

		synchronized(manciples)
		{
			manciple = manciples.get(model);
		}

		return manciple!=null ? manciple.properties : null;
	}

	/**
	 * @see #setProperties(Model, ConnectProperties)
	 */
	public static final ConnectProperties removeProperties(final Model model)
	{
		requireNonNull(model, "model");

		final Manciple manciple;

		synchronized(manciples)
		{
			manciple = manciples.remove(model);
		}

		return manciple!=null ? manciple.properties : null;
	}

	private static final Manciple manciple(final Model model)
	{
		requireNonNull(model, "model");

		final Manciple result;
		synchronized(manciples)
		{
			result = manciples.get(model);
		}
		if(result==null)
			throw new IllegalStateException("No properties set for model " + model.toString() + ", use ConnectToken.setProperties.");

		return result;
	}

	/**
	 * Connects the model to the database described in the properties,
	 * if the model is not already connected.
	 * Can be called multiple times, but only the first time
	 * takes effect.
	 * <p>
	 * Usually you may want to use this method, if you want to connect this model
	 * from different servlets with equal properties in an undefined order.
	 */
	public static final ConnectToken issue(
			final Model model,
			final String tokenName)
	{
		return manciple(model).issue(model, tokenName);
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

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #returnStrictly()} instead
	 */
	@Deprecated
	public boolean returnIt()
	{
		return returnStrictly();
	}

	/**
	 * @param properties is ignored
	 * @deprecated Use {@link #issue(Model, String)} instead. Parameter <tt>properties</tt> is ignored.
	 */
	@Deprecated
	public static final ConnectToken issue(
			final Model model,
			final ConnectProperties properties,
			final String tokenName)
	{
		return issue(model, tokenName);
	}
}
