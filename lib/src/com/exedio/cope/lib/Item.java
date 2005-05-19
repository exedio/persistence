/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.lib.Attribute.Option;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;

public abstract class Item extends Cope
{
	private final Type type;

	final int pk;
	
	private boolean deleted = false;

	/**
	 * The row containing the item cache for this item, if this item is active.
	 * If you want to be sure, that you get a row, use {@link #getRow()}.
	 */
	private Row rowWhenActive;

	/**
	 * Returns a string unique for this item in all other items of the model.
	 * For any item <code>a</code> in its model <code>m</code>
	 * the following holds true:
	 * <code>a.equals(m.findByID(a.getCopeID()).</code>
	 * Does not activate this item, if it's not already active.
	 * Never returns null.
	 * @see Model#findByID(String)
	 */
	public final String getCopeID()
	{
		return type.getID() + '.' + type.getPrimaryKeyIterator().pk2id(pk);
	}
	
	/**
	 * Returns the type of this item.
	 * Never returns null.
	 */
	public final Type getCopeType()
	{
		return type;
	}

	/**
	 * Returns true, if <code>o</code> represents the same item as this item.
	 * Is equivalent to
	 * <code>(o != null) && (o instanceof Item) && getCopeID().equals(((Item)o).getCopeID())</code>.
	 * Does not activate this item, if it's not already active.
	 */
	public final boolean equals(final Object o)
	{
		return (o!=null) && (getClass()==o.getClass()) && (pk==((Item)o).pk);
	}

	/**
	 * Returns a hash code, that is consistent with {@link #equals(Object)}.
	 * Note, that this is not neccessarily equivalent to <code>getCopeID().hashCode()</code>.
	 * Does not activate this item, if it's not already active.
	 */
	public final int hashCode()
	{
		return getClass().hashCode() ^ pk;
	}
	
	public String toString()
	{
		return getCopeID();
	}

	/**
	 * Returns, whether this item is active.
	 */	
	public final boolean isActiveCopeItem()
	{
		return rowWhenActive!=null;
	}

	/**
	 * Returns the active item object representing the same item as this item object.
	 * For any two item objects <code>a</code>, <code>b</code> the following holds true:
	 * <p>
	 * If and only if <code>a.equals(b)</code> then <code>a.activeCopeItem() == b.activeCopeItem()</code>.
	 * <p>
	 * So it does for items, what {@link String#intern} does for strings.
	 * Does activate this item, if it's not already active.
	 * Is guaranteed to be very cheap, if this item object is already active, which means
	 * this method returns <code>this</code>.
	 * Never returns null.
	 */
	public final Item activeCopeItem()
	{
		if(rowWhenActive!=null)
			return this;
		else
			return getRow().item;
	}

	/**
	 * Must never be public, since it does not throw exceptions for constraint violations.
	 * Subclasses (usually generated) must care about throwing these exception by calling
	 * {@link #throwInitialNotNullViolationException} and/or 
	 * {@link #throwInitialUniqueViolationException}.
	 * All this fiddling is needed, because one cannot wrap a <code>super()</code> call into a
	 * try-catch statement.
	 * @throws ClassCastException
	 *         if one of the values in <code>initialAttributeValues</code>
	 *         is not compatible to it's attribute.
	 */
	protected Item(final AttributeValue[] initialAttributeValues)
		throws ClassCastException
	{
		this.type = Type.findByJavaClass(getClass());
		this.pk = type.getPrimaryKeyIterator().nextPK();
		final Row row = new Row(this, false);
		//System.out.println("create item "+type+" "+pk);

		try
		{
			for(int i = 0; i<initialAttributeValues.length; i++)
			{
				final AttributeValue av = initialAttributeValues[i];
				av.attribute.checkValue(av.value, null);
			}
		}
		catch(NotNullViolationException e)
		{
			initialNotNullViolationException = e;
			return;
		}
		catch(LengthViolationException e)
		{
			initialLengthViolationException = e;
			return;
		}

		row.put(initialAttributeValues);
		try
		{
			row.write();
		}
		catch(UniqueViolationException e)
		{
			initialUniqueViolationException = e;
			return;
		}

		this.rowWhenActive = row; // make active

		if(type==null)
			throw new NullPointerException(getClass().toString());
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
	}
	
