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

import java.util.Date;
import java.util.Map;
import java.util.Properties;

final class RevisionInfoMutex extends RevisionInfo
{
	private final int expectedNumber;
	private final int actualNumber;
	
	RevisionInfoMutex(
			final Date date, final Map<String, String> environment,
			final int expectedNumber, final int actualNumber)
	{
		super(Revisions.MUTEX_NUMBER, date, environment);
		
		if(expectedNumber<0)
			throw new IllegalArgumentException("expectedNumber must be greater or equal zero, but was " + expectedNumber);
		if(actualNumber>=expectedNumber)
			throw new IllegalArgumentException("expectedNumber must be greater than " + actualNumber + ", but was " + expectedNumber);
		
		this.expectedNumber = expectedNumber;
		this.actualNumber = actualNumber;
	}
	
	int getExpectedNumber()
	{
		return expectedNumber;
	}
	
	int getActualNumber()
	{
		return actualNumber;
	}
	
	private static final String MUTEX    = "mutex";
	private static final String EXPECTED = "mutex.expected";
	private static final String ACTUAL   = "mutex.actual";
	
	@Override
	Properties getStore()
	{
		final Properties store = super.getStore();
		store.setProperty(MUTEX, Boolean.TRUE.toString());
		store.setProperty(EXPECTED, String.valueOf(expectedNumber));
		store.setProperty(ACTUAL,   String.valueOf(actualNumber));
		return store;
	}
	
	static final RevisionInfoMutex read(
			final Date date,
			final Map<String, String> environment,
			final Properties p)
	{
		if(p.getProperty(MUTEX)==null)
			return null;
		
		return new RevisionInfoMutex(
				date,
				environment,
				Integer.valueOf(p.getProperty(EXPECTED)),
				Integer.valueOf(p.getProperty(ACTUAL)));
	}
}
