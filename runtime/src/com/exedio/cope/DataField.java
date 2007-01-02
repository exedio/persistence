/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;


public final class DataField extends Field<byte[]>
{
	private final long maximumLength;

	public static final long DEFAULT_LENGTH = 10*1000*1000;
	
	private DataField(final boolean isfinal, final boolean optional, final long maximumLength)
	{
		super(isfinal, optional, byte[].class);
		this.maximumLength = maximumLength;
		
		if(maximumLength<=0)
			throw new IllegalArgumentException("maximum length must be greater zero, but was " + maximumLength + '.');
	}
	
	// TODO, empty constructor missing, since DataField cannot be MANDATORY
	
	public DataField(final Option option)
	{
		this(option.isFinal, option.optional, DEFAULT_LENGTH);

		if(option.unique)
			throw new RuntimeException("DataField cannot be unique");
		if(!option.optional)
			throw new RuntimeException("DataField cannot be mandatory"); // TODO
		if(option.isFinal)
			throw new RuntimeException("DataField cannot be final"); // TODO
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
	
	Impl impl;
	private int bufferSizeDefault = -1;
	private int bufferSizeLimit = -1;

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final Type type = getType();
		final Model model = type.getModel();
		final Properties properties = model.getProperties();
		
		if(properties.hasDatadirPath())
		{
			final File typeDir = new File(properties.getDatadirPath(), type.id);
			final File fieldDir = new File(typeDir, name);
			impl = new FileImpl(fieldDir);
		}
		else
		{
			this.impl = new BlobImpl(model, table, name, optional);
		}
		
		final int maximumLengthInt = toInt(maximumLength);
		bufferSizeDefault = Math.min(properties.dataFieldBufferSizeDefault.getIntValue(), maximumLengthInt);
		bufferSizeLimit = Math.min(properties.dataFieldBufferSizeLimit.getIntValue(), maximumLengthInt);
		
		return impl.getColumn();
	}
	
	private static final int toInt(final long l)
	{
		return (int)Math.min(l, Integer.MAX_VALUE);
	}
	
	// public methods ---------------------------------------------------------------
	
	/**
	 * Returns, whether there is no data for this field.
	 */
	public boolean isNull(final Item item)
	{
		return impl.isNull(item);
	}

	/**
	 * Returns the length of the data of this persistent data field.
	 * Returns -1, if there is no data for this field.
	 */
	public long getLength(final Item item)
	{
		return impl.getLength(item);
	}
	
