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

package com.exedio.cope.pattern;

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.util.Check.requireAtLeast;
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
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.Delete;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.JobContext;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
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

		parent = type.newItemField(CASCADE).toFinal();
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

	public <P extends Item> ItemField<P> getParent(@Nonnull final Class<P> parentClass)
	{
		requireParentClass(parentClass, "parentClass");
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
	@Nonnull
	public Token issue(
			@Nonnull final Item item,
			@Nonnull @Parameter("config") final Config config)
	{
		final long expiry = config.getExpiry().toMillis();
		final long reuse = config.getReuse().toMillis();
		final long now = Clock.currentTimeMillis();

		if(reuse>0)
		{
			final Query<Token> tokens =
				tokenType.newQuery(Cope.and(
					Cope.equalAndCast(parent, item),
					expires.greaterOrEqual(new Date(now + expiry - reuse))));
			tokens.setOrderBy(expires, false);
			tokens.setPage(0, 1);
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
	 * @param secret a secret for password recovery
	 * @return a valid token, if existing, otherwise null
	 */
	@Wrap(order=20, name="getValid{0}Token", docReturn="a valid token, if existing, otherwise null")
	@Nullable
	public Token getValidToken(
			@Nonnull final Item item,
			@Parameter(value="secret", doc="a secret for password recovery") final long secret)
	{
		if(secret==NOT_A_SECRET)
			throw new IllegalArgumentException("not a valid secret: " + NOT_A_SECRET);

		final List<Token> tokens =
			tokenType.search(Cope.and(
				Cope.equalAndCast(this.parent, item),
				this.secret.equal(secret),
				this.expires.greaterOrEqual(Clock.newDate())));

		if(!tokens.isEmpty())
		{
			return tokens.get(0);
		}

		return null;
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a new password, if the secret was valid, otherwise null
	 */
	@Wrap(order=30, docReturn="a new password, if the secret was valid, otherwise null")
	@Nullable
	public String redeem(
			@Nonnull final Item item,
			@Parameter(value="secret", doc="a secret for password recovery") final long secret)
	{
		final Token validToken = getValidToken(item, secret);

		if(validToken != null)
		{
			return validToken.redeemAndSetNewPassword();
		}
		else
		{
			return null;
		}
	}

	public static final class Config
	{
		private final Duration expiry;
		private final Duration reuse;

		/**
		 * @param expiry the time span, after which this token will not be valid anymore, in milliseconds
		 * @param reuse limits the number of tokens created within that time span.
		 *        This is against Denial-Of-service attacks filling up the database.
		 */
		public Config(final Duration expiry, final Duration reuse)
		{
			requireAtLeast(expiry, "expiry", Duration.ofMillis(1));
			requireAtLeast(reuse, "reuse", Duration.ZERO);
			if(reuse.compareTo(expiry)>0)
				throw new IllegalArgumentException("reuse must not be be greater expiry, but was " + reuse + " and " + expiry);

			this.expiry = expiry;
			this.reuse = reuse;
		}

		public Duration getExpiry()
		{
			return expiry;
		}

		public Duration getReuse()
		{
			return reuse;
		}

		// ------------------- deprecated stuff -------------------

		/**
		 * @deprecated Use {@link #Config(Duration, Duration)} instead.
		 * @param expiryMillis the time span, after which this token will not be valid anymore, in milliseconds
		 */
		@Deprecated
		public Config(final int expiryMillis)
		{
			this(expiryMillis, Math.min(10*1000, expiryMillis));
		}

		/**
		 * @deprecated Use {@link #Config(Duration, Duration)} instead.
		 * @param expiryMillis the time span, after which this token will not be valid anymore, in milliseconds
		 * @param reuseMillis limits the number of tokens created within that time span.
		 *        This is against Denial-Of-service attacks filling up the database.
		 */
		@Deprecated
		public Config(final int expiryMillis, final int reuseMillis)
		{
			this(Duration.ofMillis(expiryMillis), Duration.ofMillis(reuseMillis));
		}

		/**
		 * @deprecated Use {@link #getExpiry()} instead.
		 *             BEWARE: May fail for large values!
		 */
		@Deprecated
		public int getExpiryMillis()
		{
			return Math.toIntExact(expiry.toMillis());
		}

		/**
		 * @deprecated Use {@link #getReuse()} instead.
		 *             BEWARE: May fail for large values!
		 */
		@Deprecated
		public int getReuseMillis()
		{
			return Math.toIntExact(reuse.toMillis());
		}
	}

	@Wrap(order=110)
	public void purge(
			@Nonnull @Parameter("ctx") final JobContext ctx)
	{
		Delete.delete(
				tokenType.newQuery(expires.less(Clock.newDate())),
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

		public void redeem()
		{
			final PasswordRecovery passwordRecovery = getPattern();
			final List<Token> tokens = passwordRecovery.tokenType.search(Cope.and(
					Cope.equalAndCast(passwordRecovery.parent, getParent()),
					passwordRecovery.secret.equal(getSecret()),
					passwordRecovery.expires.greaterOrEqual(Clock.newDate())));
			for(final Token t : tokens)
				t.deleteCopeItem();
		}

		public String redeemAndSetNewPassword()
		{
			final Item parent = getParent();
			redeem();
			final HashInterface password = getPattern().password;
			final String newPassword = password.newRandomPassword(getPattern().random);
			password.set(parent, newPassword);
			return newPassword;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #issue(Item, Config)} instead.
	 * @return a valid token for password recovery
	 */
	@Deprecated
	@Wrap(order=11)
	@Nonnull
	public Token issue(
			@Nonnull final Item item,
			@Parameter(value="expiryMillis", doc="the time span, after which this token will not be valid anymore, in milliseconds") final int expiryMillis)
	{
		return issue(item, new Config(expiryMillis));
	}
}
