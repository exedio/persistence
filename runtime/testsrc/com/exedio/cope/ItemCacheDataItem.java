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

import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;

import com.exedio.cope.instrument.Visibility;
import com.exedio.cope.instrument.Wrapper;

final class ItemCacheDataItem extends Item
{
	static final StringField string = new StringField().optional();
	@Wrapper(wrap="set", visibility=Visibility.PACKAGE, internal=true)
	static final DataField data = new DataField().optional();

	String getData()
	{
		return encodeLower(getDataArray());
	}

	void setData(final String data)
	{
		setDataInternal(decodeLower(data));
	}

	void setDataMulti(final String data)
	{
		set(ItemCacheDataItem.data.map(decodeLower(data)));
	}

	void setBothMulti(final String string, final String data)
	{
		set(ItemCacheDataItem.string.map(string), ItemCacheDataItem.data.map(decodeLower(data)));
	}


	/**
	 * Creates a new ItemCacheDataItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ItemCacheDataItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new ItemCacheDataItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ItemCacheDataItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getString()
	{
		return ItemCacheDataItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setString(@javax.annotation.Nullable final java.lang.String string)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		ItemCacheDataItem.string.set(this,string);
	}

	/**
	 * Returns, whether there is no data for field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isDataNull()
	{
		return ItemCacheDataItem.data.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getDataLength()
	{
		return ItemCacheDataItem.data.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getArray")
	@javax.annotation.Nullable
	byte[] getDataArray()
	{
		return ItemCacheDataItem.data.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
			throws
				java.io.IOException
	{
		ItemCacheDataItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	void getData(@javax.annotation.Nonnull final java.nio.file.Path data)
			throws
				java.io.IOException
	{
		ItemCacheDataItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	void getData(@javax.annotation.Nonnull final java.io.File data)
			throws
				java.io.IOException
	{
		ItemCacheDataItem.data.get(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDataInternal(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data)
	{
		ItemCacheDataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDataInternal(@javax.annotation.Nullable final byte[] data)
	{
		ItemCacheDataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDataInternal(@javax.annotation.Nullable final java.io.InputStream data)
			throws
				java.io.IOException
	{
		ItemCacheDataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDataInternal(@javax.annotation.Nullable final java.nio.file.Path data)
			throws
				java.io.IOException
	{
		ItemCacheDataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setDataInternal(@javax.annotation.Nullable final java.io.File data)
			throws
				java.io.IOException
	{
		ItemCacheDataItem.data.set(this,data);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for itemCacheDataItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ItemCacheDataItem> TYPE = com.exedio.cope.TypesBound.newType(ItemCacheDataItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ItemCacheDataItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
