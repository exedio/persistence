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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
		final Properties properties = model.getProperties();
		column = new BlobColumn(table, name, optional, maximumLength);
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
	
	// public methods ---------------------------------------------------------------
	
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
	public byte[] get(final Item item)
	{
		return column.table.database.load(model.getCurrentTransaction().getConnection(), column, item);
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
		if(isfinal)
			throw new FinalViolationException(this, item);

		if(data==null)
		{
			if(!optional)
				throw new MandatoryViolationException(this, item);
		}
		else
		{
			if(data.length>maximumLength)
				throw new DataLengthViolationException(this, item, data.length, true);
		}
		
		try
		{
			// TODO make more efficient implementation
			column.table.database.store(model.getCurrentTransaction().getConnection(), column, item, data!=null ? new ByteArrayInputStream(data) : null, this);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
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
		
		column.table.database.load(model.getCurrentTransaction().getConnection(), column, item, data, this);
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
		if(isfinal)
			throw new FinalViolationException(this, item);
		if(data==null && !optional)
			throw new MandatoryViolationException(this, item);
		
		column.table.database.store(model.getCurrentTransaction().getConnection(), column, item, data, this);
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
	 * @throws IOException if reading data throws an IOException.
	 */
	public void set(final Item item, final File data)
	throws MandatoryViolationException, DataLengthViolationException, IOException
	{
		InputStream source = null;
		try
		{
			if(data!=null)
			{
				final long length = data.length();
				if(length>maximumLength)
					throw new DataLengthViolationException(this, item, length, true);
				
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
	final void checkNotNullValue(final byte[] value, final Item item) throws MandatoryViolationException
	{
		if(value.length>maximumLength)
			throw new DataLengthViolationException(this, item, value.length, true);
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
	
	void copyAsHex(final InputStream in, final StringBuffer out, final Item exceptionItem) throws IOException
	{
		final long maximumLength = this.maximumLength;
		final byte[] b = new byte[min(bufferSizeLimit, maximumLength)];
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
