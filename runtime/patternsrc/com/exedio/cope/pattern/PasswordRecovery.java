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

package com.exedio.cope.pattern;

import static com.exedio.cope.util.InterrupterJobContextAdapter.run;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.Delete;
import com.exedio.cope.util.Interrupter;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.InterrupterJobContextAdapter.Body;

public final class PasswordRecovery extends Pattern
{
	private static final long serialVersionUID = 1l;

	private static final long NOT_A_SECRET = 0l;

	private final Hash password;

	ItemField<?> parent = null;
	PartOf<?> tokens = null;
	final LongField secret = new LongField().toFinal();
	final DateField expires = new DateField().toFinal();
	Type<Token> tokenType = null;

	private final SecureRandom random = new SecureRandom();

	public PasswordRecovery(final Hash password)
	{
		this.password = password;
		if(password==null)
			throw new NullPointerException("password");
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		tokens = PartOf.newPartOf(parent, expires);
		final Features features = new Features();
		features.put("parent", parent);
		features.put("secret", secret);
		features.put("expires", expires);
		features.put("tokens", tokens);
		tokenType = newSourceType(Token.class, features, "Token");
	}

	public Hash getPassword()
	{
		return password;
	}

	public <P extends Item> ItemField<P> getParent(final Class<P> parentClass)
	{
		assert parent!=null;
		return parent.as(parentClass);
	}

	public PartOf getTokens()
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

	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper("issue").
			addParameter(int.class, "expiryMillis", "the time span, after which this token will not be valid anymore, in milliseconds").
			setReturn(Token.class));
		result.add(
			new Wrapper("redeem").
			addParameter(long.class, "secret", "a token secret for password recovery").
			setReturn(String.class, "a new password, if the token was valid, otherwise null"));
		result.add(
			new Wrapper("purge").
			setStatic(false).
			addParameter(Interrupter.class, "interrupter").
			setReturn(int.class, "the number of tokens purged"));
		result.add(
			new Wrapper("purge").
			setStatic(false).
			addParameter(JobContext.class, "ctx"));

		return Collections.unmodifiableList(result);
	}

	/**
	 * @return a valid token for password recovery
	 */
	public Token issue(final Item item, final int expiryMillis)
	{
		if(expiryMillis<=0)
			throw new IllegalArgumentException("expiryMillis must be greater zero, but was " + expiryMillis);

		long secret = NOT_A_SECRET;
		while(secret==NOT_A_SECRET)
			secret = random.nextLong();

		return tokenType.newItem(
			Cope.mapAndCast(parent, item),
			this.secret.map(secret),
			this.expires.map(new Date(System.currentTimeMillis() + expiryMillis)));
	}

	/**
	 * @param secret a token for password recovery
	 * @return a new password, if the token was valid, otherwise null
	 */
	public String redeem(final Item item, final long secret)
	{
		if(secret==NOT_A_SECRET)
			throw new IllegalArgumentException("not a valid secret: " + NOT_A_SECRET);

		final List<Token> tokens =
			tokenType.search(Cope.and(
				Cope.equalAndCast(this.parent, item),
				this.secret.equal(secret),
				this.expires.greaterOrEqual(new Date())));

		if(!tokens.isEmpty())
		{
			final String newPassword = Long.toString(Math.abs(random.nextLong()), 36);
			item.set(this.password.map(newPassword));
			for(final Token t : tokens)
				t.deleteCopeItem();
			return newPassword;
		}

		return null;
	}

	public int purge(final Interrupter interrupter)
	{
		return run(
			interrupter,
			new Body(){public void run(final JobContext ctx)
			{
				purge(ctx);
			}}
		);
	}

	public void purge(final JobContext ctx)
	{
		Delete.delete(
				tokenType.newQuery(this.expires.less(new Date())),
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
	 * @deprecated Use {@link #purge(Interrupter)} instead.
	 */
	@Deprecated
	public int purge(@SuppressWarnings("unused") final Class parentClass, final Interrupter interrupter)
	{
		return purge(interrupter);
	}
}
