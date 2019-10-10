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

import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class MapFieldItem extends Item
{
	enum Language
	{
		DE, EN, PL
	}

	static final MapField<Language, String> name = MapField.create(EnumField.create(Language.class).toFinal(), new StringField());

	static final MapField<Language, Integer> nameLength = MapField.create(EnumField.create(Language.class).toFinal(), new IntegerField());

	static final MapField<String, String> string = MapField.create(new StringField().toFinal().lengthRange(4, 8), new StringField());

	static final MapField<String, Integer> integer = MapField.create(new StringField().toFinal().lengthRange(4, 8), new IntegerField());


	/**
	 * Creates a new MapFieldItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public MapFieldItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new MapFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MapFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	String getName(@javax.annotation.Nonnull final Language k)
	{
		return MapFieldItem.name.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setName(@javax.annotation.Nonnull final Language k,@javax.annotation.Nullable final String name)
	{
		MapFieldItem.name.set(this,k,name);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<Language,String> getNameMap()
	{
		return MapFieldItem.name.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameMap(@javax.annotation.Nonnull final java.util.Map<? extends Language,? extends String> name)
	{
		MapFieldItem.name.setMap(this,name);
	}

	/**
	 * Returns the parent field of the type of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<MapFieldItem> nameParent()
	{
		return MapFieldItem.name.getParent(MapFieldItem.class);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #nameLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Integer getNameLength(@javax.annotation.Nonnull final Language k)
	{
		return MapFieldItem.nameLength.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #nameLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameLength(@javax.annotation.Nonnull final Language k,@javax.annotation.Nullable final Integer nameLength)
	{
		MapFieldItem.nameLength.set(this,k,nameLength);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<Language,Integer> getNameLengthMap()
	{
		return MapFieldItem.nameLength.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameLengthMap(@javax.annotation.Nonnull final java.util.Map<? extends Language,? extends Integer> nameLength)
	{
		MapFieldItem.nameLength.setMap(this,nameLength);
	}

	/**
	 * Returns the parent field of the type of {@link #nameLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<MapFieldItem> nameLengthParent()
	{
		return MapFieldItem.nameLength.getParent(MapFieldItem.class);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	String getString(@javax.annotation.Nonnull final String k)
	{
		return MapFieldItem.string.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setString(@javax.annotation.Nonnull final String k,@javax.annotation.Nullable final String string)
	{
		MapFieldItem.string.set(this,k,string);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<String,String> getStringMap()
	{
		return MapFieldItem.string.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setStringMap(@javax.annotation.Nonnull final java.util.Map<? extends String,? extends String> string)
	{
		MapFieldItem.string.setMap(this,string);
	}

	/**
	 * Returns the parent field of the type of {@link #string}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<MapFieldItem> stringParent()
	{
		return MapFieldItem.string.getParent(MapFieldItem.class);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Integer getInteger(@javax.annotation.Nonnull final String k)
	{
		return MapFieldItem.integer.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger(@javax.annotation.Nonnull final String k,@javax.annotation.Nullable final Integer integer)
	{
		MapFieldItem.integer.set(this,k,integer);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<String,Integer> getIntegerMap()
	{
		return MapFieldItem.integer.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntegerMap(@javax.annotation.Nonnull final java.util.Map<? extends String,? extends Integer> integer)
	{
		MapFieldItem.integer.setMap(this,integer);
	}

	/**
	 * Returns the parent field of the type of {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static com.exedio.cope.ItemField<MapFieldItem> integerParent()
	{
		return MapFieldItem.integer.getParent(MapFieldItem.class);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mapFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MapFieldItem> TYPE = com.exedio.cope.TypesBound.newType(MapFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MapFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
