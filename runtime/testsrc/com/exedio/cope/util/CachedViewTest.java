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

import java.util.Locale;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;

public class CachedViewTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(CachedViewItem.TYPE);
	
	CachedViewItem item, item2;
	
	public CachedViewTest()
	{
		super(MODEL);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new CachedViewItem());
		item2 = deleteOnTearDown(new CachedViewItem());
	}
	
	public void testIt()
	{
		assertEquals(0, item.locale.maps);
		assertEquals(0, item2.locale.maps);
		
		assertEquals(null, item.locale.get());
		assertEquals(1, item.locale.maps);
		assertEquals(0, item2.locale.maps);
		
		assertEquals(null, item.locale.get());
		assertEquals(1, item.locale.maps);
		assertEquals(0, item2.locale.maps);
		
		item.setCode("de");
		assertEquals(1, item.locale.maps);
		assertEquals(0, item2.locale.maps);

		assertEquals(Locale.GERMAN, item.locale.get());
		assertEquals(2, item.locale.maps);
		assertEquals(0, item2.locale.maps);

		assertEquals(Locale.GERMAN, item.locale.get());
		assertEquals(2, item.locale.maps);
		assertEquals(0, item2.locale.maps);
		
		item.setCode("en");
		assertEquals(2, item.locale.maps);
		assertEquals(0, item2.locale.maps);

		assertEquals(Locale.ENGLISH, item.locale.get());
		assertEquals(3, item.locale.maps);
		assertEquals(0, item2.locale.maps);

		assertEquals(Locale.ENGLISH, item.locale.get());
		assertEquals(3, item.locale.maps);
		assertEquals(0, item2.locale.maps);
		
		item.setCode(null);
		assertEquals(3, item.locale.maps);
		assertEquals(0, item2.locale.maps);

		assertEquals(null, item.locale.get());
		assertEquals(4, item.locale.maps);
		assertEquals(0, item2.locale.maps);

		assertEquals(null, item.locale.get());
		assertEquals(4, item.locale.maps);
		assertEquals(0, item2.locale.maps);
	}
}
