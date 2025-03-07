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

final class DataFinalItem extends Item
{

	static final DataField data = new DataField().toFinal().optional();

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	void setDataDeprecated(final File value) throws IOException
	{
		data.set(this, value);
	}

	/**
	 * Creates a new DataFinalItem with all the fields initially needed.
	 * @param data the initial value for field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	DataFinalItem(
				@javax.annotation.Nullable final com.exedio.cope.DataField.Value data)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(DataFinalItem.data,data),
		});
	}

	/**
	 * Creates a new DataFinalItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private DataFinalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns, whether there is no data for field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isDataNull()
	{
		return DataFinalItem.data.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getDataLength()
	{
		return DataFinalItem.data.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	byte[] getDataArray()
	{
		return DataFinalItem.data.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
			throws
				java.io.IOException
	{
		DataFinalItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void getData(@javax.annotation.Nonnull final java.nio.file.Path data)
			throws
				java.io.IOException
	{
		DataFinalItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	void getData(@javax.annotation.Nonnull final java.io.File data)
			throws
				java.io.IOException
	{
		DataFinalItem.data.get(this,data);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for dataFinalItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DataFinalItem> TYPE = com.exedio.cope.TypesBound.newType(DataFinalItem.class,DataFinalItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private DataFinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
