/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConnectToken implements AutoCloseable
{
	private static final Logger logger = LoggerFactory.getLogger(ConnectToken.class);
	private static final ConnectToken[] EMPTY_CONNECT_TOKEN_ARRAY = new ConnectToken[0];

	private final Manciple manciple;
	private final Model model;
	private final int id;
	private final Instant issueDate = Instant.now();
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
			logger.info(
					"{}: {} {} ({})", new Object[] {
						model,
						didConnect ? "connected" : conditional ? "issued conditionally" : "issued",
						id, name } );
	}

	void onReturn(final boolean disconnect)
	{
		if(disconnect)
			model.disconnect();

		if(logger.isInfoEnabled())
			logger.info(
					"{}: {} {} ({})", new Object[] {
							model,
							disconnect ? "disconnected" : "returned",
							id, name } );
	}

	public Model getModel()
	{
		return model;
	}

	public int getID()
	{
		return id;
	}

	public Instant getIssueInstant()
	{
		return issueDate;
	}

	public Date getIssueDate()
	{
		return Date.from(issueDate);
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Returns true, if this token was issued by {@link #issueIfConnected(Model, String)}.
	 * Returns false, if this token was issued by {@link #issue(Model, String)}.
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
			throw alreadyReturned();

		return manciple.returnIt(this);
	}

	private IllegalStateException alreadyReturned()
	{
		return new IllegalStateException("connect token " + id + " already returned");
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
	public void close()
	{
		returnItConditionally();
	}

	/**
	 * Calls {@code target}.
	 * If {@code target} fails with an exception,
	 * this token is {@link #returnStrictly() returned}.
	 */
	public ConnectToken returnOnFailureOf(final Consumer<ConnectToken> target)
	{
		if(isReturned())
			throw alreadyReturned();

		boolean mustReturn = true;
		try
		{
			target.accept(this);
			mustReturn = false;
		}
		finally
		{
			if(mustReturn)
				returnStrictly();
		}
		// DO NOT WRITE ANYTHING HERE,
		// OTHERWISE ConnectTokens MAY BE LOST
		return this;
	}

	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append(model).
			append('/').append(id);
		if(name!=null)
			bf.append('(').
				append(name).
				append(')');
		return bf.toString();
	}

	private static final class Manciple
	{
		private final Supplier<ConnectProperties> propertiesSupplier;
		private final ArrayList<ConnectToken> tokens = new ArrayList<>();
		private int nextId = 0;
		private final Object lock = new Object();

		Manciple(final Supplier<ConnectProperties> properties)
		{
			assert properties!=null;
			this.propertiesSupplier = properties;
		}

		ConnectProperties properties(final Model model)
		{
			final ConnectProperties result = propertiesSupplier.get();
			if(result==null)
				throw new NullPointerException(
						"ConnectToken properties supplier for " + model + " returned null: " + propertiesSupplier);
			return result;
		}

		ConnectToken issue(
				final Model model,
				final String tokenName)
		{
			synchronized(lock)
			{
				final boolean connect = tokens.isEmpty();
				if(connect)
					model.connect(properties(model));

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
				result = tokens.toArray(EMPTY_CONNECT_TOKEN_ARRAY);
			}
			return List.of(result);
		}
	}

	private static final HashMap<Model, Manciple> manciples = new HashMap<>();

	/**
	 * Sets the connect properties for the given model.
	 * Enables usage of
	 * {@link #issue(Model, String)},
	 * {@link #issueIfConnected(Model, String)},
	 * {@link #getTokens(Model)}.
	 * @see #setProperties(Model, Supplier)
	 * @see #removePropertiesVoid(Model)
	 */
	public static void setProperties(
			final Model model,
			final ConnectProperties properties)
	{
		requireNonNull(model, "model");
		requireNonNull(properties, "properties");

		setProperties(model, () -> properties);
	}

	/**
	 * Sets the connect properties for the given model.
	 * Enables usage of
	 * {@link #issue(Model, String)},
	 * {@link #issueIfConnected(Model, String)},
	 * {@link #getTokens(Model)}.
	 * @see #setProperties(Model, ConnectProperties)
	 * @see #removePropertiesVoid(Model)
	 */
	public static void setProperties(
			final Model model,
			final Supplier<ConnectProperties> properties)
	{
		requireNonNull(model, "model");
		requireNonNull(properties, "properties");

		synchronized(manciples)
		{
			if(manciples.putIfAbsent(model, new Manciple(properties))!=null)
				throw new IllegalStateException("Properties already set for model " + model + '.');
		}
	}

	public static ConnectProperties getProperties(final Model model)
	{
		requireNonNull(model, "model");

		final Manciple manciple;

		synchronized(manciples)
		{
			manciple = manciples.get(model);
		}

		return manciple!=null ? manciple.properties(model) : null;
	}

	/**
	 * @see #setProperties(Model, ConnectProperties)
	 */
	public static void removePropertiesVoid(final Model model)
	{
		requireNonNull(model, "model");

		synchronized(manciples)
		{
			manciples.remove(model);
		}
	}

	/**
	 * @deprecated
	 * Use {@link #removePropertiesVoid(Model) removePropertiesVoid} instead.
	 * This method may fail at computing the result if used together with
	 * {@link #setProperties(Model, Supplier)} and the supplier fails.
	 * @see #setProperties(Model, ConnectProperties)
	 */
	@Deprecated
	public static ConnectProperties removeProperties(final Model model)
	{
		requireNonNull(model, "model");

		final Manciple manciple;

		synchronized(manciples)
		{
			manciple = manciples.remove(model);
		}

		// BEWARE:
		// may fail if used together with #setProperties(Model, Supplier) and the supplier fails
		return manciple!=null ? manciple.properties(model) : null;
	}

	private static Manciple manciple(final Model model)
	{
		requireNonNull(model, "model");

		final Manciple result;
		synchronized(manciples)
		{
			result = manciples.get(model);
		}
		if(result==null)
			throw new IllegalStateException("No properties set for model " + model + ", use ConnectToken.setProperties.");

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
	public static ConnectToken issue(
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
	public static ConnectToken issueIfConnected(
			final Model model,
			final String tokenName)
	{
		return manciple(model).issueIfConnected(model, tokenName);
	}

	/**
	 * Returns the collection of open {@code ConnectToken}s
	 * on the model.
	 * <p>
	 * Returns an unmodifiable snapshot of the actual data,
	 * so iterating over the collection on a live server cannot cause
	 * {@link java.util.ConcurrentModificationException}s.
	 */
	public static List<ConnectToken> getTokens(final Model model)
	{
		return manciple(model).getTokens();
	}
}
