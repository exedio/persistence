
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Item extends Search
{
	/**
	 * THIS IS A HACK
	 */
	private static int pkCounter = 0;
	
	/**
	 * TODO: must be at least package private
	 */
	public final int pk;

	/**
	 * Returns a string unique for this item in all other items of this application.
	 * For any item <code>a</code> the following holds true:
	 * <code>a.equals(findByID(a.getID()).</code>
	 * Does not activate this item, if it's not already active.
	 * @see #findByID(String)
	 */
	public final String getID()
	{
		return getClass().getName() + '.' + pk;
	}
	
	/**
	 * Returns true, if <code>o</code> represents the same item as this item.
	 * Is equivalent to
	 * <code>(o != null) && (o instanceof Item) && getID().equals(((Item)o).getID())</code>.
	 * Does not activate this item, if it's not already active.
	 */
	public final boolean equals(final Object o)
	{
		return (o!=null) && (getClass()==o.getClass()) && (pk==((Item)o).pk);
	}

	/**
	 * Returns a hash code, that is consistent with {@link #equals(Object)}.
	 * Note, that this is not neccessarily equivalent to <code>getID().hashCode()</code>.
	 * Does not activate this item, if it's not already active.
	 */
	public final int hashCode()
	{
		return getClass().hashCode() ^ pk;
	}
	
	/**
	 * Returns the primary item object representing the same item as this item object.
	 * For any two active item objects <code>a</code>, <code>b</code> the following holds true:
	 * <code>If and only if a.equals(b) then a.primaryItem() == b.primaryItem().</code>
	 * Does not activate this item, if it's not already active.
	 * Is guaranteed to be very cheap, if this item object is already primary, which means
	 * this method returns <code>this</code>.
	 */
	public final Item primaryItem()
	{
		throw new RuntimeException("not yet implemented");
	}

	/**
	 * Must never be public, since it does not throw exceptions for constraint violations.
	 * Subclasses (usually generated) must care about throwing these exception by calling
	 * {@link #throwInitialNotNullViolationException} and/or 
	 * {@link #throwInitialUniqueViolationException}.
	 * All this fiddling is needed, because one cannot wrap a <code>super()</code> call into a
	 * try-catch statement.
	 */
	protected Item(final AttributeValue[] initialAttributeValues)
	{
		putCache(initialAttributeValues);
		this.pk = pkCounter++; // TODO: THIS IS A HACK
		writeCache();
	}
	
	/**
	 * Throws a {@link NotNullViolationException}, if a not-null violation occured in the constructor.
	 */
	protected final void throwInitialNotNullViolationException() throws NotNullViolationException
	{
	}
	
	/**
	 * Throws a {@link UniqueViolationException}, if a unique violation occured in the constructor.
	 */
	protected final void throwInitialUniqueViolationException() throws UniqueViolationException
	{
	}
	
	protected final Object getAttribute(final Attribute attribute)
	{
		final AttributeMapping mapping = attribute.mapping;
		if(mapping!=null)
			return mapping.mapJava(getAttribute(mapping.sourceAttribute));

		return getCache(attribute);
	}
	
	protected final Object getAttribute(final Attribute attribute, final Object[] qualifiers)
	{
		final AttributeMapping mapping = attribute.mapping;
		if(mapping!=null)
			return mapping.mapJava(getAttribute(mapping.sourceAttribute));

		return getCache(attribute);
	}

	/**
	 * @throws NotNullViolationException
	 *         if value is null and attribute is {@link Attribute#isNotNull() not-null}.
	 * @throws ReadOnlyViolationException
	 *         if attribute is {@link Attribute#isReadOnly() read-only}.
	 */
	protected final void setAttribute(final Attribute attribute, final Object value)
	throws UniqueViolationException, NotNullViolationException, ReadOnlyViolationException
	{
		if(attribute.isReadOnly() || attribute.mapping!=null)
			throw new ReadOnlyViolationException(this, attribute);
		if(attribute.isNotNull() && value == null)
			throw new NotNullViolationException(this, attribute);

		putCache(attribute, value);
		writeCache();
	}
	
	protected final void setAttribute(final Attribute attribute, final Object[] qualifiers, final Object value)
	throws UniqueViolationException
	{
		putCache(attribute, value);
		writeCache();
	}
	
	/**
	 * Returns a URL pointing to the data of this persistent media attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	protected final String getMediaURL(final MediaAttribute attribute, final String variant)
	{
		return null;
	}

	/**
	 * Returns the major mime type of this persistent media attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	protected final String getMediaMimeMajor(final MediaAttribute attribute)
	{
		return null;
	}

	/**
	 * Returns the minor mime type of this persistent media attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	protected final String getMediaMimeMinor(final MediaAttribute attribute)
	{
		return null;
	}

	/**
	 * Returns a stream for fetching the data of this persistent media attribute.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * Returns null, if there is no data for this attribute.
	 */
	protected final InputStream getMediaData(final MediaAttribute attribute)
	{
		return null;
	}

	/**
	 * Provides data for this persistent media attribute.
	 * <b>Closes the stream only, when finishing normally!</b>
	 * @param data give null to remove data.
	 * @throws NotNullViolationException
	 *         if data is null and attribute is {@link Attribute#isNotNull() not-null}.
	 * @throws IOException if reading data throws an IOException.
	 */
	protected final void setMediaData(final MediaAttribute attribute, final OutputStream data,
												 final String mimeMajor, final String mimeMinor)
	throws NotNullViolationException, IOException
	{
		if(data!=null)
			data.close();
	}

	
	// item cache -------------------------------------------------------------------
	
	private final HashMap itemCache = new HashMap();
	private boolean present = false;
	private boolean dirty = false;

	private Object getCache(final Attribute attribute)
	{
		return itemCache.get(attribute);
	}
	
	private void putCache(final AttributeValue[] attributeValues)
	{
		for(int i = 0; i<attributeValues.length; i++)
			itemCache.put(attributeValues[i].attribute, attributeValues[i].value);
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	private void putCache(final Attribute attribute, final Object value)
	{
		itemCache.put(attribute, value);
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	private void writeCache()
	{
		if(!dirty)
			return;
		
		final Type type;
		try
		{
			type = (Type)getClass().getField("TYPE").get(null); // TODO: very inefficient
		}
		catch(IllegalAccessException e)
		{
			throw new SystemException(e);
		}
		catch(NoSuchFieldException e)
		{
			throw new SystemException(e);
		}
		
		Database.theInstance.write(type, pk, itemCache, present);

		present = true;
		dirty = false;
	}
	
}
