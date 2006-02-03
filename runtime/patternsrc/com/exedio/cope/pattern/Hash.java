/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.EqualCondition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.JoinedFunction;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.NotEqualCondition;
import com.exedio.cope.Pattern;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.Attribute.Option;

public abstract class Hash extends Pattern
{
	private final StringAttribute storage;

	public Hash(final StringAttribute storage)
	{
		if(storage==null)
			throw new NullPointerException("hash storage must not be null");

		registerSource(this.storage = storage);
	}
	
	public Hash(final Option storageOption)
	{
		this(new StringAttribute(storageOption));
	}

	public void initialize()
	{
		if(!storage.isInitialized())
			initialize(storage, getName()+"Hash");
	}
	
	public final StringAttribute getStorage()
	{
		return storage;
	}
	
	public abstract String hash(String plainText);
	
	public final void set(final Item item, final String plainText)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException
	{
		storage.set(item, hash(plainText));
	}
	
	public final boolean check(final Item item, final String actualPlainText)
	{
		final String expectedHash = storage.get(item);
		final String actualHash = hash(actualPlainText);
		if(expectedHash==null)
			return actualHash==null;
		else
			return expectedHash.equals(actualHash);
	}
	
	public final EqualCondition equal(final String value)
	{
		return new EqualCondition(storage, hash(value));
	}
	
	public final EqualCondition equal(final Join join, final String value)
	{
		return new EqualCondition(new JoinedFunction(storage, join), hash(value));
	}

	public final NotEqualCondition notEqual(final String value)
	{
		return new NotEqualCondition(storage, hash(value));
	}
	
}
