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

package com.exedio.cope;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Revisions
{
	private final int number;
	private final Revision[] revisions;
	
	public Revisions(final int number)
	{
		if(number<0)
			throw new IllegalArgumentException("revision number must not be negative, but was " + number);
		
		this.number = number;
		this.revisions = new Revision[0];
	}
	
	public Revisions(final Revision... revisions)
	{
		if(revisions==null)
			throw new NullPointerException("revisions");
		if(revisions.length==0)
			throw new IllegalArgumentException("revisions must not be empty");
		
		// make a copy to avoid modifications afterwards
		final Revision[] revisionsCopy = new Revision[revisions.length];
		
		int base = -1;
		for(int i = 0; i<revisions.length; i++)
		{
			final Revision revision = revisions[i];
			if(revision==null)
				throw new NullPointerException("revisions" + '[' + i + ']');
			
			if(i==0)
				base = revision.number;
			else
			{
				final int expectedNumber = base-i;
				if(revision.number!=expectedNumber)
					throw new IllegalArgumentException("inconsistent revision number at index " + i + ", expected " + expectedNumber + ", but was " + revision.number);
			}
			
			revisionsCopy[i] = revision;
		}
		
		this.number = revisions[0].number;
		this.revisions = revisionsCopy;
	}
	
	public int getNumber()
	{
		return number;
	}

	public List<Revision> getList()
	{
		return Collections.unmodifiableList(Arrays.asList(revisions));
	}
	
	List<Revision> getListToRun(final int departureNumber)
	{
		if(departureNumber==number)
			return Collections.emptyList();
		if(departureNumber>number)
			throw new IllegalArgumentException("cannot revise backwards, expected " + number + ", but was " + departureNumber);
		
		final int startIndex = number - departureNumber - 1;
		if(startIndex>=revisions.length)
			throw new IllegalArgumentException(
					"attempt to revise from " + departureNumber + " to " + number +
					", but declared revisions allow from " + (number - revisions.length) + " only");
		
		final Revision[] result = new Revision[number - departureNumber];
		int resultIndex = 0;
		for(int i = startIndex; i>=0; i--)
			result[resultIndex++] = revisions[i];
		
		return Collections.unmodifiableList(Arrays.asList(result));
	}
}
