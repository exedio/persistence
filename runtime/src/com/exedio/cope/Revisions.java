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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Revisions
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
	
	public Revisions(final Revision[] revisions)
	{
		if(revisions==null)
			throw new NullPointerException("revisions");
		if(revisions.length==0)
			throw new IllegalArgumentException("revisions must not be empty");
		
		// make a copy to avoid modifications afterwards
		final Revision[] result = new Revision[revisions.length];
		System.arraycopy(revisions, 0, result, 0, revisions.length);
		
		int base = -1;
		for(int i = 0; i<result.length; i++)
		{
			final Revision revision = result[i];
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
		}
		
		this.number = revisions[0].number;
		this.revisions = revisions;
	}
	
	public int getNumber()
	{
		return number;
	}

	public List<Revision> getRevisions()
	{
		return Collections.unmodifiableList(Arrays.asList(revisions));
	}
	
	List<Revision> getRevisionsToRun(final int targetNumber)
	{
		if(targetNumber==number)
			return Collections.emptyList();
		if(targetNumber>number)
			throw new IllegalArgumentException("cannot revise backwards, expected " + number + ", but was " + targetNumber);
		
		{
			final ArrayList<Revision> result = new ArrayList<Revision>();
			final int startRevisionIndex = number - targetNumber - 1;
			if(startRevisionIndex>=revisions.length)
				throw new IllegalArgumentException(
						"attempt to revise from " + targetNumber + " to " + number +
						", but declared revisions allow from " + (number - revisions.length) + " only");
			
			for(int revisionIndex = startRevisionIndex; revisionIndex>=0; revisionIndex--)
			{
				final Revision revision = revisions[revisionIndex];
				assert revision.number == (number - revisionIndex);
				result.add(revision);
			}
			
			return result;
		}
	}
}
