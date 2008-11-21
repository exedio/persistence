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
import java.util.List;

import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.Wrapper;

public final class PasswordRecovery extends Pattern
{
	private static final long NOT_A_TOKEN = 0l;
	
	private final Hash password;
	private final long expiryMillis;
	private final LongField token = new LongField().defaultTo(NOT_A_TOKEN);
	private final DateField date = new DateField().optional();
	private final SecureRandom random = new SecureRandom();

	public PasswordRecovery(final Hash password)
	{
		this(password, 15*60*1000); // 15 minutes
	}
	
	public PasswordRecovery(final Hash password, final long expiryMillis)
	{
		this.password = password;
		this.expiryMillis = expiryMillis;
		if(password==null)
			throw new IllegalArgumentException("password must not be null");
		if(expiryMillis<=0)
			throw new IllegalArgumentException("expiryMillis must be greater zero, but was " + expiryMillis);
		
		addSource(token, "Token");
		addSource(date, "Date");
	}
	
	public Hash getPassword()
	{
		return password;
	}
	
	public long getExpiryMillis()
	{
		return expiryMillis;
	}
	
	public long getExpiryMinutes()
	{
		return expiryMillis / (60*1000);
	}
	
	public LongField getToken()
	{
		return token;
	}
	
	public DateField getDate()
	{
		return date;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("getToken").
			setReturn(long.class));
		result.add(
			new Wrapper("getDate").
			setReturn(Date.class));
		result.add(
			new Wrapper("issue").
			setReturn(long.class, "a valid token for password recovery"));
		result.add(
			new Wrapper("redeem").
			addParameter(long.class, "token", "a token for password recovery").
			setReturn(String.class, "a new password, if the token was valid, otherwise null"));
		
		return Collections.unmodifiableList(result);
	}
	
	public long getToken(final Item item)
	{
		return token.getMandatory(item);
	}
	
	public Date getDate(final Item item)
	{
		return date.get(item);
	}
	
	/**
	 * @return a valid token for password recovery
	 */
	public long issue(final Item item)
	{
		long result = NOT_A_TOKEN;
		while(result==NOT_A_TOKEN)
			result = random.nextLong();
		
		item.set(
			token.map(result),
			date.map(new Date()));
		return result;
	}
	
	/**
	 * @param token a token for password recovery
	 * @return a new password, if the token was valid, otherwise null
	 */
	public String redeem(final Item item, final long token)
	{
		if(token==NOT_A_TOKEN)
			throw new IllegalArgumentException("not a valid token: " + NOT_A_TOKEN);
		
		if(this.token.getMandatory(item)!=token ||
			this.date.get(item).before(new Date(System.currentTimeMillis() - expiryMillis)))
			return null;
		
		final String newPassword = Long.toString(Math.abs(random.nextLong()), 36);
		item.set(
			this.password.map(newPassword),
			this.token.map(NOT_A_TOKEN),
			this.date.map(null));
		return newPassword;
	}
}
