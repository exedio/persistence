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

import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

public class DumperItem extends Item
{
	static final StringField string = new StringField().lengthMax(10);
	static final StringField unique = new StringField().unique();
	static final DataField data = new DataField();

	static int beforeNewCopeItemCount = 0;

	@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD") // called by reflection
	private static SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
	{
		beforeNewCopeItemCount++;
		return setValues;
	}

	@Override
	protected final void afterNewCopeItem()
	{
		fail("must not be called");
	}

	@Override
	protected SetValue<?>[] beforeSetCopeItem(final SetValue<?>[] setValues)
	{
		throw new AssertionError(Arrays.toString(setValues));
	}


	/**
	 * Creates a new DumperItem with all the fields initially needed.
	 * @param string the initial value for field {@link #string}.
	 * @param unique the initial value for field {@link #unique}.
	 * @param data the initial value for field {@link #data}.
	 * @throws com.exedio.cope.MandatoryViolationException if string, unique, data is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string, unique violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if unique is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DumperItem(
				@javax.annotation.Nonnull final java.lang.String string,
				@javax.annotation.Nonnull final java.lang.String unique,
				@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DumperItem.string.map(string),
			DumperItem.unique.map(unique),
			DumperItem.data.map(data),
		});
	}

	/**
	 * Creates a new DumperItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected DumperItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.lang.String getString()
	{
		return DumperItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setString(@javax.annotation.Nonnull final java.lang.String string)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DumperItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #unique}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.lang.String getUnique()
	{
		return DumperItem.unique.get(this);
	}

	/**
	 * Sets a new value for {@link #unique}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setUnique(@javax.annotation.Nonnull final java.lang.String unique)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		DumperItem.unique.set(this,unique);
	}

	/**
	 * Finds a dumperItem by it's {@link #unique}.
	 * @param unique shall be equal to field {@link #unique}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static final DumperItem forUnique(@javax.annotation.Nonnull final java.lang.String unique)
	{
		return DumperItem.unique.searchUnique(DumperItem.class,unique);
	}

	/**
	 * Finds a dumperItem by its {@link #unique}.
	 * @param unique shall be equal to field {@link #unique}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static final DumperItem forUniqueStrict(@javax.annotation.Nonnull final java.lang.String unique)
			throws
				java.lang.IllegalArgumentException
	{
		return DumperItem.unique.searchUniqueStrict(DumperItem.class,unique);
	}

	/**
	 * Returns, whether there is no data for field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isDataNull()
	{
		return DumperItem.data.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getDataLength()
	{
		return DumperItem.data.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getArray")
	@javax.annotation.Nullable
	final byte[] getDataArray()
	{
		return DumperItem.data.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
			throws
				java.io.IOException
	{
		DumperItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final void getData(@javax.annotation.Nonnull final java.nio.file.Path data)
			throws
				java.io.IOException
	{
		DumperItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final void getData(@javax.annotation.Nonnull final java.io.File data)
			throws
				java.io.IOException
	{
		DumperItem.data.get(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DumperItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nonnull final byte[] data)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DumperItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nonnull final java.io.InputStream data)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		DumperItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nonnull final java.nio.file.Path data)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		DumperItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nonnull final java.io.File data)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		DumperItem.data.set(this,data);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dumperItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<DumperItem> TYPE = com.exedio.cope.TypesBound.newType(DumperItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected DumperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
