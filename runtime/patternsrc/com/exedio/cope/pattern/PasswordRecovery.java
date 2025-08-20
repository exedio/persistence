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

import static com.exedio.cope.Cope.mapAndCast;
import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.pattern.FeatureCounter.counter;
import static com.exedio.cope.pattern.FeatureTimer.timer;
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
import java.io.Serial;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class PasswordRecovery extends Pattern
{
	@Serial
	private static final long serialVersionUID = 1l;

	private static final long NOT_A_SECRET = 0l;

	@Deprecated
	private final HashInterface password;

	ItemField<?> parent = null;
	PartOf<?> tokens = null;
	final LongField secret = new LongField().toFinal();
	final DateField expires = new DateField().toFinal();
	Type<Token> tokenType = null;

	private final SecureRandom random;

	public PasswordRecovery(final HashInterface password)
	{
		this(password, new SecureRandom());
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
		tokenType = newSourceType(Token.class, Token::new, features, "Token");

		FeatureMeter.onMount(this, issueCounter, issueReuseCounter, redeemTimer, redeemFailCounter, setPasswordCounter);
	}

	/**
	 * @deprecated
	 * This method is needed to support the recently deprecated {@link PasswordRecovery#redeem(Item, long)} only.
	 * Therefore it is deprecated as well.
	 */
	@Deprecated
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
					parent.isCasted(item),
					expires.greaterOrEqual(new Date(now + expiry - reuse))));
			tokens.setOrderBy(expires, false);
			tokens.setPage(0, 1);
			final Token token = tokens.searchSingleton();
			if(token!=null)
			{
				issueReuseCounter.increment();
				return token;
			}
		}

		long secret = NOT_A_SECRET;
		while(secret==NOT_A_SECRET)
			secret = random.nextLong();

		issueCounter.increment();
		return tokenType.newItem(
			mapAndCast(parent, item),
			map(this.secret, secret),
			map(this.expires, new Date(now + expiry)));
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

		final Date now = Clock.newDate();
		final List<Token> tokens =
			tokenType.search(Cope.and(
				this.parent.isCasted(item),
				this.secret.is(secret),
				this.expires.greaterOrEqual(now)));

		if(!tokens.isEmpty())
		{
			final Token token = tokens.get(0);
			redeemTimer.record(token.getExpires().getTime() - now.getTime(), TimeUnit.MILLISECONDS);
			return token;
		}

		redeemFailCounter.increment();
		return null;
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a new password, if the secret was valid, otherwise null
	 * @deprecated
	 * This method has been deprecated because it is needed for single-step token redemption.
	 * In that single step, both the token is redeemed and the password is set to a random value.
	 * Which is generally a bad idea, because mail filters following links contained in the mail may set the new password.
	 * Implement a two-step redemption using {@link #getValidToken(Item, long)} instead.
	 */
	@Deprecated
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

	@SuppressWarnings("ClassCanBeRecord")
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
				100, // TODO allow customization
				"PasswordRecovery#purge " + getID(),
				ctx);
	}

	@Computed
	public static final class Token extends Item
	{
		@Serial
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
					passwordRecovery.parent.isCasted(getParent()),
					passwordRecovery.secret.is(getSecret()),
					passwordRecovery.expires.greaterOrEqual(Clock.newDate())));
			for(final Token t : tokens)
				t.deleteCopeItem();
		}

		/**
		 * @deprecated for the same reason that lead to the deprecation of {@link PasswordRecovery#redeem(Item, long)}.
		 */
		@Deprecated
		public String redeemAndSetNewPassword()
		{
			final Item parent = getParent();
			redeem();
			final PasswordRecovery passwordRecovery = getPattern();
			final HashInterface password = passwordRecovery.password;
			final String newPassword = password.newRandomPassword(passwordRecovery.random);
			password.set(parent, newPassword);
			passwordRecovery.setPasswordCounter.increment();
			return newPassword;
		}
	}

	private final FeatureCounter issueCounter = counter("issue", "A token was issued.", "reuse", "no");
	private final FeatureCounter issueReuseCounter = issueCounter.newValue("yes");
	private final FeatureTimer redeemTimer = timer("redeem", "A token was redeemed the measured time before expiry.");
	private final FeatureCounter redeemFailCounter = counter("redeemFail", "An attempt to redeem a secret failed, because either there was no token with such a secret or that token was expired.");
	private final FeatureCounter setPasswordCounter = counter("setPassword", "The password was set to a new value, because a token was redeemed.");
}
