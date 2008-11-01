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

import java.util.Arrays;

public final class CharacterSet
{
	public static final CharacterSet ALPHA = new CharacterSet('A', 'Z', 'a', 'z');
	public static final CharacterSet ALPHA_UPPER = new CharacterSet('A', 'Z');
	public static final CharacterSet ALPHA_LOWER = new CharacterSet('a', 'z');
	public static final CharacterSet ALPHA_NUMERIC = new CharacterSet('0', '9', 'A', 'Z', 'a', 'z');
	public static final CharacterSet ALPHA_UPPER_NUMERIC = new CharacterSet('0', '9', 'A', 'Z');
	public static final CharacterSet ALPHA_LOWER_NUMERIC = new CharacterSet('0', '9', 'a', 'z');
	public static final CharacterSet NUMERIC = new CharacterSet('0', '9');
	public static final CharacterSet DOMAIN = new CharacterSet('-', '.', '0', '9', 'a', 'z');
	public static final CharacterSet EMAIL  = new CharacterSet('-', '.', '0', '9', '@', 'Z', 'a', 'z');
	
	private final char[] set;
	
	public CharacterSet(final char from, final char to)
	{
		this(new char[]{from, to});
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2)
	{
		this(new char[]{from1, to1, from2, to2});
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3});
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4});
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4, from5, to5});
	}
	
	public CharacterSet(final char from1, final char to1, final char from2, final char to2, final char from3, final char to3, final char from4, final char to4, final char from5, final char to5, final char from6, final char to6)
	{
		this(new char[]{from1, to1, from2, to2, from3, to3, from4, to4, from5, to5, from6, to6});
	}
	
	private CharacterSet(final char... set)
	{
		assert set.length%2==0;
		
		char cp = set[0];
		for(int i = 1; i<set.length; i++)
		{
			final char c = set[i];
			if(cp>c)
				throw new IllegalArgumentException("inconsistent character set, character '" + c + "' on position " + i + " is less character '" + cp + "' on position " + (i-1));
			cp = c;
		}
			
		this.set = set;
	}
	
	public boolean contains(final char c)
	{
		for(int i = 0; i<set.length; i+=2)
		{
			if(set[i]>c)
				continue;
			if(set[i+1]>=c)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(!(o instanceof CharacterSet))
			return false;
		
		return Arrays.equals(set, ((CharacterSet)o).set);
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(set);
	}
	
	@Override
	public String toString()
	{
		final StringBuffer bf = new StringBuffer();
		bf.append('[');
		bf.append(set[0]).
			append('-').
			append(set[1]);
		for(int i = 2; i<set.length; i+=2)
		{
			bf.append(',').
				append(set[i]).
				append('-').
				append(set[i+1]);
		}
		bf.append(']');
		
		return bf.toString();
	}
	
	public String getRegularExpression()
	{
		// ^[0-9,a-z,A-Z]*$
		final StringBuilder bf = new StringBuilder();
		bf.append("^[");
		
		boolean prependComma = false;
		
		if(contains('-'))
		{
			bf.append('-');
			prependComma = true;
		}
		
		for(int i = 0; i<set.length; i+=2)
		{
			char from = set[i];
			char to = set[i+1];
			
			if(from=='-')
				from++;
			if(to=='-')
				to--;
			
			if(from<=to)
			{
				if(prependComma)
					bf.append(',');
			
				if(from==to)
					bf.append(from);
				else if(from<to)
					bf.append(from).append('-').append(to);
			
				prependComma = true;
			}
		}
		
		bf.append("]*$");
		return bf.toString();
	}
}
