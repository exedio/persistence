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
import com.exedio.cope.vaulttest.VaultServiceTest.NonCloseableOrFlushableOutputStream;
import java.io.ByteArrayInputStream;
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
	@Wrapper(wrap="get", parameters=Path.class, visibility=NONE)
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

	static VaultItem byPath(@Nonnull final String field) throws IOException
	{
		return new VaultItem(DataField.toValue(toPath(field)));
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
		final NonCloseableOrFlushableOutputStream s = new NonCloseableOrFlushableOutputStream();
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

	void setFieldByPath(final String field) throws IOException
	{
		setField(toPath(field));
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

	private static Path toPath(final String field) throws IOException
	{
		final Path path = Files.createTempFile("VaultItem-", ".dat");
		Files.write(path, Hex.decodeLower(field));
		return path;
	}

	private static File toFile(final String field) throws IOException
	{
		return toPath(field).toFile();
	}


	/**
	 * Creates a new VaultItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @throws com.exedio.cope.MandatoryViolationException if field is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private VaultItem(
				@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(VaultItem.field,field),
		});
	}

	/**
	 * Creates a new VaultItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private VaultItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns, whether there is no data for field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean isFieldNull()
	{
		return VaultItem.field.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private long getFieldLengthInternal()
	{
		return VaultItem.field.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getArray")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	private byte[] getFieldArray()
	{
		return VaultItem.field.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void getField(@javax.annotation.Nonnull final java.io.OutputStream field)
			throws
				java.io.IOException
	{
		VaultItem.field.get(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setField(@javax.annotation.Nonnull final com.exedio.cope.DataField.Value field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setField(@javax.annotation.Nonnull final byte[] field)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setField(@javax.annotation.Nonnull final java.io.InputStream field)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void setField(@javax.annotation.Nonnull final java.nio.file.Path field)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		VaultItem.field.set(this,field);
	}

	/**
	 * Sets a new value for the persistent field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	private void setField(@javax.annotation.Nonnull final java.io.File field)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.io.IOException
	{
		VaultItem.field.set(this,field);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for vaultItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<VaultItem> TYPE = com.exedio.cope.TypesBound.newType(VaultItem.class,VaultItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private VaultItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
