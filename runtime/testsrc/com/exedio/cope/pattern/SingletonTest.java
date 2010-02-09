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

package com.exedio.cope.pattern;

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;

public final class SingletonTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(SingletonItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(SingletonTest.class, "MODEL");
	}
	
	public SingletonTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		// test model
		SingletonItem item = null;
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.integer,
				item.integer55,
				item.booleanField,
				item.einzigartig,
				item.einzigartig.getSource(),
				item.einzigartig.getSource().getImplicitUniqueConstraint(),
			}), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.integer,
				item.integer55,
				item.booleanField,
				item.einzigartig,
				item.einzigartig.getSource(),
				item.einzigartig.getSource().getImplicitUniqueConstraint(),
			}), item.TYPE.getDeclaredFeatures());

		assertEquals(item.TYPE, item.einzigartig.getSource().getType());
		assertEquals(item.TYPE, item.einzigartig.getType());
		assertEquals("einzigartigOnce", item.einzigartig.getSource().getName());
		assertEquals("einzigartig",     item.einzigartig.getName());

		assertEquals(item.einzigartig, item.einzigartig.getSource().getPattern());
		
		assertFalse(item.einzigartig.            isAnnotationPresent(Computed.class));
		assertTrue (item.einzigartig.getSource().isAnnotationPresent(Computed.class));
		
		assertSerializedSame(item.einzigartig, 387);
		
		// test persistence
		assertEquals(list(), item.TYPE.search());
		
		final SingletonItem theOne = deleteOnTearDown(SingletonItem.instance());
		assertEquals(list(theOne), item.TYPE.search());
		assertEquals(null, theOne.getInteger());
		assertEquals(55, theOne.getInteger55());
		assertEquals(true, theOne.getBooleanField());
		assertEquals(theOne, SingletonItem.instance());
	}
}
