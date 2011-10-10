/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;

public class InstanceOfModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(
			InstanceOfAItem.TYPE,
			InstanceOfB1Item.TYPE,
			InstanceOfB2Item.TYPE,
			InstanceOfC1Item.TYPE,
			InstanceOfRefItem.TYPE);

	public void testAs()
	{
		assertSame(InstanceOfRefItem.ref, InstanceOfRefItem.ref.as(InstanceOfAItem.class));
		try
		{
			InstanceOfRefItem.ref.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + ItemField.class.getName() + '<' + InstanceOfB1Item.class.getName() + ">, " +
					"but was a " + ItemField.class.getName() + '<' + InstanceOfAItem.class.getName() + '>',
				e.getMessage());
		}
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.as(InstanceOfB2Item.class));
		try
		{
			InstanceOfRefItem.refb2.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + ItemField.class.getName() + '<' + InstanceOfB1Item.class.getName() + ">, " +
					"but was a " + ItemField.class.getName() + '<' + InstanceOfB2Item.class.getName() + '>',
				e.getMessage());
		}
		try
		{
			InstanceOfRefItem.refb2.as(InstanceOfAItem.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + ItemField.class.getName() + '<' + InstanceOfAItem.class.getName() + ">, " +
					"but was a " + ItemField.class.getName() + '<' + InstanceOfB2Item.class.getName() + '>',
				e.getMessage());
		}
	}

	public void testGetSubTypes()
	{
		assertEqualsUnmodifiable(list(InstanceOfB1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(), InstanceOfB2Item.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(), InstanceOfC1Item.TYPE.getSubtypes());
	}

	public void testGetSubTypesTransitively()
	{
		assertEqualsUnmodifiable(list(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfB2Item.TYPE), InstanceOfB2Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfC1Item.TYPE), InstanceOfC1Item.TYPE.getSubtypesTransitively());
	}

	public void testTypesOfInstances()
	{
		assertEqualsUnmodifiable(list(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getTypesOfInstances());
		assertEqualsUnmodifiable(list(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfB2Item.TYPE), InstanceOfB2Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfC1Item.TYPE), InstanceOfC1Item.TYPE.getSubtypesTransitively());
	}
}
