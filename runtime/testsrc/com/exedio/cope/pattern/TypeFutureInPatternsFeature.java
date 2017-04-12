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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Features;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.TypeFuture;
import com.exedio.cope.instrument.WrapperIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import junit.framework.AssertionFailedError;

final class TypeFutureInPatternsFeature extends Pattern
{
	IntegerField field = new IntegerField();

	ItemField<TypeItem> self()
	{
		return mount().self;
	}

	Type<TypeItem> sourceType()
	{
		return mount().runType;
	}

	TypeFuture<TypeItem> sourceTypeFuture()
	{
		return new TypeFuture<TypeItem>(){
			@Override
			public Type<TypeItem> get()
			{
				return sourceType();
			}
			@Override
			public String toString()
			{
				throw new AssertionFailedError();
			}
		};
	}

	@Override
	protected void onMount()
	{
		super.onMount();

		final ItemField<TypeItem> self = ItemField.create(TypeItem.class, sourceTypeFuture(), DeletePolicy.FORBID).optional();
		final Features features = new Features();
		features.put("field", field);
		features.put("self", self);
		final Type<TypeItem> runType = newSourceType(TypeItem.class, features, "Type");
		this.mountIfMounted = new Mount(self, runType);
	}

	private static final class Mount
	{
		ItemField<TypeItem> self;
		final Type<TypeItem> runType;

		Mount(
				final ItemField<TypeItem> self,
				final Type<TypeItem> runType)
		{
			this.self = self;
			this.runType = runType;
		}
	}

	Mount mount()
	{
		final Mount mount = this.mountIfMounted;
		if(mount==null)
			throw new IllegalStateException("feature not mounted");
		return mount;
	}

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount mountIfMounted = null;


	public TypeItem create(final int field, final TypeItem self)
	{
		return sourceType().newItem(
				this.field.map(field),
				self().map(self));
	}


	@WrapperIgnore
	public static final class TypeItem extends Item
	{
		private static final long serialVersionUID = 1l;

		TypeItem(final ActivationParameters ap)
		{
			super(ap);
		}

		public TypeFutureInPatternsFeature getPattern()
		{
			return (TypeFutureInPatternsFeature)getCopeType().getPattern();
		}

		public int getInteger()
		{
			return getPattern().field.getMandatory(this);
		}

		public TypeItem getSelf()
		{
			return getPattern().self().get(this);
		}
	}

	private static final long serialVersionUID = 1l;
}