	/**
	 * Reactivation constructor.
	 * Is used for internal purposes only.
	 * Does not actually create a new item, but a passive item object for
	 * an already existing item.
	 */
	protected Item(
		final ReactivationConstructorDummy reactivationDummy,
		final int pk)
	{
		this.type = Type.findByJavaClass(getClass());
		this.pk = pk;
		rowWhenActive = null; // make passive
		//System.out.println("reactivate item:"+type+" "+pk);

		if(reactivationDummy!=Type.REACTIVATION_DUMMY)
			throw new RuntimeException("reactivation constructor is for internal purposes only, don't use it in your application!");
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
	}
	
	private NotNullViolationException initialNotNullViolationException = null;

	/**
	 * Throws a {@link NotNullViolationException}, if a not-null violation occured in the constructor.
	 * @throws NotNullViolationException
	 *         if one of the values in <code>initialAttributeValues</code>
	 *         is either null or not specified
	 *         and it's attribute is {@link Attribute#isNotNull() not-null}.
	 */
	protected final void throwInitialNotNullViolationException() throws NotNullViolationException
	{
		if(initialNotNullViolationException!=null)
			throw initialNotNullViolationException;
	}
	
	private LengthViolationException initialLengthViolationException = null;

	/**
	 * Throws a {@link LengthViolationException}, if a length violation occured in the constructor.
	 * @throws LengthViolationException
	 *         if one of the values in <code>initialAttributeValues</code>
	 *         violated the length constraint of it's attribute.
	 */
	protected final void throwInitialLengthViolationException() throws LengthViolationException
	{
		if(initialLengthViolationException!=null)
			throw initialLengthViolationException;
	}
	
	private UniqueViolationException initialUniqueViolationException = null;
	
	/**
	 * Throws a {@link UniqueViolationException}, if a unique violation occured in the constructor.
	 */
	protected final void throwInitialUniqueViolationException() throws UniqueViolationException
	{
		if(initialUniqueViolationException!=null)
			throw initialUniqueViolationException;
	}
	
	public final Object getAttribute(final Function function)
	{
		if(function instanceof ObjectAttribute)
			return getAttribute((ObjectAttribute)function);
		else
			return getFunction((ComputedFunction)function);
	}

	public final Object getAttribute(final ObjectAttribute attribute)
	{
		return getRow().get(attribute);
	}
	
	public final Object getFunction(final Function function)
	{
		if(function instanceof ComputedFunction)
			return getFunction((ComputedFunction)function);
		else
			return getAttribute((ObjectAttribute)function);
	}

	public final Object getFunction(final ComputedFunction function)
	{
		final List sources = function.getSources();
		final Object[] values = new Object[sources.size()];
		int pos = 0;
		for(Iterator i = sources.iterator(); i.hasNext(); )
			values[pos++] = getFunction((Function)i.next());
	
		return function.mapJava(values);
	}
	
	/**
	 * @throws NotNullViolationException
	 *         if <code>value</code> is null and <code>attribute</code>
	 *         is {@link Attribute#isNotNull() not-null}.
	 * @throws ReadOnlyViolationException
	 *         if <code>attribute</code> is {@link Attribute#isReadOnly() read-only}.
	 * @throws ClassCastException
	 *         if <code>value</code> is not compatible to <code>attribute</code>.
	 */
	public final void setAttribute(final ObjectAttribute attribute, final Object value)
		throws
			UniqueViolationException,
			NotNullViolationException,
			LengthViolationException,
			ReadOnlyViolationException,
			ClassCastException
	{
		if(attribute.isReadOnly())
			throw new ReadOnlyViolationException(this, attribute);

		attribute.checkValue(value, this);

		final Row row = getRow();
		final Object previousValue = row.get(attribute);
		row.put(attribute, value);
		try
		{
			row.write();
		}
		catch(UniqueViolationException e)
		{
			row.put(attribute, previousValue);
			throw e;
		}
	}

