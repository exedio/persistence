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

package com.exedio.cope.pattern;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class PasswordRecovery extends Pattern
{
	private static final long NOT_A_TOKEN = 0l;
	
	private final Hash password;
	
	ItemField<?> parent = null;
	PartOf<?> tokens = null;
	private final LongField token = new LongField().toFinal();
	private final DateField expires = new DateField().toFinal();
	Type<Token> tokenType = null;
	
	private final SecureRandom random = new SecureRandom();

	public PasswordRecovery(final Hash password)
	{
		this.password = password;
		if(password==null)
			throw new IllegalArgumentException("password must not be null");
	}
	
	@Override
	protected void initialize()
	{
		final Type<?> type = getType();

		parent = type.newItemField(ItemField.DeletePolicy.CASCADE).toFinal();
		tokens = PartOf.newPartOf(parent);
		final LinkedHashMap<String, com.exedio.cope.Feature> features = new LinkedHashMap<String, com.exedio.cope.Feature>();
		features.put("parent", parent);
		features.put("tokens", tokens);
		features.put("token", token);
		features.put("expires", expires);
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
	
	public LongField getToken()
	{
		return token;
	}
	
	public DateField getExpires()
	{
		return expires;
	}
	
	public Type getTokenType()
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
			addParameter(long.class, "token", "a token for password recovery").
			setReturn(String.class, "a new password, if the token was valid, otherwise null"));
		
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * @return a valid token for password recovery
	 */
	public Token issue(final Item item, final int expiryMillis)
	{
		if(expiryMillis<=0)
			throw new IllegalArgumentException("expiryMillis must be greater zero, but was " + expiryMillis);
		
		long result = NOT_A_TOKEN;
		while(result==NOT_A_TOKEN)
			result = random.nextLong();
		
		return tokenType.newItem(
			Cope.mapAndCast(parent, item),
			token.map(result),
			expires.map(new Date(System.currentTimeMillis() + expiryMillis)));
	}
	
	/**
	 * @param token a token for password recovery
	 * @return a new password, if the token was valid, otherwise null
	 */
	public String redeem(final Item item, final long token)
	{
		if(token==NOT_A_TOKEN)
			throw new IllegalArgumentException("not a valid token: " + NOT_A_TOKEN);
		
		final List<Token> tokens =
			tokenType.search(Cope.and(
				Cope.equalAndCast(this.parent, item),
				this.token.equal(token),
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
	
	public static final class Token extends Item
	{
		private static final long serialVersionUID = 1l;
		
		Token(final SetValue[] setValues, final Type<? extends Item> type)
		{
			super(setValues, type);
			assert type!=null;
		}

		Token(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> type)
		{
			super(reactivationDummy, pk, type);
		}
		
		public PasswordRecovery getPattern()
		{
			return (PasswordRecovery)getCopeType().getPattern();
		}
		
		public Item getParent()
		{
			return getPattern().parent.get(this);
		}
		
		public long getToken()
		{
			return getPattern().token.get(this);
		}
		
		public Date getExpires()
		{
			return getPattern().expires.get(this);
		}
	}
}
