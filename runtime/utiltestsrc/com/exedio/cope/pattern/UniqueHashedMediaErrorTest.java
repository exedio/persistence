/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.TypesBound.newType;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DataField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;

public class UniqueHashedMediaErrorTest extends CopeAssert
{
	public void testNotFinal()
	{
		try
		{
			new UniqueHashedMedia(new Media());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Media template must be final", e.getMessage());
		}
	}

	public void testOptional()
	{
		try
		{
			new UniqueHashedMedia(new Media().toFinal().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Media template must be mandatory", e.getMessage());
		}
	}


	public void testAbstract()
	{
		try
		{
			newType(AbstractItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"UniqueHashedMedia AbstractItem.value does not allow abstract type AbstractItem",
					e.getMessage());
		}
	}

	static abstract class AbstractItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media().toFinal());

		private static final long serialVersionUID = 1l;
		private AbstractItem(final ActivationParameters ap) { super(ap); }
	}


	public void testNonCreateableFunctionField()
	{
		try
		{
			new Model(NonCreateableFunctionFieldItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"UniqueHashedMedia NonCreateableFunctionFieldItem.value cannot create instances of type NonCreateableFunctionFieldItem, " +
					"because NonCreateableFunctionFieldItem.field is mandatory and has no default.",
					e.getMessage());
		}
	}
	static class NonCreateableFunctionFieldItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media().toFinal());
		static final IntegerField field = new IntegerField();
		static final Type<NonCreateableFunctionFieldItem> TYPE =
				TypesBound.newType(NonCreateableFunctionFieldItem.class);

		private static final long serialVersionUID = 1l;
		private NonCreateableFunctionFieldItem(final ActivationParameters ap) { super(ap); }
	}


	public void testNonCreateableDataField()
	{
		try
		{
			new Model(NonCreateableDataFieldItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"UniqueHashedMedia NonCreateableDataFieldItem.value cannot create instances of type NonCreateableDataFieldItem, " +
					"because NonCreateableDataFieldItem.field is mandatory and has no default.",
					e.getMessage());
		}
	}
	static class NonCreateableDataFieldItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media().toFinal());
		static final IntegerField field = new IntegerField();
		static final Type<NonCreateableDataFieldItem> TYPE =
				TypesBound.newType(NonCreateableDataFieldItem.class);

		private static final long serialVersionUID = 1l;
		private NonCreateableDataFieldItem(final ActivationParameters ap) { super(ap); }
	}


	public void testCreateable()
	{
		// test, that is does not throw an exception
		new Model(CreateableItem.TYPE);
	}
	static class CreateableItem extends Item
	{
		static final UniqueHashedMedia value = new UniqueHashedMedia(new Media().toFinal());
		static final IntegerField optionalField = new IntegerField().optional();
		static final IntegerField defaultField = new IntegerField().defaultTo(77);
		static final DataField dataField = new DataField().optional();
		static final Type<CreateableItem> TYPE = TypesBound.newType(CreateableItem.class);

		private static final long serialVersionUID = 1l;
		private CreateableItem(final ActivationParameters ap) { super(ap); }
	}
}
