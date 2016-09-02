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

import static org.junit.Assert.assertNotNull;

final class DeleteOtherItem extends Item
{

	String name = null;

	DeleteOtherItem(final String name)
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
		DeleteItem.BEFORE_DELETE_COPE_ITEM_CALLS.get().add(name);
	}


	/**

	 **
	 * Creates a new DeleteOtherItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DeleteOtherItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}/**

	 **
	 * Creates a new DeleteOtherItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DeleteOtherItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for deleteOtherItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DeleteOtherItem> TYPE = com.exedio.cope.TypesBound.newType(DeleteOtherItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DeleteOtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
