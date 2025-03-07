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

import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class DataItem extends Item
{
	static final DataField data = new DataField().optional();
	static final DataField data10 = new DataField().optional().lengthMax(10);
	static final DataField data10k = new DataField().optional().lengthMax(10*1000);
	static final DataField data100M = new DataField().optional().lengthMax(100*1000*1000);
	static final StringField name = new StringField().optional();

	DataItem(final byte[] data)
	{
		this(new com.exedio.cope.SetValue<?>[]{
				DataItem.data.map(data),
		});
	}

	DataItem(final byte[] data, final byte[] data10)
	{
		this(new com.exedio.cope.SetValue<?>[]{
				DataItem.data.map(data),
				DataItem.data10.map(data10),
		});
	}

	final void getDataDeprecated(@Nonnull final File data) throws IOException
	{
		getData(data);
	}

	final void setDataDeprecated(@Nullable final File data) throws IOException
	{
		setData(data);
	}

	final void setData10Deprecated(@Nullable final File data10) throws IOException
	{
		setData10(data10);
	}


	/**
	 * Creates a new DataItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DataItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new DataItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected DataItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns, whether there is no data for field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean isDataNull()
	{
		return DataItem.data.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getDataLength()
	{
		return DataItem.data.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getDataArray()
	{
		return DataItem.data.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
			throws
				java.io.IOException
	{
		DataItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData(@javax.annotation.Nonnull final java.nio.file.Path data)
			throws
				java.io.IOException
	{
		DataItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void getData(@javax.annotation.Nonnull final java.io.File data)
			throws
				java.io.IOException
	{
		DataItem.data.get(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data)
	{
		DataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData(@javax.annotation.Nullable final byte[] data)
	{
		DataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData(@javax.annotation.Nullable final java.io.InputStream data)
			throws
				java.io.IOException
	{
		DataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData(@javax.annotation.Nullable final java.nio.file.Path data)
			throws
				java.io.IOException
	{
		DataItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void setData(@javax.annotation.Nullable final java.io.File data)
			throws
				java.io.IOException
	{
		DataItem.data.set(this,data);
	}

	/**
	 * Returns, whether there is no data for field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean isData10Null()
	{
		return DataItem.data10.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getData10Length()
	{
		return DataItem.data10.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getData10Array()
	{
		return DataItem.data10.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData10(@javax.annotation.Nonnull final java.io.OutputStream data10)
			throws
				java.io.IOException
	{
		DataItem.data10.get(this,data10);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData10(@javax.annotation.Nonnull final java.nio.file.Path data10)
			throws
				java.io.IOException
	{
		DataItem.data10.get(this,data10);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void getData10(@javax.annotation.Nonnull final java.io.File data10)
			throws
				java.io.IOException
	{
		DataItem.data10.get(this,data10);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data10)
	{
		DataItem.data10.set(this,data10);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10(@javax.annotation.Nullable final byte[] data10)
	{
		DataItem.data10.set(this,data10);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10(@javax.annotation.Nullable final java.io.InputStream data10)
			throws
				java.io.IOException
	{
		DataItem.data10.set(this,data10);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10(@javax.annotation.Nullable final java.nio.file.Path data10)
			throws
				java.io.IOException
	{
		DataItem.data10.set(this,data10);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void setData10(@javax.annotation.Nullable final java.io.File data10)
			throws
				java.io.IOException
	{
		DataItem.data10.set(this,data10);
	}

	/**
	 * Returns, whether there is no data for field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean isData10kNull()
	{
		return DataItem.data10k.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getData10kLength()
	{
		return DataItem.data10k.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getData10kArray()
	{
		return DataItem.data10k.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData10k(@javax.annotation.Nonnull final java.io.OutputStream data10k)
			throws
				java.io.IOException
	{
		DataItem.data10k.get(this,data10k);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData10k(@javax.annotation.Nonnull final java.nio.file.Path data10k)
			throws
				java.io.IOException
	{
		DataItem.data10k.get(this,data10k);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void getData10k(@javax.annotation.Nonnull final java.io.File data10k)
			throws
				java.io.IOException
	{
		DataItem.data10k.get(this,data10k);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10k(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data10k)
	{
		DataItem.data10k.set(this,data10k);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10k(@javax.annotation.Nullable final byte[] data10k)
	{
		DataItem.data10k.set(this,data10k);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10k(@javax.annotation.Nullable final java.io.InputStream data10k)
			throws
				java.io.IOException
	{
		DataItem.data10k.set(this,data10k);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData10k(@javax.annotation.Nullable final java.nio.file.Path data10k)
			throws
				java.io.IOException
	{
		DataItem.data10k.set(this,data10k);
	}

	/**
	 * Sets a new value for the persistent field {@link #data10k}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void setData10k(@javax.annotation.Nullable final java.io.File data10k)
			throws
				java.io.IOException
	{
		DataItem.data10k.set(this,data10k);
	}

	/**
	 * Returns, whether there is no data for field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean isData100MNull()
	{
		return DataItem.data100M.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getData100MLength()
	{
		return DataItem.data100M.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getData100MArray()
	{
		return DataItem.data100M.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData100M(@javax.annotation.Nonnull final java.io.OutputStream data100M)
			throws
				java.io.IOException
	{
		DataItem.data100M.get(this,data100M);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getData100M(@javax.annotation.Nonnull final java.nio.file.Path data100M)
			throws
				java.io.IOException
	{
		DataItem.data100M.get(this,data100M);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void getData100M(@javax.annotation.Nonnull final java.io.File data100M)
			throws
				java.io.IOException
	{
		DataItem.data100M.get(this,data100M);
	}

	/**
	 * Sets a new value for the persistent field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData100M(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data100M)
	{
		DataItem.data100M.set(this,data100M);
	}

	/**
	 * Sets a new value for the persistent field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData100M(@javax.annotation.Nullable final byte[] data100M)
	{
		DataItem.data100M.set(this,data100M);
	}

	/**
	 * Sets a new value for the persistent field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData100M(@javax.annotation.Nullable final java.io.InputStream data100M)
			throws
				java.io.IOException
	{
		DataItem.data100M.set(this,data100M);
	}

	/**
	 * Sets a new value for the persistent field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setData100M(@javax.annotation.Nullable final java.nio.file.Path data100M)
			throws
				java.io.IOException
	{
		DataItem.data100M.set(this,data100M);
	}

	/**
	 * Sets a new value for the persistent field {@link #data100M}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	final void setData100M(@javax.annotation.Nullable final java.io.File data100M)
			throws
				java.io.IOException
	{
		DataItem.data100M.set(this,data100M);
	}

	/**
	 * Returns the value of {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getName()
	{
		return DataItem.name.get(this);
	}

	/**
	 * Sets a new value for {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setName(@javax.annotation.Nullable final java.lang.String name)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		DataItem.name.set(this,name);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dataItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DataItem> TYPE = com.exedio.cope.TypesBound.newType(DataItem.class,DataItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected DataItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
