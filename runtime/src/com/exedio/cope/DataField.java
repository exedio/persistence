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

import static com.exedio.cope.Executor.longResultSetHandler;
import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonNegative;
import static com.exedio.cope.vault.VaultProperties.checkBucket;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import com.exedio.cope.util.Hex;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.annotations.Contract;

public final class DataField extends Field<DataField.Value>
{
	private static final long serialVersionUID = 1l;

	private final long maximumLength;

	public static final long DEFAULT_LENGTH = 10*1000*1000;

	private DataField(final boolean isfinal, final boolean optional, final long maximumLength)
	{
		super(isfinal, optional, Value.class);
		this.maximumLength = requireGreaterZero(maximumLength, "maximumLength");
	}

	public DataField()
	{
		this(false, false, DEFAULT_LENGTH);
	}

	@Override
	public DataField toFinal()
	{
		return new DataField(true, optional, maximumLength);
	}

	@Override
	public DataField optional()
	{
		return new DataField(isfinal, true, maximumLength);
	}

	@Override
	public DataField mandatory()
	{
		return new DataField(isfinal, false, maximumLength);
	}

	public DataField lengthMax(final long maximumLength)
	{
		return new DataField(isfinal, optional, maximumLength);
	}

	public long getMaximumLength()
	{
		return maximumLength;
	}

	public boolean isAnnotatedVault()
	{
		return getAnnotatedVaultValue()!=null;
	}

	public String getAnnotatedVaultValue()
	{
		final Vault byFeature = getAnnotation(Vault.class);
		if(byFeature!=null)
			return checkVault(byFeature.value(), this);

		final Type<?> type = getType();
		final Vault byType = type.getAnnotation(Vault.class);
		if(byType!=null)
			return checkVault(byType.value(), type);

		return null;
	}

	private static String checkVault(final String value, final Object origin)
	{
		checkBucket(value, message ->
				new IllegalArgumentException("@Vault at " + origin + ' ' + message));
		return value;
	}

	// second initialization phase ---------------------------------------------------

	private Model model;
	private DataFieldStore store;

	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		this.model = getType().getModel();
		final ConnectProperties properties = connect.properties;
		final VaultProperties vaultProperties = properties.vault;
		store = vaultProperties==null ||
					!(
						vaultProperties.isAppliedToAllFields() ||
						isAnnotatedVault()
					)
				? new DataFieldBlobStore (this, table, name, optional, maximumLength)
				: new DataFieldVaultStore(this, table, name, optional, vaultProperties, connect, metrics);

