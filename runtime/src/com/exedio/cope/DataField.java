/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.util.Hex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class DataField extends Field<DataField.Value>
{
	private static final long serialVersionUID = 1l;

	private final long maximumLength;

	public static final long DEFAULT_LENGTH = 10*1000*1000;

	private DataField(final boolean isfinal, final boolean optional, final long maximumLength)
	{
		super(isfinal, optional, Value.class);
		this.maximumLength = maximumLength;

		if(maximumLength<=0)
			throw new IllegalArgumentException("maximum length must be greater zero, but was " + maximumLength + '.');
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

	public DataField lengthMax(final long maximumLength)
	{
		return new DataField(isfinal, optional, maximumLength);
	}

	public long getMaximumLength()
	{
		return maximumLength;
	}

	// second initialization phase ---------------------------------------------------

	private Model model;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private BlobColumn column;
	private int bufferSizeDefault = -1;
	private int bufferSizeLimit = -1;

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final Type<?> type = getType();
		this.model = type.getModel();
		final ConnectProperties properties = model.getConnectProperties();
		column = new BlobColumn(table, name, optional, maximumLength);
		bufferSizeDefault = min(properties.dataFieldBufferSizeDefault, maximumLength);
		bufferSizeLimit = min(properties.dataFieldBufferSizeLimit, maximumLength);

		return column;
	}

	/**
	 * for tests only
	 */
	void setBufferSize(final int defaulT, final int limit)
	{
		assert defaulT!=-1;
		assert limit!=-1;
		this.bufferSizeDefault = defaulT;
		this.bufferSizeLimit   = limit;
	}

	private static final int toInt(final long l)
	{
		return min(Integer.MAX_VALUE, l);
	}

	/**
	 * @throws IllegalArgumentException if either i or l is negative
	 */
	public static final int min(final int i, final long l)
	{
		if(i<0)
			throw new IllegalArgumentException("i must not be negative, but was " + i);
		if(l<0)
			throw new IllegalArgumentException("l must not be negative, but was " + l);

		return i<=l ? i : (int)l;
	}

	@Override
	@Deprecated
	public Class<?> getInitialType()
	{
		return byte[].class; // TODO remove (use DataField.Value.class)
	}

	/**
	 * Returns, whether there is no data for this field.
	 */
	@Wrap(order=10, doc="Returns, whether there is no data for field {0}.")
	public boolean isNull(final Item item)
	{
		// TODO make this more efficient !!!
		return getLength(item)<0;
	}

	/**
	 * Returns the length of the data of this persistent data field.
	 * Returns -1, if there is no data for this field.
	 */
	@Wrap(order=20,doc="Returns the length of the data of the data field {0}.")
	public long getLength(final Item item)
	{
		final Transaction tx = model.currentTransaction();
		return column.loadLength(tx.getConnection(), tx.connect.executor, item);
	}

	/**
	 * Returns the data of this persistent data field.
	 * Returns null, if there is no data for this field.
	 */
	@Override
	public ArrayValue get(final Item item)
	{
		final byte[] array = getArray(item);
		return array!=null ? new ArrayValue(array) : null;
	}

	/**
	 * Returns the data of this persistent data field.
	 * Returns null, if there is no data for this field.
	 */
	@Wrap(order=30, doc="Returns the value of the persistent field {0}.") // TODO better text
	public byte[] getArray(final Item item)
	{
		final Transaction tx = model.currentTransaction();
		return column.load(tx.getConnection(), tx.connect.executor, item);
	}

	/**
	 * Reads data for this persistent data field
	 * and writes it into the given stream.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if data is null.
	 * @throws IOException if writing data throws an IOException.
	 */
	@Wrap(order=40,
			doc="Writes the data of this persistent data field into the given stream.",
			thrown=@Wrap.Thrown(IOException.class))
	public void get(final Item item, final OutputStream data) throws IOException
	{
		if(data==null)
			throw new NullPointerException();

		final Transaction tx = model.currentTransaction();
		column.load(tx.getConnection(), tx.connect.executor, item, data, this);
	}

	/**
	 * Reads data for this persistent data field
	 * and writes it into the given file.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if data is null.
	 * @throws IOException if writing data throws an IOException.
	 */
	@Wrap(order=50,
			doc="Writes the data of this persistent data field into the given file.",
			thrown=@Wrap.Thrown(IOException.class))
	public void get(final Item item, final File data) throws IOException
	{
		if(data==null)
			throw new NullPointerException();

		if(!isNull(item))
		{
			final FileOutputStream target = new FileOutputStream(data);
			try
			{
				get(item, target);
			}
			finally
			{
				target.close();
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
	public void set(final Item item, final Value data) throws MandatoryViolationException, DataLengthViolationException
	{
		if(isfinal)
			throw FinalViolationException.create(this, item);

		if(data==null)
		{
			if(!optional)
				throw MandatoryViolationException.create(this, item);
		}
		else
		{
			checkNotNull(data, item);
		}

		final Transaction tx = model.currentTransaction();
		column.store(tx.getConnection(), tx.connect.executor, item, data, this);
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
	public void set(final Item item, final byte[] data) throws MandatoryViolationException, DataLengthViolationException
	{
		set(item, toValue(data));
	}

	/**
	 * Provides data for this persistent data field.
	 * Closes <data>data</data> after reading the contents of the stream.
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
	public void set(final Item item, final InputStream data)
	throws MandatoryViolationException, DataLengthViolationException, IOException
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
	public void set(final Item item, final File data)
	throws MandatoryViolationException, DataLengthViolationException, IOException
	{
		set(item, toValue(data));
	}

	private static final class InitialAndIOThrown implements ThrownGetter<Field<?>>
	{
		public Set<Class<? extends Throwable>> get(final Field<?> feature)
		{
			final Set<Class<? extends Throwable>> result = feature.getInitialExceptions();
			result.add(IOException.class);
			return result;
		}
	}

	/**
	 * Returns null, if <code>array</code> is null.
	 */
	public static Value toValue(final byte[] array)
	{
		return array!=null ? new ArrayValue(array) : null;
	}

	/**
	 * Returns null, if <code>stream</code> is null.
	 */
	public static Value toValue(final InputStream stream)
	{
		return stream!=null ? new StreamValue(stream) : null;
	}

	/**
	 * Returns null, if <code>file</code> is null.
	 */
	public static Value toValue(final File file)
	{
		return file!=null ? new FileValue(file) : null;
	}

	/**
	 * Returns null, if <code>file</code> is null.
	 */
	public static Value toValue(final ZipFile file, final ZipEntry entry)
	{
		return file!=null ? new ZipValue(file, entry) : null;
	}

	public SetValue<?> map(final byte[] array)
	{
		return map(toValue(array));
	}

	public SetValue<?> map(final InputStream stream)
	{
		return map(toValue(stream));
	}

	public SetValue<?> map(final File file)
	{
		return map(toValue(file));
	}

	@Override
	final void checkNotNull(final Value value, final Item item) throws MandatoryViolationException
	{
		final long lengthIfKnown = value.estimateLength();
		if(lengthIfKnown>maximumLength)
			throw new DataLengthViolationException(this, item, lengthIfKnown, true);
	}

	final void copy(final InputStream in, final OutputStream out, final Item exceptionItem) throws IOException
	{
		copy(in, out, bufferSizeDefault, exceptionItem);
	}

	final void copy(final InputStream in, final OutputStream out, final long length, final Item exceptionItem) throws IOException
	{
		if(length==0)
			return;

		assert length>0;

		final byte[] b = new byte[min(bufferSizeLimit, length)];
		//System.out.println("-------------- "+length+" ----- "+b.length);

		final long maximumLength = this.maximumLength;
		long transferredLength = 0;
		for(int len = in.read(b); len>=0; len = in.read(b))
		{
			transferredLength += len;
			if(transferredLength>maximumLength)
				throw new DataLengthViolationException(this, exceptionItem, transferredLength, false);

			out.write(b, 0, len);
		}
	}

	static final byte[] copy(final InputStream in, final long length)
	{
		try
		{
			if(length==0)
				return new byte[]{};

			if(length>Integer.MAX_VALUE)
				throw new RuntimeException("byte array cannot be longer than int");

			assert length>0;

			final byte[] result = new byte[toInt(length)];
			final int readBytes = in.read(result);
			// TODO
			// method InputStream.read(byte[]) may read less than result.length bytes
			// even is the stream is not yet at its end
			// Probably we could use
			//    new BufferedInputStream(in).read(result);
			// to rely on complete reading guaranteed by
			// BufferedInputStream#read(byte[]) which extends contract of
			// InputStream#read(byte[])
			if(readBytes!=length)
				throw new RuntimeException("expected " + length + " bytes, but got " + readBytes + ", TODO not yet fully implemented");

			final int tooManyBytes = in.read(new byte[1]);
			if(tooManyBytes!=-1)
				throw new RuntimeException("expected " + length + " bytes, but got more.");

			in.close();
			return result;
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
		}
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

		public abstract Value update(MessageDigest digest) throws IOException;

		@Override
		public abstract String toString();

		boolean exhausted = false;

		protected final void assertNotExhausted()
		{
			if(exhausted)
				throw new IllegalStateException(
						"Value already exhausted: " + toString() + "." +
						" Each DataField.Value can be used for at most one setter action.");
			exhausted = true;
		}
	}

	static final class ArrayValue extends Value
	{
		final byte[] array;

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
	}

	abstract static class AbstractStreamValue extends Value
	{
		abstract InputStream openStream() throws IOException;
		abstract boolean exhaustsOpenStream();
		abstract AbstractStreamValue copy();

		@Override
		final byte[] asArraySub(final DataField field, final Item exceptionItem) throws IOException
		{
			assertNotExhausted();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final InputStream stream = openStream();
			try
			{
				field.copy(stream, baos, exceptionItem);
			}
			finally
			{
				stream.close();
			}
			return baos.toByteArray();
		}

		@Override
		public final Value update(final MessageDigest digest) throws IOException
		{
			assertNotExhausted();
			if(exhaustsOpenStream())
			{
				final long estimateLength = estimateLength();
				final byte[] buf = new byte[estimateLength<=0 ? 5000 : min(5000, estimateLength)];
				final ByteArrayOutputStream bf = new ByteArrayOutputStream();
				final InputStream in = openStream();
				try
				{
					for(int len = in.read(buf); len>=0; len = in.read(buf))
					{
						digest.update(buf, 0, len);
						bf.write(buf, 0, len);
					}
				}
				finally
				{
					in.close();
				}
				return new ArrayValue(bf.toByteArray());
			}
			else
			{
			final long estimateLength = estimateLength();
			final byte[] buf = new byte[estimateLength<=0 ? 5000 : min(5000, estimateLength)];
			final InputStream in = openStream();
			try
			{
				for(int len = in.read(buf); len>=0; len = in.read(buf))
					digest.update(buf, 0, len);
			}
			finally
			{
				in.close();
			}
			return copy();
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
		AbstractStreamValue copy()
		{
			throw new RuntimeException();
		}

		@Override
		public String toString()
		{
			return "DataField.Value:" + stream.toString();
		}
	}

	static final class FileValue extends AbstractStreamValue
	{
		private final File file;

		FileValue(final File file)
		{
			this.file = file;

			assert file!=null;
		}

		@Override
		long estimateLength()
		{
			return file.length();
		}

		@Override
		InputStream openStream() throws FileNotFoundException
		{
			return new FileInputStream(file);
		}

		@Override
		boolean exhaustsOpenStream()
		{
			return false;
		}

		@Override
		AbstractStreamValue copy()
		{
			return new FileValue(file);
		}

		@Override
		public String toString()
		{
			return "DataField.Value:" + file.toString();
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
			// see http://en.wikipedia.org/wiki/Zip_bomb
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
		AbstractStreamValue copy()
		{
			return new ZipValue(file, entry);
		}

		@Override
		public String toString()
		{
			return "DataField.Value:" + file.toString() + '#' + entry.getName();
		}
	}

	public StartsWithCondition startsWith(final byte[] value)
	{
		return new StartsWithCondition(this, value);
	}
}
