/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypeFuture;
import com.exedio.cope.TypesBound;
import org.junit.Test;

public class TypeFutureInconsistentTest
{
	@Test public void testTypeFutureInconsistent()
	{
		try
		{
			new Model(TypeItem.TYPE, ValueClassItem.TYPE, FeatureItem.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"ItemField FeatureItem.itemField: " +
					"valueClass com.exedio.cope.pattern.TypeFutureInconsistentTest$ValueClassItem " +
					"must be equal to " +
					"javaClass com.exedio.cope.pattern.TypeFutureInconsistentTest$TypeItem " +
					"of valueType TypeItem provided by TypeFuture TypeFuture(FeatureItem.itemField).",
					e.getMessage());
		}

		// make sure there is still not value type set
		try
		{
			FeatureItem.itemField.getValueType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"value type of FeatureItem.itemField (TypeFuture(FeatureItem.itemField)) does not belong to any model",
					e.getMessage());
		}
	}

	private static class ValueClassItem extends Item
	{
		static final Type<ValueClassItem> TYPE = TypesBound.newType(ValueClassItem.class);
		private static final long serialVersionUID = 1l;
		private ValueClassItem(final ActivationParameters ap) { super(ap); }
	}

	private static class TypeItem extends Item
	{
		static final Type<TypeItem> TYPE = TypesBound.newType(TypeItem.class);
		private static final long serialVersionUID = 1l;
		private TypeItem(final ActivationParameters ap) { super(ap); }
	}

	private static class FeatureItem extends Item
	{
		static final ItemField<ValueClassItem> itemField = ItemField.create(ValueClassItem.class, new TypeFuture<ValueClassItem>(){

			@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
			public Type<ValueClassItem> get()
			{
				return (Type)TypeItem.TYPE;
			}
			@Override
			public String toString()
			{
				return "TypeFuture(FeatureItem.itemField)";
			}

		}, DeletePolicy.FORBID);
		static final Type<FeatureItem> TYPE = TypesBound.newType(FeatureItem.class);
		private static final long serialVersionUID = 1l;
		private FeatureItem(final ActivationParameters ap) { super(ap); }
	}
}