		return store.column();
	}

	@Override
	void disconnect()
	{
		super.disconnect();
		store = null;
	}

	BlobColumn getBlobColumnIfSupported(final String capability)
	{
		return store.blobColumnIfSupported(capability);
	}

	void appendHashExpression(final Statement bf, final String algorithm)
	{
		store.appendHashExpression(bf, algorithm);
	}

	void put(final Entity entity, final Value value, final Item exceptionItem) // just for DataVault
	{
		store.put(entity, value, exceptionItem);
	}

	/**
	 * @throws IllegalArgumentException if either i or l is negative
	 */
	public static int min(final int i, final long l)
	{
		requireNonNegative(i, "i");
		requireNonNegative(l, "l");

		return i<=l ? i : (int)l;
	}

	/**
	 * Returns, whether there is no data for this field.
	 */
	@Wrap(order=10, doc="Returns, whether there is no data for field {0}.")
	public boolean isNull(@Nonnull final Item item)
	{
		return store.isNull(model.currentTransaction(), item);
	}

	/**
	 * Returns the length of the data of this persistent data field.
	 * Returns -1, if there is no data for this field.
	 */
	@Wrap(order=20,doc="Returns the length of the data of the data field {0}.")
	public long getLength(@Nonnull final Item item)
	{
		return store.loadLength(model.currentTransaction(), item);
	}

	/**
	 * Returns the data of this persistent data field.
	 * Returns null, if there is no data for this field.
	 */
	@Override
	public Value get(final Item item) // return type must not be ArrayValue, as it is not public
	{
		final byte[] array = getArray(item);
		return array!=null ? new ArrayValue(array) : null;
	}

	/**
	 * Returns the data of this persistent data field.
	 * Returns null, if there is no data for this field.
	 */
	@Wrap(order=30, doc="Returns the value of the persistent field {0}.", nullability=NullableIfOptional.class) // TODO better text
	@Nullable // if this DataField is optional
	public byte[] getArray(@Nonnull final Item item)
	{
		return store.load(model.currentTransaction(), item);
	}

	/**
	 * @throws IllegalArgumentException if vault is not enabled for this field
	 */
	public String getVaultHash(@Nonnull final Item item)
	{
		return store.getHash(model.currentTransaction(), item);
	}

	/**
	 * Reads data for this persistent data field
	 * and writes it into the given stream.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if {@code sink} is null.
	 * @throws IOException if writing {@code sink} throws an IOException.
	 */
	@Wrap(order=40,
			doc="Writes the data of this persistent data field into the given stream.",
			thrown=@Wrap.Thrown(IOException.class))
	@SuppressWarnings({"RedundantThrows", "RedundantThrowsDeclaration"}) // TODO should not wrap IOException into RuntimeException
	public void get(@Nonnull final Item item, @Nonnull final OutputStream sink) throws IOException
	{
		requireNonNull(sink, "sink");

		store.load(model.currentTransaction(), item, sink);
	}

	@Wrap(order=48,
			doc="Writes the data of this persistent data field into the given file.",
			thrown=@Wrap.Thrown(IOException.class))
	public void get(@Nonnull final Item item, @Nonnull final Path sink) throws IOException
	{
		requireNonNull(sink, "sink");

		if(!isNull(item))
		{
			try(OutputStream target = Files.newOutputStream(sink,
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING))
			{
				get(item, target);
			}
		}
		// TODO maybe file should be deleted when field is null?
	}

	/**
	 * Reads data for this persistent data field
	 * and writes it into the given file.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if {@code sink} is null.
	 * @throws IOException if writing {@code sink} throws an IOException.
	 */
	@Wrap(order=50,
			doc="Writes the data of this persistent data field into the given file.",
			thrown=@Wrap.Thrown(IOException.class))
	public void get(@Nonnull final Item item, @Nonnull final File sink) throws IOException
	{
		requireNonNull(sink, "sink");

		if(!isNull(item))
		{
			try(FileOutputStream sinkStream = new FileOutputStream(sink))
			{
				get(item, sinkStream);
			}
		}
		// TODO maybe file should be deleted when field is null?
	}

	/**
	 * Provides data for this persistent data field.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if data is longer than {@link #getMaximumLength()}
	 */
	@Wrap(order=100,
			doc="Sets a new value for the persistent field {0}.", // TODO better text
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	@Override
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Value data)
	{
		item.set(SetValue.map(this, data));
	}

	/**
	 * Provides data for this persistent data field.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if data is longer than {@link #getMaximumLength()}
	 */
	@Wrap(order=110,
			doc="Sets a new value for the persistent field {0}.", // TODO better text
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final byte[] data)
	{
		set(item, toValue(data));
	}

	/**
	 * Provides data for this persistent data field.
	 * Closes {@code data} after reading the contents of the stream.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if data is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading data throws an IOException.
	 */
	@Wrap(order=120,
			doc="Sets a new value for the persistent field {0}.", // TODO better text
			thrownGetter=InitialAndIOThrown.class,
			hide=FinalSettableGetter.class)
	@SuppressWarnings({"RedundantThrows", "RedundantThrowsDeclaration"}) // TODO should not wrap IOException into RuntimeException
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final InputStream data)
	throws IOException
	{
		set(item, toValue(data));
	}

	/**
	 * Provides data for this persistent data field.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if data is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading data throws an IOException.
	 */
	@Wrap(order=128,
			doc="Sets a new value for the persistent field {0}.", // TODO better text
			thrownGetter=InitialAndIOThrown.class,
			hide=FinalSettableGetter.class)
	@SuppressWarnings({"RedundantThrows", "RedundantThrowsDeclaration"}) // TODO should not wrap IOException into RuntimeException
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final Path data)
			throws IOException
	{
		set(item, toValue(data));
	}

	/**
	 * Provides data for this persistent data field.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if data is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading data throws an IOException.
	 */
	@Wrap(order=130,
			doc="Sets a new value for the persistent field {0}.", // TODO better text
			thrownGetter=InitialAndIOThrown.class,
			hide=FinalSettableGetter.class)
	@SuppressWarnings({"RedundantThrows", "RedundantThrowsDeclaration"}) // TODO should not wrap IOException into RuntimeException
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final File data)
	throws IOException
	{
		set(item, toValue(data));
	}

	/**
	 * Returns null, if {@code array} is null.
	 */
	@Contract("null -> null; !null -> !null")
	public static Value toValue(final byte[] array)
	{
		return array!=null ? new ArrayValue(array) : null;
	}

	/**
	 * Returns null, if {@code stream} is null.
	 */
	@Contract("null -> null; !null -> !null")
	public static Value toValue(final InputStream stream)
	{
		return stream!=null ? new StreamValue(stream) : null;
	}

	/**
	 * Returns null, if {@code file} is null.
	 */
	@Contract("null -> null; !null -> !null")
	public static Value toValue(final Path path)
	{
		return path!=null ? new PathValue(path) : null;
	}

	/**
	 * Returns null, if {@code file} is null.
	 */
	@Contract("null -> null; !null -> !null")
	public static Value toValue(final File file)
	{
		return file!=null ? new PathValue(file.toPath()) : null;
	}

	/**
	 * Returns null, if {@code file} is null.
	 */
	@Contract("null, null -> null; !null, !null -> !null; null, !null -> fail; !null, null -> fail")
	public static Value toValue(final ZipFile file, final ZipEntry entry)
	{
		if(file!=null)
		{
			if(entry!=null)
				return new ZipValue(file, entry);
			else
				throw new IllegalArgumentException("if file is not null, entry must also be not null");
		}
		else
		{
			if(entry!=null)
				throw new IllegalArgumentException("if file is null, entry must also be null");
			else
				return null;
		}
	}

	public SetValue<?> map(final byte[] array)
	{
		return SetValue.map(this, toValue(array));
	}

	public SetValue<?> map(final InputStream stream)
	{
		return SetValue.map(this, toValue(stream));
	}

	public SetValue<?> map(final Path path)
	{
		return SetValue.map(this, toValue(path));
	}

	public SetValue<?> map(final File file)
	{
		return SetValue.map(this, toValue(file));
	}

	@Override
	void checkNotNull(final Value value, final Item item)
	{
		final long lengthIfKnown = value.estimateLength();
		if(lengthIfKnown>maximumLength)
			throw new DataLengthViolationException(this, item, lengthIfKnown, true);
	}

	DataLengthViolationOutputStream wrap(final OutputStream back, final Item exceptionItem)
	{
		return new DataLengthViolationOutputStream(this, back, exceptionItem);
	}

	public abstract static class Value
	{
		Value()
		{
			// just to make it package private
		}

		/**
		 * Estimates the length of this value.
		 * Returns -1 if length cannot be estimated in advance.
		 * This length is used for checking against {@link DataField#getMaximumLength()}.
		 * If this length is less than the actual length of this value,
		 * this may cause a {@link DataLengthViolationException} wrongly thrown.
		 * If this length is greater than the actual length of this value,
		 * an {@link DataLengthViolationException} is thrown nevertheless,
		 * but only after fetching at least {@link DataField#getMaximumLength()} bytes from the source.
		 * A typical source of the estimated length is
		 * {@link File#length()} or
		 * {@link HttpURLConnection#getContentLength()} or
		 * {@link ZipEntry#getSize()}.
		 */
		abstract long estimateLength();

		abstract byte[] asArraySub(DataField field, Item exceptionItem) throws IOException; // TODO put this directly into statement

		final byte[] asArray(final DataField field, final Item exceptionItem)
		{
			try
			{
				return asArraySub(field, exceptionItem);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(field.toString(), e);
			}
		}

		/**
		 * Puts the contents of this value into {@code digest} via
		 * {@link MessageDigest#update(byte[])}.
		 * After the invocation of this method, this value is exhausted.
		 * Therefore this method returns a new value equivalent to this value,
		 * which can be used instead.
		 */
		public abstract Value update(MessageDigest digest) throws IOException;

		/**
		 * Puts the contents of this value into {@code digest} via
		 * {@link MessageDigest#update(byte[])}.
		 * After the invocation of this method, this value is exhausted.
		 * Therefore this method returns a new value equivalent to this value,
		 * which can be used instead.
		 * Additionally checks for {@link #getMaximumLength() maximum length} and
		 * throw {@link DataLengthViolationException} if exceeded.
		 */
		abstract Value update(MessageDigest digest, DataConsumer consumer, DataField field, Item exceptionItem) throws IOException;

		@Override
		public abstract String toString();

		boolean exhausted = false;

		protected final void assertNotExhausted()
		{
			if(exhausted)
				throw new IllegalStateException(
						"Value already exhausted: " + this + "." +
						" Each DataField.Value can be used for at most one setter action.");
			exhausted = true;
		}

		abstract boolean put(
				@Nonnull VaultService service,
				@Nonnull String hash,
				@Nonnull VaultPutInfo info) throws IOException;
	}

	static final class ArrayValue extends Value
	{
		final byte[] array;

		@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
		ArrayValue(final byte[] array)
		{
			this.array = array;

			assert array!=null;
		}

		@Override
		long estimateLength()
		{
			return array.length;
		}

		@Override
		@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
		byte[] asArraySub(final DataField field, final Item exceptionItem)
		{
			assertNotExhausted();
			return array;
		}

		private static final int TO_STRING_LIMIT = 10;

		@Override
		public String toString()
		{
			final StringBuilder bf = new StringBuilder((2*TO_STRING_LIMIT)+30);
			bf.append("DataField.Value:");
			Hex.append(bf, array, Math.min(TO_STRING_LIMIT, array.length));

			if(array.length>TO_STRING_LIMIT)
				bf.append("...(").
					append(array.length).
					append(')');

			return bf.toString();
		}

		@Override
		public Value update(final MessageDigest digest)
		{
			assertNotExhausted();
			digest.update(array, 0, array.length);
			return new ArrayValue(array);
		}

		@Override
		Value update(
				final MessageDigest digest,
				final DataConsumer consumer,
				final DataField field,
				final Item exceptionItem)
		{
			assertNotExhausted();
			if(array.length>field.maximumLength)
				throw new DataLengthViolationException(field, exceptionItem, array.length, true);
			digest.update(array, 0, array.length);
			consumer.acceptBytes(array, array.length);
			consumer.acceptLength(array.length);
			return new ArrayValue(array);
		}

		@Override
		boolean put(
				final VaultService service,
				final String hash,
				final VaultPutInfo info)
		{
			return service.put(hash, array, info);
		}
	}

	abstract static class AbstractStreamValue extends Value
	{
		abstract InputStream openStream() throws IOException;
		abstract boolean exhaustsOpenStream();
		abstract AbstractStreamValue copyAfterExhaustion();

		@Override
		final byte[] asArraySub(final DataField field, final Item exceptionItem) throws IOException
		{
			assertNotExhausted();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try(InputStream stream = openStream())
			{
				stream.transferTo(field.wrap(baos, exceptionItem));
			}
			return baos.toByteArray();
		}

		@Override
		public final Value update(final MessageDigest digest) throws IOException
		{
			assertNotExhausted();
			final long estimateLength = estimateLength();
			final byte[] buf = new byte[estimateLength<=0 ? 5000 : min(5000, estimateLength)];
			if(exhaustsOpenStream())
			{
				final ByteArrayOutputStream bf = new ByteArrayOutputStream();
				try(InputStream in = openStream())
				{
					for(int len = in.read(buf); len>=0; len = in.read(buf))
					{
						digest.update(buf, 0, len);
						bf.write(buf, 0, len);
					}
				}
				return new ArrayValue(bf.toByteArray());
			}
			else
			{
				try(InputStream in = openStream())
				{
					for(int len = in.read(buf); len>=0; len = in.read(buf))
						digest.update(buf, 0, len);
				}
				return copyAfterExhaustion();
			}
		}

		@Override
		final Value update(
				final MessageDigest digest,
				final DataConsumer consumer,
				final DataField field,
				final Item exceptionItem) throws IOException
		{
			assertNotExhausted();
			final long estimateLength = estimateLength();
			final long maximumLength = field.getMaximumLength();
			if(estimateLength>maximumLength)
				throw new DataLengthViolationException(field, exceptionItem, estimateLength, true);

			final byte[] buf = new byte[estimateLength<=0 ? 5000 : min(5000, estimateLength)];
			long transferredLength = 0;
			if(exhaustsOpenStream())
			{
				final ByteArrayOutputStream bf = new ByteArrayOutputStream();
				try(InputStream in = openStream())
				{
					for(int len = in.read(buf); len>=0; len = in.read(buf))
					{
						transferredLength += len;
						if(transferredLength>maximumLength)
							throw new DataLengthViolationException(field, exceptionItem, transferredLength, false);

						digest.update(buf, 0, len);
						bf.write(buf, 0, len);
						consumer.acceptBytes(buf, len);
					}
				}
				consumer.acceptLength(transferredLength);
				return new ArrayValue(bf.toByteArray());
			}
			else
			{
				try(InputStream in = openStream())
				{
					for(int len = in.read(buf); len>=0; len = in.read(buf))
					{
						transferredLength += len;
						if(transferredLength>maximumLength)
							throw new DataLengthViolationException(field, exceptionItem, transferredLength, false);

						digest.update(buf, 0, len);
						consumer.acceptBytes(buf, len);
					}
				}
				consumer.acceptLength(transferredLength);
				return copyAfterExhaustion();
			}
		}

		@Override
		boolean put(
				final VaultService service,
				final String hash,
				final VaultPutInfo info) throws IOException
		{
			try(InputStream value = openStream())
			{
				return service.put(hash, value, info);
			}
		}
	}

	static final class StreamValue extends AbstractStreamValue
	{
		private final InputStream stream;

		StreamValue(final InputStream stream)
		{
			this.stream = stream;

			assert stream!=null;
		}

		@Override
		long estimateLength()
		{
			return -1;
		}

		@Override
		InputStream openStream()
		{
			return stream;
		}

		@Override
		boolean exhaustsOpenStream()
		{
			return true;
		}

		@Override
		AbstractStreamValue copyAfterExhaustion()
		{
			throw new RuntimeException();
		}

		@Override
		public String toString()
		{
			return "DataField.Value:" + stream;
		}
	}

	static final class PathValue extends AbstractStreamValue
	{
		final Path path;

		PathValue(final Path path)
		{
			this.path = path;

			assert path!=null;
		}

		@Override
		long estimateLength()
		{
			try
			{
				return Files.size(path);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(path.toAbsolutePath().toString(), e);
			}
		}

		@Override
		InputStream openStream() throws IOException
		{
			return Files.newInputStream(path);
		}

		@Override
		boolean exhaustsOpenStream()
		{
			return false;
		}

		@Override
		AbstractStreamValue copyAfterExhaustion()
		{
			return new PathValue(path);
		}

		@Override
		boolean put(
				final VaultService service,
				final String hash,
				final VaultPutInfo info) throws IOException
		{
			return service.put(hash, path, info);
		}

		@Override
		public String toString()
		{
			return "DataField.Value:" + path;
		}
	}

	static final class ZipValue extends AbstractStreamValue
	{
		private final ZipFile file;
		private final ZipEntry entry;

		ZipValue(final ZipFile file, final ZipEntry entry)
		{
			this.file = file;
			this.entry = entry;

			assert file!=null;
			assert entry!=null;
		}

		@Override
		long estimateLength()
		{
			// NOTICE
			// The following code is needed to avoid the zip bomb,
			// see https://en.wikipedia.org/wiki/Zip_bomb
			return entry.getSize();
		}

		@Override
		InputStream openStream() throws IOException
		{
			return file.getInputStream(entry);
		}

		@Override
		boolean exhaustsOpenStream()
		{
			return false;
		}

		@Override
		AbstractStreamValue copyAfterExhaustion()
		{
			return new ZipValue(file, entry);
		}

		@Override
		public String toString()
		{
			return "DataField.Value:" + file.getName() + '#' + entry.getName();
		}
	}

	/**
	 * @return null if vault is not enabled for this field
	 */
	public String getVaultBucket()
	{
		return store().getBucket();
	}

	/**
	 * @see SchemaInfo#checkVaultTrail(DataField)
	 */
	public long checkVaultTrail()
	{
		final Statement statement = // must be first to throw Model.NotConnectedException when needed
				checkVaultTrailStatement(Statement.Mode.NORMAL);
		final Transaction tx = getType().getModel().currentTransaction();
		return tx.connect.executor.query(
				tx.getConnection(),
				statement,
				null, false, longResultSetHandler);
	}

	Statement checkVaultTrailStatement(final Statement.Mode mode)
	{
		return store().checkVaultTrail(mode);
	}

	/**
	 * @return null if vault is not enabled for this field
	 */
	public DataFieldVaultInfo getVaultInfo()
	{
		return store().getVaultInfo();
	}

	DataFieldStore store()
	{
		final DataFieldStore store = this.store;
		if(store==null)
			throw new Model.NotConnectedException(getType().getModel());
		return store;
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault}.
	 */
	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	public StartsWithCondition startsWithIfSupported(final byte[] value)
	{
		return new StartsWithCondition(this, value);
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault}.
	 */
	public Condition startsWithIfSupported(final int offset, final byte[] value)
	{
		return new StartsWithCondition(this, offset, value);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getVaultBucket()} instead.
	 */
	@Deprecated
	public String getVaultServiceKey()
	{
		return getVaultBucket();
	}

	/**
	 * @deprecated Use {@link #startsWithIfSupported(byte[])} instead.
	 */
	@Deprecated
	public StartsWithCondition startsWith(final byte[] value)
	{
		return startsWithIfSupported(value);
	}
}
