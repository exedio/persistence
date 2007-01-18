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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;



public class EnumTest extends AbstractLibTest
{
	static final Model MODEL = new Model(EnumItem.TYPE, EnumItem2.TYPE);

	EnumItem item;
	EnumItem2 item2;
	
	private static final EnumItem.Status status1 = EnumItem.Status.status1;
	private static final EnumItem2.Status state1 = EnumItem2.Status.state1;
	
	public EnumTest()
	{
		super(MODEL);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new EnumItem(EnumItem.Status.status1));
		deleteOnTearDown(item2 = new EnumItem2(EnumItem2.Status.state1));
	}

	public void testIt()
	{
		assertEquals(EnumItem.Status.class, item.status.getValueClass());
		assertEquals(EnumItem2.Status.class, item2.status.getValueClass());
		assertEquals(status1, item.getStatus());
		assertEquals(state1, item2.getStatus());
	}
	
	public void testDatabaseLog()
	{
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		
		final ByteArrayOutputStream o1 = new ByteArrayOutputStream();
		model.setDatabaseLog(true, 0, new PrintStream(o1));
		assertTrue(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		assertEquals(0, o1.size());
		item.TYPE.search();
		assertTrue(s(o1), s(o1).indexOf("select")>0);
		item.setStatus(EnumItem.Status.status3);
		assertTrue(s(o1), s(o1).indexOf("update")>0);
		
		final ByteArrayOutputStream o2 = new ByteArrayOutputStream();
		model.setDatabaseLog(true, 5000, new PrintStream(o2));
		assertTrue(model.isDatabaseLogEnabled());
		assertEquals(5000, model.getDatabaseLogThreshold());
		item.TYPE.search();
		item.setStatus(EnumItem.Status.status2);
		assertEquals(0, o2.size());
		
		final ByteArrayOutputStream o3 = new ByteArrayOutputStream();
		model.setDatabaseLog(false, 60, new PrintStream(o3));
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		item.TYPE.search();
		item.setStatus(EnumItem.Status.status1);
		assertEquals(0, o2.size());
		assertEquals(0, o3.size());
		
		try
		{
			model.setDatabaseLog(true, -60, null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("threshold must not be negative, but was -60", e.getMessage());
		}
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
		
		try
		{
			model.setDatabaseLog(true, 120, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("out must not be null", e.getMessage());
		}
		assertFalse(model.isDatabaseLogEnabled());
		assertEquals(0, model.getDatabaseLogThreshold());
	}
	
	private static final String s(final ByteArrayOutputStream o)
	{
		return new String(o.toByteArray());
	}
}
