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

package com.exedio.cope.util;

/**
 * @deprecated Use {@link CharSet} instead
 */
@Deprecated
public final class CharacterSet
{
	public static final CharacterSet ALPHA = new CharacterSet(CharSet.ALPHA);
	public static final CharacterSet ALPHA_UPPER = new CharacterSet(CharSet.ALPHA_UPPER);
	public static final CharacterSet ALPHA_LOWER = new CharacterSet(CharSet.ALPHA_LOWER);
	public static final CharacterSet ALPHA_NUMERIC = new CharacterSet(CharSet.ALPHA_NUMERIC);
	public static final CharacterSet ALPHA_UPPER_NUMERIC = new CharacterSet(CharSet.ALPHA_UPPER_NUMERIC);
	public static final CharacterSet ALPHA_LOWER_NUMERIC = new CharacterSet(CharSet.ALPHA_LOWER_NUMERIC);
	public static final CharacterSet NUMERIC = new CharacterSet(CharSet.NUMERIC);
	public static final CharacterSet DOMAIN = new CharacterSet(CharSet.DOMAIN);
	public static final CharacterSet EMAIL  = new CharacterSet(CharSet.EMAIL);
	
	private final CharSet set;
	
	public CharacterSet(final char from, final char to)
	{
		this(new CharSet(from, to));
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2)
	{
		this(new CharSet(from1, to1, from2, to2));
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3)
	{
		this(new CharSet(from1, to1, from2, to2, from3, to3));
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4)
	{
		this(new CharSet(from1, to1, from2, to2, from3, to3, from4, to4));
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5)
	{
		this(new CharSet(from1, to1, from2, to2, from3, to3, from4, to4, from5, to5));
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5, final char from6, final char to6)
	{
		this(new CharSet(from1, to1, from2, to2, from3, to3, from4, to4, from5, to5, from6, to6));
	}
	
	private CharacterSet(final CharSet set)
	{
		this.set = set;
	}
	
	public boolean contains(final char c)
	{
		return set.contains(c);
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(!(o instanceof CharSet))
			return false;
		
		return set.equals(((CharacterSet)o).set);
	}
	
	@Override
	public int hashCode()
	{
		return set.hashCode() ^ 71246512;
	}
	
	@Override
	public String toString()
	{
		return set.toString();
	}
	
	public String getRegularExpression()
	{
		return set.getRegularExpression();
	}
}
