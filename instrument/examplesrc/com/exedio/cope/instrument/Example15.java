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

package com.exedio.cope.instrument;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class Example15
{
	private String name;
	public HashSet<Date> dates;
	HashMap<Integer, Boolean> primes = new HashMap();

	public Example15(final HashSet<Date> dates, HashMap<Integer, Boolean> primes)
	{
		this.dates = dates;
		this.primes = primes;
	}
	
	@SuppressWarnings("unchecked")
	public void set(HashSet<Date> dates, final HashMap<Integer, Boolean> primes)
	{
		this.dates = dates;
		this.primes = primes;
	}

	private HashSet<Date> getDates()
	{
		return dates;
	}

	@  SuppressWarnings
	( "unchecked" )
	HashMap<Integer, Boolean> getPrimes()
	{
		return primes;
	}
	
	static enum Color
	{
		blue,
		green;
	}

	static enum Weekday
	{
		monday("mon"),
		tuesday("tue");
		
		final String shortName;
		
		Weekday(final String shortName)
		{
			this.shortName = shortName;
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		return true;
	}

}
