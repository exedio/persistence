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

import static com.exedio.cope.CheckConstraintItem.TYPE;
import static com.exedio.cope.CheckConstraintItem.alphaLessBeta;
import static com.exedio.cope.CheckConstraintSuperItem.einsGreaterOrEqualZwei;

import java.util.Locale;

import com.exedio.dsmf.SQLRuntimeException;

public class CheckConstraintDataTest extends AbstractRuntimeTest
{
	public CheckConstraintDataTest()
	{
		super(CheckConstraintTest.MODEL);
	}

	public void testSet()
	{
		final CheckConstraintItem item = deleteOnTearDown(new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7));
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setAlpha(5);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(alphaLessBeta, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + alphaLessBeta.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setBeta(4);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(alphaLessBeta, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + alphaLessBeta.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		item.setGamma(7);
		assertIt(102, 101, 103, 4, 5, 7, 7, item);
		
		item.setAlpha(3);
		assertIt(102, 101, 103, 3, 5, 7, 7, item);
		
		item.setBeta(6);
		assertIt(102, 101, 103, 3, 6, 7, 7, item);
	}

	public void testSetSuper()
	{
		final CheckConstraintItem item = deleteOnTearDown(new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7));
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setEins(100);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(einsGreaterOrEqualZwei, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + einsGreaterOrEqualZwei.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setZwei(103);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(einsGreaterOrEqualZwei, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + einsGreaterOrEqualZwei.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		item.setDrei(104);
		assertIt(102, 101, 104, 4, 5, 6, 7, item);
		
		item.setEins(103);
		assertIt(103, 101, 104, 4, 5, 6, 7, item);
		
		item.setZwei(100);
		assertIt(103, 100, 104, 4, 5, 6, 7, item);
	}

	public void testSetMulti()
	{
		final CheckConstraintItem item = deleteOnTearDown(new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7));
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setAlphaBeta(5, 4);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(alphaLessBeta, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + alphaLessBeta.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setBetaGamma(4, 6);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(alphaLessBeta, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + alphaLessBeta.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		item.setAlphaBeta(6, 7);
		assertIt(102, 101, 103, 6, 7, 6, 7, item);
		
		item.setAlphaBeta(4, 5);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		item.setBetaGamma(8, 9);
		assertIt(102, 101, 103, 4, 8, 9, 7, item);
	}

	public void testSetMultiSuper()
	{
		final CheckConstraintItem item = deleteOnTearDown(new CheckConstraintItem(102, 101, 103, 4, 5, 6, 7));
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setEinsZwei(101, 102);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(einsGreaterOrEqualZwei, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + einsGreaterOrEqualZwei.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		try
		{
			item.setZweiDrei(103, 104);
			fail();
		}
		catch(CheckViolationException e)
		{
			assertSame(item, e.getItem());
			assertSame(einsGreaterOrEqualZwei, e.getFeature());
			assertEquals("check violation on " + item.getCopeID() + " for " + einsGreaterOrEqualZwei.getID(), e.getMessage());
		}
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		item.setEinsZwei(106, 105);
		assertIt(106, 105, 103, 4, 5, 6, 7, item);
		
		item.setEinsZwei(102, 101);
		assertIt(102, 101, 103, 4, 5, 6, 7, item);
		
		item.setZweiDrei(100, 105);
		assertIt(102, 100, 105, 4, 5, 6, 7, item);
	}
	
	public void testCreate()
	{
		try
		{
			deleteOnTearDown(new CheckConstraintItem(102, 101, 103, 5, 4, 6, 7));
			fail();
		}
		catch(SQLRuntimeException e)
		{
			assertCheckFailed("insert into \"checkconstraintitem\"", "Check constraint violation CheckConsItem_alpLessBeta", e);
		}
		assertEquals(list(), TYPE.search());
		
		// TODO remove, once CheckViolations are thrown
		model.commit();
		model.deleteSchema();
		model.startTransaction(getClass().getName());
	}
	
	public void testCreateSuper()
	{
		try
		{
			new CheckConstraintItem(101, 102, 103, 4, 5, 6, 7);
			fail();
		}
		catch(SQLRuntimeException e)
		{
			assertCheckFailed("insert into \"checkconstraintsuperitem\"", "Check constraint violation CheConSupIte_eiGreOrEquZw", e);
		}
		assertEquals(list(), TYPE.search());
	}
	
	private static void assertCheckFailed(final String message, final String messageCause, final SQLRuntimeException e)
	{
		assertStartsWith(message, e.getMessage().toLowerCase(Locale.ENGLISH));
		assertStartsWith(messageCause, e.getCause().getMessage());
	}
	
	private static void assertStartsWith(final String expected, final String actual)
	{
		assertTrue(actual, actual.startsWith(expected));
	}
	
	private static void assertIt(
			final Integer eins,
			final Integer zwei,
			final Integer drei,
			final Integer alpha,
			final Integer beta,
			final Integer gamma,
			final Integer delta,
			final CheckConstraintItem item)
	{
		assertEquals(eins,  item.getEins ());
		assertEquals(zwei,  item.getZwei ());
		assertEquals(drei,  item.getDrei ());
		assertEquals(alpha, item.getAlpha());
		assertEquals(beta,  item.getBeta ());
		assertEquals(gamma, item.getGamma());
		assertEquals(delta, item.getDelta());
	}
}
