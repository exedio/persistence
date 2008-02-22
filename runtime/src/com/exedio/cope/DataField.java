/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.instrument.Wrapper;

public final class DataField extends Field<DataField.Value>
{
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
	
	/**
	 * @deprecated use {@link #toFinal()} and {@link #optional()} instead.
	 */
	@Deprecated
	public DataField(final Option option)
	{
		this(option.isFinal, option.optional, DEFAULT_LENGTH);

		if(option.unique)
			throw new RuntimeException("DataField cannot be unique");
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
	private BlobColumn column;
	private int bufferSizeDefault = -1;
	private int bufferSizeLimit = -1;

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final Type type = getType();
		this.model = type.getModel();
		final ConnectProperties properties = model.getProperties();
		column = new BlobColumn(table, this, name, optional, maximumLength);
		bufferSizeDefault = min(properties.dataFieldBufferSizeDefault.getIntValue(), maximumLength);
		bufferSizeLimit = min(properties.dataFieldBufferSizeLimit.getIntValue(), maximumLength);
		
		return column;
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
	public Class getWrapperSetterType()
	{
		return byte[].class; // TODO remove (use DataField.Value.class)
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper(
				boolean.class,
				"isNull",
				"Returns, whether there is no data for field {0}.",
				"getter"));
		
		result.add(
			new Wrapper(
				long.class,
				"getLength",
				"Returns the length of the data of the data field {0}.",
				"getter"));
			
		result.add(
			new Wrapper(
				byte[].class,
				"getArray",
				"Returns the value of the persistent field {0}.", // TODO better text
				"getter"));
		
		result.add(
			new Wrapper(
				void.class,
				"get",
				"Writes the data of this persistent data field into the given stream.",
				"getter").
			addThrows(IOException.class).
			addParameter(OutputStream.class));
			
		result.add(
			new Wrapper(
				void.class, "get",
				"Writes the data of this persistent data field into the given file.",
				"getter").
			addThrows(IOException.class).
			addParameter(File.class));
		
		if(!isfinal)
		{
			final Set<Class<? extends Throwable>> exceptions = getSetterExceptions();
			
			result.add(
				new Wrapper(
					void.class,
					"set",
					"Sets a new value for the persistent field {0}.", // TODO better text
					"setter").
				addThrows(exceptions).
				addParameter(Value.class));
			
			result.add(
				new Wrapper(
					void.class,
					"set",
					"Sets a new value for the persistent field {0}.", // TODO better text
					"setter").
				addThrows(exceptions).
				addParameter(byte[].class));

			result.add(
				new Wrapper(
					void.class,
					"set",
					"Sets a new value for the persistent field {0}.", // TODO better text
					"setter").
				addThrows(exceptions).
				addThrows(IOException.class).
				addParameter(InputStream.class));
			
			result.add(
				new Wrapper(
					void.class,
					"set",
					"Sets a new value for the persistent field {0}.", // TODO better text
					"setter").
				addThrows(exceptions).
				addThrows(IOException.class).
				addParameter(File.class));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * Returns, whether there is no data for this field.
	 */
	public boolean isNull(final Item item)
	{
		// TODO make this more efficient !!!
		return getLength(item)<0;
	}

	/**
	 * Returns the length of the data of this persistent data field.
	 * Returns -1, if there is no data for this field.
	 */
	public long getLength(final Item item)
	{
		return column.table.database.loadLength(model.getCurrentTransaction().getConnection(), column, item);
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
	public byte[] getArray(final Item item)
	{
		return column.table.database.load(model.getCurrentTransaction().getConnection(), column, item);
	}
	
	/**
	 * Reads data for this persistent data field
	 * and writes it into the given stream.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if data is null.
	 * @throws IOException if writing data throws an IOException.
	 */
	public void get(final Item item, final OutputStream data) throws IOException
	{
		if(false)
			throw new IOException(); // disables warning about throws clause, TODO
		if(data==null)
			throw new NullPointerException();
		
		column.table.database.load(model.getCurrentTransaction().getConnection(), column, item, data, this);
	}
	
	/**
	 * Reads data for this persistent data field
	 * and writes it into the given file.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if data is null.
	 * @throws IOException if writing data throws an IOException.
	 */
	public void get(final Item item, final File data) throws IOException
	{
		if(data==null)
			throw new NullPointerException();
		
		if(!isNull(item))
		{
			FileOutputStream target = null;
			try
			{
				target = new FileOutputStream(data);
				get(item, target);
			}
			finally
			{
				if(target!=null)
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
	@Override
	public void set(final Item item, final Value data) throws MandatoryViolationException, DataLengthViolationException
	{
		if(isfinal)
			throw new FinalViolationException(this, item);

		if(data==null)
		{
			if(!optional)
				throw new MandatoryViolationException(this, item);
		}
		else
		{
			checkNotNullValue(data, item);
		}
		
		column.table.database.store(model.getCurrentTransaction().getConnection(), column, item, data, this);
	}
	
	/**
	 * Provides data for this persistent data field.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if data is longer than {@link #getMaximumLength()}
	 */
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
	public void set(final Item item, final InputStream data)
	throws MandatoryViolationException, DataLengthViolationException, IOException
	{
		if(false)
			throw new IOException(); // disables warning about throws clause, TODO
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
	public void set(final Item item, final File data)
	throws MandatoryViolationException, DataLengthViolationException, IOException
	{
		if(false)
			throw new IOException(); // disables warning about throws clause, TODO
		set(item, toValue(data));
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
	
	public SetValue map(final byte[] array)
	{
		return map(toValue(array));
	}
	
	public SetValue map(final InputStream stream)
	{
		return map(toValue(stream));
	}
	
	public SetValue map(final File file)
	{
		return map(toValue(file));
	}
	
	@Override
	final void checkNotNullValue(final Value value, final Item item) throws MandatoryViolationException
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
			if(readBytes!=length)
				throw new RuntimeException("expected " + length + " bytes, but got " + readBytes);
				
			final int tooManyBytes = in.read(new byte[1]);
			if(tooManyBytes!=-1)
				throw new RuntimeException("expected " + length + " bytes, but got more.");
			
			in.close();
			return result;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	private static final char[] mapping = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	static final void appendAsHex(final byte[] in, final int len, final StringBuilder out)
	{
		for(int i = 0; i<len; i++)
		{
			final byte bi = in[i];
			out.append(mapping[(bi & 0xF0)>>4]);
			out.append(mapping[bi & 0x0F]);
		}
	}
	
	public abstract static class Value
	{
		/**
		 * Estimates the length of this value.
		 * Returns -1 if length cannot be estimated in advance.
		 * This length is used for checking against {@link DataField#getMaximumLength()}.
		 * If this length is greater than the actual length of this value,
		 * this may cause a {@link DataLengthViolationException} wrongly thrown.
		 * If this length is less than the actual length of this value,
		 * an {@link DataLengthViolationException} is thrown nevertheless,
		 * but only after fetching at least {@link DataField#getMaximumLength()} bytes from the source.
		 * A typical source of the estimated length is
		 * {@link File#length()} or {@link HttpURLConnection#getContentLength()}.
		 */
		abstract long estimateLength();
		
		abstract byte[] asArray(DataField field, Item exceptionItem); // TODO put this directly into statement
		
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
	
	public final static class ArrayValue extends Value
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
		byte[] asArray(final DataField field, final Item exceptionItem)
		{
			assertNotExhausted();
			return array;
		}
		
		public byte[] asArray()
		{
			return array;
		}
		
		private static final int TO_STRING_LIMIT = 10;
		
		@Override
		public String toString()
		{
			final StringBuilder bf = new StringBuilder((2*TO_STRING_LIMIT)+30);
			bf.append("DataField.Value:");
			appendAsHex(array, Math.min(TO_STRING_LIMIT, array.length), bf);

			if(array.length>TO_STRING_LIMIT)
				bf.append("...(").
					append(array.length).
					append(')');
			
			return bf.toString();
		}
	}
	
	abstract static class AbstractStreamValue extends Value
	{
		abstract InputStream openStream() throws IOException;
		
		@Override
		final byte[] asArray(final DataField field, final Item exceptionItem)
		{
			assertNotExhausted();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream stream = null;
			try
			{
				stream = openStream();
				field.copy(stream, baos, exceptionItem);
				stream.close();
				stream = null;
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				if(stream!=null)
				{
					try
					{
						stream.close();
					}
					catch(IOException e)
					{
						// IGNORE, because already in exception
					}
				}
			}
			return baos.toByteArray();
		}
	}
	
	final static class StreamValue extends AbstractStreamValue
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
		public String toString()
		{
			return "DataField.Value:" + stream.toString();
		}
	}
	
	final static class FileValue extends AbstractStreamValue
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
		public String toString()
		{
			return "DataField.Value:" + file.toString();
		}
	}
}
