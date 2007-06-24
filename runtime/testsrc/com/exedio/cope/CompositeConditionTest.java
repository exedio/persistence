/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;

public class CompositeConditionTest extends CopeAssert
{
	public CompositeConditionTest()
	{
		super();
	}
	
	public void testIt()
	{
		try
		{
			Cope.and((Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions must not be null", e.getMessage());
		}
		try
		{
			Cope.and((List<Condition>)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null/*TODO*/, e.getMessage());
		}
		try
		{
			Cope.or((Condition[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("conditions must not be null", e.getMessage());
		}
		try
		{
			Cope.or((List<Condition>)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null/*TODO*/, e.getMessage());
		}
		try
		{
			Cope.and(new Condition[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.and(Collections.<Condition>emptyList());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.or(new Condition[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			Cope.or(Collections.<Condition>emptyList());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
	}
}
