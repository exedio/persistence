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

package com.exedio.cope.console;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfoCreate;

public class RevisionLineTest extends TestCase
{
	private static final Date DATE = new Date(2874526134l);
	private static final String DATE_STRING = "1970/02/03 06:28:46.134";
	
	public void testBad() throws UnsupportedEncodingException
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertSame(null, l.getRevision());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		
		final Revision r = new Revision(55, "comment55", "sql55.1", "sql55.2");
		l.setRevision(r);
		assertSame(r, l.getRevision());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		
		l.setInfo("#migrationlogv01\nkey1=value1\nkey2=value2".getBytes("latin1"));
		assertEquals("#migrationlogv01\nkey1=value1\nkey2=value2", l.getLogString());
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		assertEquals(map, l.getLogProperties());
	}
	
	public void testCreate()
	{
		final RevisionLine l = new RevisionLine(55);
		assertEquals(55, l.number);
		assertSame(null, l.getRevision());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		
		final Revision r = new Revision(55, "comment55", "sql55.1", "sql55.2");
		l.setRevision(r);
		assertSame(r, l.getRevision());
		assertEquals(null, l.getLogString());
		assertEquals(null, l.getLogProperties());
		
		l.setInfo(new RevisionInfoCreate(55, DATE, Collections.<String, String>emptyMap()).toBytes());
		assertTrue(l.getLogString(), l.getLogString().startsWith("#migrationlogv01\n"));
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("create", "true");
		map.put("dateUTC", DATE_STRING);
		map.put("revision", "55");
		assertEquals(map, l.getLogProperties());
	}
}
