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

package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

final class DeleteItem extends Item
{
	static final ThreadLocal<List<String>> BEFORE_DELETE_COPE_ITEM_CALLS = new ThreadLocal<>();

	static final ItemField<DeleteItem> selfForbid = ItemField.create(DeleteItem.class).optional();

	static final ItemField<DeleteItem> selfNullify = ItemField.create(DeleteItem.class).nullify();

	static final ItemField<DeleteItem> selfCascade = ItemField.create(DeleteItem.class).cascade().optional();
	static final ItemField<DeleteItem> selfCascade2 = ItemField.create(DeleteItem.class).cascade().optional();

	static final ItemField<DeleteOtherItem> otherForbid = ItemField.create(DeleteOtherItem.class).optional();

	static final ItemField<DeleteOtherItem> otherNullify = ItemField.create(DeleteOtherItem.class).nullify();

	static final ItemField<DeleteOtherItem> otherCascade = ItemField.create(DeleteOtherItem.class).cascade().optional();


	String name = null;

	DeleteItem(final String name)
	{
		this();
		assertNotNull(name);
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	protected void beforeDeleteCopeItem()
	{
		BEFORE_DELETE_COPE_ITEM_CALLS.get().add(name);
	}


	/**
	 * Creates a new DeleteItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DeleteItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new DeleteItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DeleteItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #selfForbid}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteItem getSelfForbid()
	{
		return DeleteItem.selfForbid.get(this);
	}

	/**
	 * Sets a new value for {@link #selfForbid}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSelfForbid(@javax.annotation.Nullable final DeleteItem selfForbid)
	{
		DeleteItem.selfForbid.set(this,selfForbid);
	}

	/**
	 * Returns the value of {@link #selfNullify}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteItem getSelfNullify()
	{
		return DeleteItem.selfNullify.get(this);
	}

	/**
	 * Sets a new value for {@link #selfNullify}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSelfNullify(@javax.annotation.Nullable final DeleteItem selfNullify)
	{
		DeleteItem.selfNullify.set(this,selfNullify);
	}

	/**
	 * Returns the value of {@link #selfCascade}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteItem getSelfCascade()
	{
		return DeleteItem.selfCascade.get(this);
	}

	/**
	 * Sets a new value for {@link #selfCascade}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSelfCascade(@javax.annotation.Nullable final DeleteItem selfCascade)
	{
		DeleteItem.selfCascade.set(this,selfCascade);
	}

	/**
	 * Returns the value of {@link #selfCascade2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteItem getSelfCascade2()
	{
		return DeleteItem.selfCascade2.get(this);
	}

	/**
	 * Sets a new value for {@link #selfCascade2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSelfCascade2(@javax.annotation.Nullable final DeleteItem selfCascade2)
	{
		DeleteItem.selfCascade2.set(this,selfCascade2);
	}

	/**
	 * Returns the value of {@link #otherForbid}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteOtherItem getOtherForbid()
	{
		return DeleteItem.otherForbid.get(this);
	}

	/**
	 * Sets a new value for {@link #otherForbid}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOtherForbid(@javax.annotation.Nullable final DeleteOtherItem otherForbid)
	{
		DeleteItem.otherForbid.set(this,otherForbid);
	}

	/**
	 * Returns the value of {@link #otherNullify}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteOtherItem getOtherNullify()
	{
		return DeleteItem.otherNullify.get(this);
	}

	/**
	 * Sets a new value for {@link #otherNullify}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOtherNullify(@javax.annotation.Nullable final DeleteOtherItem otherNullify)
	{
		DeleteItem.otherNullify.set(this,otherNullify);
	}

	/**
	 * Returns the value of {@link #otherCascade}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	DeleteOtherItem getOtherCascade()
	{
		return DeleteItem.otherCascade.get(this);
	}

	/**
	 * Sets a new value for {@link #otherCascade}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOtherCascade(@javax.annotation.Nullable final DeleteOtherItem otherCascade)
	{
		DeleteItem.otherCascade.set(this,otherCascade);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for deleteItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DeleteItem> TYPE = com.exedio.cope.TypesBound.newType(DeleteItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DeleteItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
