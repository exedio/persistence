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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Hex;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;

@WrapperType(constructor=PRIVATE)
final class VaultItem extends Item
{
	@Wrapper(wrap="getLength", internal=true)
	@Wrapper(wrap="get", visibility=PRIVATE)
	@Wrapper(wrap="get", parameters=File.class, visibility=NONE)
	@Wrapper(wrap="getArray", visibility=PRIVATE)
	@Wrapper(wrap="set", visibility=PRIVATE)
	static final DataField field = new DataField();

	VaultItem(
			@javax.annotation.Nonnull final String field)
	{
		this(DataField.toValue(Hex.decodeLower(field)));
	}

	static VaultItem byStream(@Nonnull final String field)
	{
		return new VaultItem(DataField.toValue(toStream(field)));
	}

	static VaultItem byFile(@Nonnull final String field) throws IOException
	{
		return new VaultItem(DataField.toValue(toFile(field)));
	}

	static VaultItem byZip(@Nonnull final String field) throws IOException, URISyntaxException
	{
		try(ZipFile zip = new ZipFile(VaultTest.class.getResource("VaultTest.zip").toURI().getPath()))
		{
			return new VaultItem(DataField.toValue(zip, zip.getEntry(field)));
		}
	}

	long getFieldLength()
	{
		return getFieldLengthInternal() * 2; // 1 byte is 2 hex chars
	}

	String getFieldBytes()
	{
		return Hex.encodeLower(getFieldArray());
	}

	String getFieldStream() throws IOException
	{
		final ByteArrayOutputStream s = new ByteArrayOutputStream();
		getField(s);
		return Hex.encodeLower(s.toByteArray());
	}

	void setField(final String field)
	{
		setField(Hex.decodeLower(field));
	}

	void setFieldByStream(final String field) throws IOException
	{
		setField(toStream(field));
	}

	void setFieldByFile(final String field) throws IOException
	{
		setField(toFile(field));
	}

	void setFieldByZip(final String field) throws IOException, URISyntaxException
	{
		try(ZipFile zip = new ZipFile(VaultTest.class.getResource("VaultTest.zip").toURI().getPath()))
		{
			setField(DataField.toValue(zip, zip.getEntry(field)));
		}
	}


	private static InputStream toStream(final String field)
	{
		return new ByteArrayInputStream(Hex.decodeLower(field));
	}

	private static File toFile(final String field) throws IOException
	{
		final Path path = Files.createTempFile("VaultItem-", ".dat");
		Files.write(path, Hex.decodeLower(field));
		return path.toFile();
	}


	/**
	 * Creates a new VaultItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.MandatoryViolationException if field is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	private VaultItem(
				@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			VaultItem.field.map(field),
		});
	}

	/**
	 * Creates a new VaultItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private VaultItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns, whether there is no data for field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isFieldNull()
	{
		return VaultItem.field.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	private final long getFieldLengthInternal()
	{
		return VaultItem.field.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getArray")
	@javax.annotation.Nullable
	private final byte[] getFieldArray()
	{
		return VaultItem.field.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	private final void getField(@javax.annotation.Nonnull final java.io.OutputStream field)
			throws
				java.io.IOException
	{
		VaultItem.field.get(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private final void setField(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private final void setField(@javax.annotation.Nonnull final byte[] field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private final void setField(@javax.annotation.Nonnull final java.io.InputStream field)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	private final void setField(@javax.annotation.Nonnull final java.io.File field)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		VaultItem.field.set(this,field);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for vaultItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<VaultItem> TYPE = com.exedio.cope.TypesBound.newType(VaultItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private VaultItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}