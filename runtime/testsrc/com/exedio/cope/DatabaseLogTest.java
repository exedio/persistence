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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DatabaseLogTest extends AbstractRuntimeTest
{
	public DatabaseLogTest()
	{
		super(MatchTest.MODEL);
	}
	
	MatchItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MatchItem());
	}
	
	public void testDatabaseLog()
	{
		final ExpectingDatabaseListener l = new ExpectingDatabaseListener();
		model.setDatabaseListener(l);
		
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		
		final ByteArrayOutputStream o1 = new ByteArrayOutputStream();
		model.setDatabaseLog(true, 0, null, new PrintStream(o1));
		assertTrue(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		assertEquals(null, model.getDatabaseLogSQL());
		assertEquals(0, o1.size());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string1"));
		l.verifyExpectations();
		assertTrue(s(o1), s(o1).indexOf("select")>0);
		item.setText("string1");
		assertTrue(s(o1), s(o1).indexOf("update")>0);
		assertTrue(s(o1), s(o1).indexOf("select")>0);
		
		final ByteArrayOutputStream o2 = new ByteArrayOutputStream();
		model.setDatabaseLog(true, 5000, null, new PrintStream(o2));
		assertTrue(model.isDatabaseLogEnabled());
		assertEquals(5000, model.getDatabaseLogThreshold());
		assertEquals(null, model.getDatabaseLogSQL());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string2"));
		l.verifyExpectations();
		item.setText("string2");
		assertEquals(0, o2.size());
		
		final ByteArrayOutputStream o2a = new ByteArrayOutputStream();
		model.setDatabaseLog(true, 0, "update", new PrintStream(o2a));
		assertTrue(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		assertEquals("update", model.getDatabaseLogSQL());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string2"));
		l.verifyExpectations();
		item.setText("string2");
		assertEquals(0, o2.size());
		assertFalse(s(o2a), s(o2a).indexOf("select")>0);
		assertTrue(s(o2a), s(o2a).indexOf("update")>0);
		o2a.reset();
		assertEquals(0, o2a.size());
		
		final ByteArrayOutputStream o3 = new ByteArrayOutputStream();
		model.setDatabaseLog(false, 60, null, new PrintStream(o3));
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		assertEquals(null, model.getDatabaseLogSQL());
		l.expectSearch(model.getCurrentTransaction(), item.TYPE);
		item.TYPE.search(item.text.equal("string3"));
		l.verifyExpectations();
		item.setText("string3");
		assertEquals(0, o2.size());
		assertEquals(0, o2a.size());
		assertEquals(0, o3.size());
		
		model.setDatabaseListener(null);
		
		try
		{
			model.setDatabaseLog(true, -60, "hallo", null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("threshold must not be negative, but was -60", e.getMessage());
		}
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		assertEquals(null, model.getDatabaseLogSQL());
		
		try
		{
			model.setDatabaseLog(true, 120, "bello", null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("out must not be null", e.getMessage());
		}
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		assertEquals(null, model.getDatabaseLogSQL());
	}
	
	private static final String s(final ByteArrayOutputStream o)
	{
		return new String(o.toByteArray());
	}
}
