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

import java.util.Date;

import junit.framework.TestCase;

public class IsInitialTest extends TestCase
{
	public void testIsInitial()
	{
		assertEquals(true,  new DataField().isInitial());
		assertEquals(false, new DataField().optional().isInitial());
		assertEquals(true,  new DataField().toFinal().isInitial());
		assertEquals(true,  new DataField().toFinal().optional().isInitial());
		
		assertEquals(true,  new StringField().isInitial());
		assertEquals(false, new StringField().optional().isInitial());
		assertEquals(true,  new StringField().toFinal().isInitial());
		assertEquals(true,  new StringField().toFinal().optional().isInitial());
		assertEquals(false, new StringField().defaultTo("hallo").isInitial());
		assertEquals(false, new StringField().optional().defaultTo("hallo").isInitial());
		assertEquals(false, new StringField().toFinal().defaultTo("hallo").isInitial());
		assertEquals(false, new StringField().toFinal().optional().defaultTo("hallo").isInitial());
		
		assertEquals(true,  new DateField().isInitial());
		assertEquals(false, new DateField().optional().isInitial());
		assertEquals(true,  new DateField().toFinal().isInitial());
		assertEquals(true,  new DateField().toFinal().optional().isInitial());
		assertEquals(false, new DateField().defaultTo(new Date()).isInitial());
		assertEquals(false, new DateField().optional().defaultTo(new Date()).isInitial());
		assertEquals(false, new DateField().toFinal().defaultTo(new Date()).isInitial());
		assertEquals(false, new DateField().toFinal().optional().defaultTo(new Date()).isInitial());
		assertEquals(false, new DateField().defaultToNow().isInitial());
		assertEquals(false, new DateField().optional().defaultToNow().isInitial());
		assertEquals(false, new DateField().toFinal().defaultToNow().isInitial());
		assertEquals(false, new DateField().toFinal().optional().defaultToNow().isInitial());
	}
}