	/**
	 * Returns the data of this persistent data field.
	 * Returns null, if there is no data for this field.
	 */
	@Override
	public byte[] get(final Item item)
	{
		return impl.get(item);
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
	public void set(final Item item, final byte[] data) throws MandatoryViolationException, DataLengthViolationException
	{
		if(data!=null && data.length>maximumLength)
			throw new DataLengthViolationException(this, item, data.length, true);
		
		impl.set(item, data);
	}
	
	/**
	 * Reads data for this persistent data field
	 * and writes it into the given steam.
	 * Does nothing, if there is no data for this field.
	 * @throws NullPointerException
	 *         if data is null.
	 * @throws IOException if writing data throws an IOException.
	 */
	public void get(final Item item, final OutputStream data) throws IOException
	{
		if(data==null)
			throw new NullPointerException();
		
		impl.get(item, data);
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
		impl.set(item, data);
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
		
		impl.get(item, data);
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
		impl.set(item, data);
	}
	
	@Override
	final void checkValue(final Object value, final Item item) throws MandatoryViolationException
	{
		if(value == null)
		{
			if(!optional)
				throw new MandatoryViolationException(this, item);
		}
		else
		{
			if(!(value instanceof byte[]))
			{
				throw new ClassCastException(
						"expected a byte[], but was a " + value.getClass().getName() +
						" for " + toString() + '.');
			}
			
			final byte[] valueBytes = (byte[])value;
			if(valueBytes.length>maximumLength)
				throw new DataLengthViolationException(this, item, valueBytes.length, true);
		}
	}

	abstract class Impl
	{
		// TODO remove
		final boolean blob;
		
		Impl(final boolean blob)
		{
			this.blob = blob;
		}
		
		abstract Column getColumn();
		abstract boolean isNull(Item item);
		abstract long getLength(Item item);
		abstract byte[] get(Item item);
		abstract void set(Item item, byte[] data);
		abstract void get(Item item, OutputStream data) throws IOException;
		abstract void set(Item item, InputStream data) throws MandatoryViolationException, IOException;
		abstract void get(Item item, File data) throws IOException;
		abstract void set(Item item, File data) throws MandatoryViolationException, IOException;
		abstract void fillBlob(byte[] value, HashMap<BlobColumn, byte[]> blobs, Item item);
	}
	
	final class BlobImpl extends Impl
	{
		final Model model;
		final BlobColumn column;
		
		BlobImpl(final Model model, final Table table, final String name, final boolean optional)
		{
			super(true);
			this.model = model;
			this.column = new BlobColumn(table, name, optional, DataField.this.maximumLength);
		}
		
		@Override
		Column getColumn()
		{
			return column;
		}
		
		@Override
		boolean isNull(final Item item)
		{
			// TODO make this more efficient !!!
			return getLength(item)<0;
		}
		
		@Override
		long getLength(final Item item)
		{
			return column.table.database.loadLength(model.getCurrentTransaction().getConnection(), column, item);
		}
		
		@Override
		byte[] get(final Item item)
		{
			return column.table.database.load(model.getCurrentTransaction().getConnection(), column, item);
		}
		
		@Override
		void set(final Item item, final byte[] data)
		{
			try
			{
				// TODO make more efficient implementation
				column.table.database.store(model.getCurrentTransaction().getConnection(), column, item, data!=null ? new ByteArrayInputStream(data) : null, DataField.this);
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		void get(final Item item, final OutputStream data)
		{
			column.table.database.load(model.getCurrentTransaction().getConnection(), column, item, data, DataField.this);
		}
		
		@Override
		void set(final Item item, final InputStream data)
		throws MandatoryViolationException, IOException
		{
			column.table.database.store(model.getCurrentTransaction().getConnection(), column, item, data, DataField.this);
		}
		
		@Override
		void get(final Item item, final File data) throws IOException
		{
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
			// TODO maybe file should be deleted when result is null?, same in file mode
		}
		
		@Override
		void set(final Item item, final File data) throws MandatoryViolationException, IOException
		{
			InputStream source = null;
			try
			{
				if(data!=null)
				{
					final long length = data.length();
					if(length>maximumLength)
						throw new DataLengthViolationException(DataField.this, item, length, true);
					
					source =  new FileInputStream(data);
				}
				
				set(item, source);
			}
			finally
			{
				if(source!=null)
					source.close();
			}
		}
		
		@Override
		void fillBlob(final byte[] value, final HashMap<BlobColumn, byte[]> blobs, final Item item)
		{
			blobs.put(column, value);
		}
	}
	
	final class FileImpl extends Impl
	{
		final File directory;
		
		FileImpl(final File directory)
		{
			super(false);
			this.directory = directory;
		}
		
		@Override
		Column getColumn()
		{
			return null;
		}
		
		private File getStorage(final Item item)
		{
			return new File(directory, String.valueOf(item.type.getPkSource().pk2id(item.pk)));
		}
		
		@Override
		boolean isNull(final Item item)
		{
			final File file = getStorage(item);
			return !file.exists();
		}
		
		@Override
		long getLength(final Item item)
		{
			final File file = getStorage(item);

			return file.exists() ? file.length() : -1l;
		}

		@Override
		byte[] get(final Item item)
		{
			final File file = getStorage(item);
			if(file.exists())
			{
				try
				{
					return DataField.copy(new FileInputStream(file), file.length());
				}
				catch(FileNotFoundException e)
				{
					throw new RuntimeException(file.getAbsolutePath(), e);
				}
			}
			else
				return null;
		}
		
		@Override
		void set(final Item item, final byte[] data)
		{
			OutputStream out = null;
			try
			{
				final File file = getStorage(item);

				if(data!=null)
				{
					out = new FileOutputStream(file);
					out.write(data, 0, data.length);
					out.close();
				}
				else
				{
					if(file.exists())
					{
						if(!file.delete())
							throw new RuntimeException("deleting "+file+" failed.");
					}
				}
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				if(out!=null)
				{
					try
					{
						out.close();
					}
					catch(IOException e)
					{
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		@Override
		void get(final Item item, final OutputStream data) throws IOException
		{
			final File file = getStorage(item);
			if(file.exists())
			{
				FileInputStream in = null;
				try
				{
					if(data!=null)
					{
						in = new FileInputStream(file);
						DataField.this.copy(in, data, file.length(), item);
						in.close();
						data.close();
					}
					else
					{
						if(file.exists())
						{
							if(!file.delete())
								throw new RuntimeException("deleting "+file+" failed.");
						}
					}
				}
				finally
				{
					if(data!=null)
						data.close();
					if(in!=null)
						in.close();
				}
			}
		}

		@Override
		void set(final Item item, final InputStream data) throws MandatoryViolationException, IOException
		{
			OutputStream out = null;
			try
			{
				final File file = getStorage(item);

				if(data!=null)
				{
					out = new FileOutputStream(file);
					DataField.this.copy(data, out, item);
					out.close();
					data.close();
				}
				else
				{
					if(file.exists())
					{
						if(!file.delete())
							throw new RuntimeException("deleting "+file+" failed.");
					}
				}
			}
			finally
			{
				if(data!=null)
					data.close();
				if(out!=null)
					out.close();
			}
		}
		
		private final void copy(final File source, final File target, final Item item) throws IOException
		{
			final long length = source.length();
			if(length>0)
			{
				if(length>maximumLength)
					throw new DataLengthViolationException(DataField.this, item, length, true);
				
				InputStream sourceS = null;
				OutputStream targetS = null;
				try
				{
					sourceS = new FileInputStream(source);
					targetS = new FileOutputStream(target);
					DataField.this.copy(sourceS, targetS, length, item);
				}
				finally
				{
					if(sourceS!=null)
						sourceS.close();
					if(targetS!=null)
						targetS.close();
				}
			}
			else if(length==0)
			{
				if(target.exists())
				{
					final long targetLength = target.length();
					if(targetLength==0)
						; // do nothing
					else if(targetLength<0)
						throw new RuntimeException(String.valueOf(targetLength));
					else
					{
						target.delete();
						target.createNewFile();
					}
				}
				else
					target.createNewFile();
			}
			else
				throw new RuntimeException(String.valueOf(length));
		}
		
		@Override
		void get(final Item item, final File data) throws IOException
		{
			final File file = getStorage(item);
			if(file.exists())
				copy(file, data, item);
			// TODO maybe file should be deleted when result is null?, same in blob mode
		}
		
		@Override
		void set(final Item item, final File data) throws MandatoryViolationException, IOException
		{
			final File file = getStorage(item);

			if(data!=null)
				copy(data, file, item);
			else
			{
				if(file.exists())
				{
					if(!file.delete())
						throw new RuntimeException("deleting "+file+" failed.");
				}
			}
		}
		
		@Override
		void fillBlob(final byte[] value, final HashMap<BlobColumn, byte[]> blobs, final Item item)
		{
			set(item, value);
		}
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
		
		final byte[] b = new byte[Math.min(bufferSizeLimit, toInt(length))];
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
	
	void copyAsHex(final InputStream in, final StringBuffer out, final Item exceptionItem) throws IOException
	{
		final long maximumLength = this.maximumLength;
		final byte[] b = new byte[Math.max(bufferSizeLimit, toInt(maximumLength))];
		//System.out.println("-------------- "+length+" ----- "+b.length);
		
		long transferredLength = 0;
		for(int len = in.read(b); len>=0; len = in.read(b))
		{
			transferredLength += len;
			if(transferredLength>maximumLength)
				throw new DataLengthViolationException(this, exceptionItem, transferredLength, false);
			
			appendAsHex(b, len, out);
		}
		in.close();
	}

	private static final char[] mapping = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	static final void appendAsHex(final byte[] in, final int len, final StringBuffer out)
	{
		for(int i = 0; i<len; i++)
		{
			final byte bi = in[i];
			out.append(mapping[(bi & 0xF0)>>4]);
			out.append(mapping[bi & 0x0F]);
		}
	}
}
