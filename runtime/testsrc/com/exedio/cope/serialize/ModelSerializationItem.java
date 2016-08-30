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

package com.exedio.cope.serialize;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.pattern.ListField;

final class ModelSerializationItem extends Item
{
	@WrapperInitial
	static final StringField name = new StringField().optional();
	static final ListField<String> list = ListField.create(new StringField());


	/**

	 **
	 * Creates a new ModelSerializationItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ModelSerializationItem(
				@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ModelSerializationItem.name.map(name),
		});
	}/**

	 **
	 * Creates a new ModelSerializationItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ModelSerializationItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getName()
	{
		return ModelSerializationItem.name.get(this);
	}/**

	 **
	 * Sets a new value for {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setName(@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		ModelSerializationItem.name.set(this,name);
	}/**

	 **
	 * Returns the value of {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.util.List<String> getList()
	{
		return ModelSerializationItem.list.get(this);
	}/**

	 **
	 * Returns a query for the value of {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getQuery")
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<String> getListQuery()
	{
		return ModelSerializationItem.list.getQuery(this);
	}/**

	 **
	 * Returns the items, for which field list {@link #list} contains the given element.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getDistinctParentsOf")
	@javax.annotation.Nonnull
	static final java.util.List<ModelSerializationItem> getDistinctParentsOfList(final String element)
	{
		return ModelSerializationItem.list.getDistinctParents(ModelSerializationItem.class,element);
	}/**

	 **
	 * Adds a new value for {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="addTo")
	final void addToList(@javax.annotation.Nonnull final String list)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ModelSerializationItem.list.add(this,list);
	}/**

	 **
	 * Sets a new value for {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setList(@javax.annotation.Nonnull final java.util.Collection<? extends String> list)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				java.lang.ClassCastException
	{
		ModelSerializationItem.list.set(this,list);
	}/**

	 **
	 * Returns the parent field of the type of {@link #list}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="Parent")
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<ModelSerializationItem> listParent()
	{
		return ModelSerializationItem.list.getParent(ModelSerializationItem.class);
	}/**

	 **
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for modelSerializationItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ModelSerializationItem> TYPE = com.exedio.cope.TypesBound.newType(ModelSerializationItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ModelSerializationItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
