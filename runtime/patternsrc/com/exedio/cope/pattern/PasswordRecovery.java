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

package com.exedio.cope.pattern;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.Delete;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

public final class PasswordRecovery extends Pattern
{
	private static final long serialVersionUID = 1l;

	private static final long NOT_A_SECRET = 0l;

	private final HashInterface password;

	ItemField<?> parent = null;
	PartOf<?> tokens = null;
	final LongField secret = new LongField().toFinal();
	final DateField expires = new DateField().toFinal();
	Type<Token> tokenType = null;

	private final SecureRandom random;

	// for binary backwards compatibility
	public PasswordRecovery(final Hash password)
	{
		this((HashInterface)password);
	}

	public PasswordRecovery(final HashInterface password)
	{
		this(password, new SecureRandom());
	}

	// for binary backwards compatibility
	public PasswordRecovery(final Hash password, final SecureRandom random)
	{
		this((HashInterface)password, random);
	}

	public PasswordRecovery(final HashInterface password, final SecureRandom random)
	{
		this.password = requireNonNull(password, "password");
		this.random = requireNonNull(random, "random");
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		tokens = PartOf.create(parent, expires);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("secret", secret);
		features.put("expires", expires);
		features.put("tokens", tokens);
		tokenType = newSourceType(Token.class, features, "Token");
	}

	public HashInterface getPassword()
	{
		return password;
	}

	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		assert parent!=null;
		return parent.as(parentClass);
	}

	public PartOf<?> getTokens()
	{
		return tokens;
	}

	public LongField getSecret()
	{
		return secret;
	}

	public DateField getExpires()
	{
		return expires;
	}

	public Type<Token> getTokenType()
	{
		return tokenType;
	}

	/**
	 * @return a valid token for password recovery
	 */
	@Wrap(order=10)
	public Token issue(
			final Item item,
			@Parameter("config") final Config config)
	{
		final int expiry = config.getExpiryMillis();
		final int reuse = config.getReuseMillis();
		final long now = Clock.currentTimeMillis();

		if(config.getReuseMillis()>0)
		{
			final Query<Token> tokens =
				tokenType.newQuery(Cope.and(
					Cope.equalAndCast(this.parent, item),
					this.expires.greaterOrEqual(new Date(now + expiry - reuse))));
			tokens.setOrderBy(this.expires, false);
			tokens.setLimit(0, 1);
			final Token token = tokens.searchSingleton();
			if(token!=null)
				return token;
		}

		long secret = NOT_A_SECRET;
		while(secret==NOT_A_SECRET)
			secret = random.nextLong();

		return tokenType.newItem(
			Cope.mapAndCast(parent, item),
			this.secret.map(secret),
			this.expires.map(new Date(now + expiry)));
	}

	/**
	 * @param secret a token for password recovery
	 * @return a new password, if the token was valid, otherwise null
	 */
	@Wrap(order=20, docReturn="a new password, if the token was valid, otherwise null")
	public String redeem(
			final Item item,
			@Parameter(value="secret", doc="a token secret for password recovery") final long secret)
	{
		if(secret==NOT_A_SECRET)
			throw new IllegalArgumentException("not a valid secret: " + NOT_A_SECRET);

		final List<Token> tokens =
			tokenType.search(Cope.and(
				Cope.equalAndCast(this.parent, item),
				this.secret.equal(secret),
				this.expires.greaterOrEqual(new Date(Clock.currentTimeMillis()))));

		if(!tokens.isEmpty())
		{
			final String newPassword = password.newRandomPassword(random);
			item.set(this.password.map(newPassword));
			for(final Token t : tokens)
				t.deleteCopeItem();
			return newPassword;
		}

		return null;
	}

	public static final class Config
	{
		private final int expiryMillis;
		private final int reuseMillis;

		/**
		 * @param expiryMillis the time span, after which this token will not be valid anymore, in milliseconds
		 */
		public Config(final int expiryMillis)
		{
			this(expiryMillis, Math.min(10*1000, expiryMillis));
		}

		/**
		 * @param expiryMillis the time span, after which this token will not be valid anymore, in milliseconds
		 * @param reuseMillis limits the number of tokens created within that time span.
		 *        This is against Denial-Of-service attacks filling up the database.
		 */
		public Config(final int expiryMillis, final int reuseMillis)
		{
			if(expiryMillis<=0)
				throw new IllegalArgumentException("expiryMillis must be greater zero, but was " + expiryMillis);
			if(reuseMillis<0)
				throw new IllegalArgumentException("reuseMillis must be greater or equal zero, but was " + reuseMillis);
			if(reuseMillis>expiryMillis)
				throw new IllegalArgumentException("reuseMillis must not be be greater expiryMillis, but was " + reuseMillis + " and " + expiryMillis);

			this.expiryMillis = expiryMillis;
			this.reuseMillis = reuseMillis;
		}

		public int getExpiryMillis()
		{
			return expiryMillis;
		}

		public int getReuseMillis()
		{
			return reuseMillis;
		}
	}

	@Wrap(order=110)
	public void purge(
			@Parameter("ctx") final JobContext ctx)
	{
		Delete.delete(
				tokenType.newQuery(this.expires.less(new Date(Clock.currentTimeMillis()))),
				"PasswordRecovery#purge " + getID(),
				ctx);
	}

	@Computed
	public static final class Token extends Item
	{
		private static final long serialVersionUID = 1l;

		Token(final ActivationParameters ap)
		{
			super(ap);
		}

		public PasswordRecovery getPattern()
		{
			return (PasswordRecovery)getCopeType().getPattern();
		}

		public Item getParent()
		{
			return getPattern().parent.get(this);
		}

		public long getSecret()
		{
			return getPattern().secret.get(this);
		}

		public Date getExpires()
		{
			return getPattern().expires.get(this);
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #purge(com.exedio.cope.util.Interrupter)} instead.
	 */
	@Deprecated
	public int purge(@SuppressWarnings("unused") final Class<?> parentClass, final com.exedio.cope.util.Interrupter interrupter)
	{
		return purge(interrupter);
	}

	/**
	 * @deprecated Use {@link #purge(JobContext)} instead.
	 */
	@Wrap(order=100, docReturn="the number of tokens purged")
	@Deprecated
	public int purge(
			@Parameter("interrupter") final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			new com.exedio.cope.util.InterrupterJobContextAdapter.Body(){public void run(final JobContext ctx)
			{
				purge(ctx);
			}}
		);
	}

	/**
	 * @deprecated Use {@link #issue(Item, Config)} instead.
	 * @return a valid token for password recovery
	 */
	@Deprecated
	@Wrap(order=11)
	public Token issue(
			final Item item,
			@Parameter(value="expiryMillis", doc="the time span, after which this token will not be valid anymore, in milliseconds") final int expiryMillis)
	{
		return issue(item, new Config(expiryMillis));
	}
}
