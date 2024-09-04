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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapperType;
import java.util.Map;

@WrapperType(genericConstructor=PACKAGE)
@SuppressWarnings("rawtypes")
public final class SerializerItem extends Item
{
	static final Serializer<Integer> integer = Serializer.create(Integer.class).optional();

	static final Serializer<Map> map = Serializer.create(Map.class).optional();

	static final Serializer<Map<?,?>> mapWildcard = Serializer.create(ClassWildcard.map).optional();

	static final Serializer<String> mandatoryString = Serializer.create(String.class);


	/**
	 * Creates a new SerializerItem with all the fields initially needed.
	 * @param mandatoryString the initial value for field {@link #mandatoryString}.
	 * @throws com.exedio.cope.MandatoryViolationException if mandatoryString is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SerializerItem(
				@javax.annotation.Nonnull final String mandatoryString)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(SerializerItem.mandatoryString,mandatoryString),
		});
	}

	/**
	 * Creates a new SerializerItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	SerializerItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Integer getInteger()
	{
		return SerializerItem.integer.get(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger(@javax.annotation.Nullable final Integer integer)
	{
		SerializerItem.integer.set(this,integer);
	}

	/**
	 * Returns the value of {@link #map}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Map getMap()
	{
		return SerializerItem.map.get(this);
	}

	/**
	 * Sets a new value for {@link #map}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMap(@javax.annotation.Nullable final Map map)
	{
		SerializerItem.map.set(this,map);
	}

	/**
	 * Returns the value of {@link #mapWildcard}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Map<?,?> getMapWildcard()
	{
		return SerializerItem.mapWildcard.get(this);
	}

	/**
	 * Sets a new value for {@link #mapWildcard}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMapWildcard(@javax.annotation.Nullable final Map<?,?> mapWildcard)
	{
		SerializerItem.mapWildcard.set(this,mapWildcard);
	}

	/**
	 * Returns the value of {@link #mandatoryString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	String getMandatoryString()
	{
		return SerializerItem.mandatoryString.get(this);
	}

	/**
	 * Sets a new value for {@link #mandatoryString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setMandatoryString(@javax.annotation.Nonnull final String mandatoryString)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		SerializerItem.mandatoryString.set(this,mandatoryString);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for serializerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SerializerItem> TYPE = com.exedio.cope.TypesBound.newType(SerializerItem.class,SerializerItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private SerializerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