	/**
	 * @throws ReadOnlyViolationException
	 *         if <code>attribute</code> is {@link Attribute#isReadOnly() read-only}.
	 */
	public final void touchAttribute(final DateAttribute attribute)
		throws
			UniqueViolationException,
			ReadOnlyViolationException
	{
		try
		{
			setAttribute(attribute, new Date()); // TODO: make a more efficient implementation
		}
		catch(NotNullViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(LengthViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	private final boolean isNull(final DataAttribute attribute)
	{
		if(attribute.isNotNull())
			return false;

		final Row row = getRow();

		final String mimeMajorFixed = attribute.fixedMimeMajor;
		if(mimeMajorFixed==null)
			return row.get(attribute.mimeMajor)==null;
		else
		{
			final String mimeMinorFixed = attribute.fixedMimeMinor;
			if(mimeMinorFixed==null)
				return row.get(attribute.mimeMinor)==null;
			else
				return row.get(attribute.exists)==null;
		}
	}
	
	private static final String getCompactExtension(final String mimeMajor, final String mimeMinor)
	{
		if("image".equals(mimeMajor))
		{
			if("jpeg".equals(mimeMinor) || "pjpeg".equals(mimeMinor))
				return ".jpg";
			else if("gif".equals(mimeMinor))
				return ".gif";
			else if("png".equals(mimeMinor))
				return ".png";
			else
				return null;
		}
		else if("text".equals(mimeMajor))
		{
			if("html".equals(mimeMinor))
				return ".html";
			else if("plain".equals(mimeMinor))
				return ".txt";
			else if("css".equals(mimeMinor))
				return ".css";
			else
				return null;
		}
		else
			return null;
	}

	private final void appendDataPath(
									final DataAttribute attribute, final DataAttributeVariant variant,
									final StringBuffer bf)
	{
		final String mimeMajor;
		final String mimeMinor;
		{
			final String mimeMajorFixed = attribute.fixedMimeMajor;
			if(mimeMajorFixed==null)
			{
				final Row row = getRow();
				mimeMajor = (String)row.get(attribute.mimeMajor);
				final String mimeMinorFixed = attribute.fixedMimeMinor;
				if(mimeMinorFixed==null)
					mimeMinor = (String)row.get(attribute.mimeMinor);
				else
					mimeMinor = mimeMinorFixed;
			}
			else
			{
				mimeMajor = mimeMajorFixed;
				final String mimeMinorFixed = attribute.fixedMimeMinor;
				if(mimeMinorFixed==null)
				{
					final Row row = getRow();
					mimeMinor = (String)row.get(attribute.mimeMinor);
				}
				else
					mimeMinor = mimeMinorFixed;
			}
		}

		bf.append(attribute.getType().getID()).
			append('/').
			append(attribute.getName());
		
		if(variant!=null)
		{
			bf.append('/').
				append(variant.getName());
		}

		bf.append('/').
			append(type.getPrimaryKeyIterator().pk2id(pk));

		final String compactExtension = getCompactExtension(mimeMajor, mimeMinor);
		if(compactExtension==null)
		{
			bf.append('.').
				append(mimeMajor).
				append('.').
				append(mimeMinor);
		}
		else
			bf.append(compactExtension);
	}
	
	private final File getDataFile(final DataAttribute attribute)
	{
		final File directory = getCopeType().getModel().getProperties().getDatadirPath();
		final StringBuffer buf = new StringBuffer();
		appendDataPath(attribute, null, buf);
		return new File(directory, buf.toString());
	}
	
	/**
	 * Returns a URL pointing to the data of this persistent data attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	public final String getURL(final DataAttribute attribute)
	{
		return getDataURL(attribute, null);
	}

	/**
	 * Returns a URL pointing to the data of this persistent data attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	public final String getURL(final DataAttributeVariant variant)
	{
		return getDataURL(variant.attribute, variant);
	}

	private final String getDataURL(final DataAttribute attribute, final DataAttributeVariant variant)
	{
		if(variant!=null && variant.attribute!=attribute)
			throw new RuntimeException();

		if(isNull(attribute))
			return null;

		final StringBuffer bf = new StringBuffer(getCopeType().getModel().getProperties().getDatadirUrl());
		appendDataPath(attribute, variant, bf);
		return bf.toString();
	}

	/**
	 * Returns the major mime type of this persistent data attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	public final String getMimeMajor(final DataAttribute attribute)
	{
		if(isNull(attribute))
			return null;

		final String fixed = attribute.fixedMimeMajor;
		if(fixed==null)
			return (String)getRow().get(attribute.mimeMajor);
		else
			return fixed;
	}

	/**
	 * Returns the minor mime type of this persistent data attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	public final String getMimeMinor(final DataAttribute attribute)
	{
		if(isNull(attribute))
			return null;

		final String fixed = attribute.fixedMimeMinor;
		if(fixed==null)
			return (String)getRow().get(attribute.mimeMinor);
		else
			return fixed;
	}

	/**
	 * Returns a stream for fetching the data of this persistent data attribute.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * Returns null, if there is no data for this attribute.
	 */
	public final InputStream getData(final DataAttribute attribute)
	{
		if(isNull(attribute))
			return null;

		final File file = getDataFile(attribute);
		try
		{
			return new FileInputStream(file);
		}
		catch(FileNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	/**
	 * Provides data for this persistent data attribute.
	 * Closes <data>data</data> after reading the contents of the stream.
	 * @param data give null to remove data.
	 * @throws NotNullViolationException
	 *         if data is null and attribute is {@link Attribute#isNotNull() not-null}.
	 * @throws IOException if reading data throws an IOException.
	 */
	public final void setData(final DataAttribute attribute, final InputStream data,
												 final String mimeMajor, final String mimeMinor)
	throws NotNullViolationException, IOException
	{
		try
		{
			if(data!=null)
			{
				if((mimeMajor==null&&attribute.fixedMimeMajor==null) ||
					(mimeMinor==null&&attribute.fixedMimeMinor==null))
					throw new RuntimeException("if data is not null, mime types must also be not null");
			}
			else
			{
				if(mimeMajor!=null||mimeMinor!=null)
					throw new RuntimeException("if data is null, mime types must also be null");
			}
	
			final boolean isNullPreviously = isNull(attribute);
			final File previousFile = isNullPreviously ? null : getDataFile(attribute);
	
			final Row row = getRow();
			if(attribute.fixedMimeMajor==null)
				row.put(attribute.mimeMajor, mimeMajor);
			if(attribute.fixedMimeMinor==null)
				row.put(attribute.mimeMinor, mimeMinor);
			if(attribute.exists!=null)
				row.put(attribute.exists, (data!=null) ? BooleanAttribute.TRUE : null);
	
			try
			{
				row.write();
			}
			catch(UniqueViolationException e)
			{
				new NestingRuntimeException(e);
			}
	
			if(data!=null)
			{
				final File file = getDataFile(attribute);
				final OutputStream out = new FileOutputStream(file);
				final byte[] b = new byte[20*1024];
				for(int len = data.read(b); len>=0; len = data.read(b))
					out.write(b, 0, len);
				out.close();
				data.close();
	
				// This is done after the new file is written,
				// to prevent loss of data, if writing the new file fails
				if(!isNullPreviously)
				{
					if(!previousFile.equals(file))
					{
						if(!previousFile.delete())
							throw new RuntimeException("deleting "+previousFile+" failed.");
					}
				}
			}
			else
			{
				if(!isNullPreviously)
				{
					if(!previousFile.delete())
						throw new RuntimeException("deleting "+previousFile+" failed.");
				}
			}
		}
		finally
		{
			if(data!=null)
				data.close();
		}
	}
	
	public final void deleteCopeItem()
			throws IntegrityViolationException
	{
		// TODO: additionally we must ensure, that any passive item objects of this item
		// are marked deleted when they are tried to be loaded.
		if(rowWhenActive!=null)
		{
			if(type.getRow(pk)!=rowWhenActive)
				throw new RuntimeException();
			rowWhenActive.delete();
			rowWhenActive = null;
		}
		else
		{
			final Row row = type.getRow(pk);
			if(row==null)
			{
				type.getModel().getDatabase().delete(type, pk);
			}
			else
			{
				if(row.item==this)
					throw new RuntimeException();
				if(row.item.rowWhenActive!=row)
					throw new RuntimeException();
				row.item.deleteCopeItem();
			}
		}
		deleted = true;
	}
	
	public final boolean isCopeItemDeleted()
	{
		return deleted;
	}

	public static final Attribute.Option DEFAULT = new Attribute.Option(false, false, false);

	public static final Attribute.Option READ_ONLY = new Attribute.Option(true, false, false);
	public static final Attribute.Option NOT_NULL = new Attribute.Option(false, true, false);
	public static final Attribute.Option UNIQUE = new Attribute.Option(false, false, true);

	public static final Attribute.Option READ_ONLY_NOT_NULL = new Attribute.Option(true, true, false);
	public static final Attribute.Option READ_ONLY_UNIQUE = new Attribute.Option(true, false, true);
	public static final Attribute.Option NOT_NULL_UNIQUE = new Attribute.Option(false, true, true);
	 
	public static final Attribute.Option READ_ONLY_NOT_NULL_UNIQUE = new Attribute.Option(true, true, true);
	
	// activation/deactivation -----------------------------------------------------
	
	/**
	 * Activates this item.
	 * After this method, {link #row} is guaranteed to be not null.
	 */
	private final Row getRow()
	{
		if(rowWhenActive!=null)
		{
			if(type.getRow(pk)!=rowWhenActive)
				throw new RuntimeException();
			return rowWhenActive;
		}
		else
		{
			final Row row = type.getRow(pk);
			if(row==null)
			{
				rowWhenActive = new Row(this, true);
				type.getModel().getDatabase().load(rowWhenActive);
				return rowWhenActive;
			}
			else
			{
				if(row.item==this)
					throw new RuntimeException();
				if(row.item.rowWhenActive!=row)
					throw new RuntimeException();
				return row;
			}
		}
	}

	public final void passivateCopeItem()
	{
		if(rowWhenActive!=null)
		{
			rowWhenActive.close();
			rowWhenActive = null;
		}
	}
	
	//-----------------------------------------
	
	protected static final ItemAttribute itemAttribute(final Option option, final Class targetTypeClass)
	{
		return new ItemAttribute(option, targetTypeClass);
	}
	
	protected static final StringAttribute stringAttribute(final Option option)
	{
		return new StringAttribute(option);
	}

	protected static final StringAttribute stringAttribute(final Option option, final int minimumLength)
	{
		return new StringAttribute(option, minimumLength);
	}

	protected static final StringAttribute stringAttribute(final Option option, final int minimumLength, final int maximumLength)
	{
		return new StringAttribute(option, minimumLength, maximumLength);
	}

	protected static final IntegerAttribute integerAttribute(final Option option)
	{
		return new IntegerAttribute(option);
	}
	
	protected static final LongAttribute longAttribute(final Option option)
	{
		return new LongAttribute(option);
	}
	
	protected static final DoubleAttribute doubleAttribute(final Option option)
	{
		return new DoubleAttribute(option);
	}
	
	protected static final DataAttribute dataAttribute(final Option option)
	{
		return new DataAttribute(option);
	}

	protected static final DataAttribute dataAttribute(final Option option, final String fixedMimeMajor)
	{
		return new DataAttribute(option, fixedMimeMajor);
	}

	protected static final DataAttribute dataAttribute(final Option option, final String fixedMimeMajor, final String fixedMimeMinor)
	{
		return new DataAttribute(option, fixedMimeMajor, fixedMimeMinor);
	}
	
	protected static final DataAttributeVariant dataAttributeVariant(final DataAttribute attribute)
	{
		return new DataAttributeVariant(attribute);
	}
	
	protected static final UniqueConstraint uniqueConstraint(final ObjectAttribute uniqueAttribute)
	{
		return new UniqueConstraint(uniqueAttribute);
	}

	protected static final UniqueConstraint uniqueConstraint(final ObjectAttribute uniqueAttribute1, final ObjectAttribute uniqueAttribute2)
	{
		return new UniqueConstraint(uniqueAttribute1, uniqueAttribute2);
	}
	
	protected static final UniqueConstraint uniqueConstraint(final ObjectAttribute uniqueAttribute1, final ObjectAttribute uniqueAttribute2, final ObjectAttribute uniqueAttribute3)
	{
		return new UniqueConstraint(uniqueAttribute1, uniqueAttribute2, uniqueAttribute3);
	}
	
	protected static final DateAttribute dateAttribute(final Option option)
	{
		return new DateAttribute(option);
	}

	/**
	 * @param forbidTimestampColumn
	 * 		forces the new date attribute to be implemented with an integer column
	 * 		holding the time value of the dates,
	 * 		even if the database supports timestamp columns.
	 */
	protected static final DateAttribute dateAttribute(final Option option, final boolean forbidTimestampColumn)
	{
		return new DateAttribute(option, forbidTimestampColumn);
	}
	
	protected static final BooleanAttribute booleanAttribute(final Option option)
	{
		return new BooleanAttribute(option);
	}

	protected static final EnumAttribute enumAttribute(final Option option, final Class enumClass)
	{
		return new EnumAttribute(option, enumClass);
	}

}
