
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
	private final Type type;

	/**
	 * TODO: THIS IS A HACK
	 */
	private static int pkCounter = 10;
	
	final int pk;

	/**
	 * Returns a string unique for this item in all other items of this application.
	 * For any item <code>a</code> the following holds true:
	 * <code>a.equals(findByID(a.getID()).</code>
	 * Does not activate this item, if it's not already active.
	 * Never returns null.
	 * @see #findByID(String)
	 */
	public final String getID()
	{
		return getClass().getName() + '.' + pk;
	}
	
	/**
	 * Returns the type of this item.
	 * Never returns null.
	 */
	public final Type getType()
	{
		return type;
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
	 * Returns the active item object representing the same item as this item object.
	 * For any two active item objects <code>a</code>, <code>b</code> the following holds true:
	 * <code>If and only if a.equals(b) then a.activeItem() == b.activeItem().</code>
	 * Does not activate this item, if it's not already active.
	 * Is guaranteed to be very cheap, if this item object is already active, which means
	 * this method returns <code>this</code>.
	 */
	public final Item activeItem()
	{
		if(itemCache!=null)
			return this;
		else
		{
			final Item activeItem = type.getActiveItem(pk);
			if(activeItem!=null)
				return activeItem;
			else
				return this;
		}
	}

	/**
	 * Must never be public, since it does not throw exceptions for constraint violations.
	 * Subclasses (usually generated) must care about throwing these exception by calling
	 * {@link #throwInitialNotNullViolationException} and/or 
	 * {@link #throwInitialUniqueViolationException}.
	 * All this fiddling is needed, because one cannot wrap a <code>super()</code> call into a
	 * try-catch statement.
	 * @throws ClassCastException if the values in <code>initialAttributeValues</code> are not
	 *                            compatible to their attributes.
	 */
	protected Item(final Type type, final AttributeValue[] initialAttributeValues)
	{
		itemCache = new HashMap(); // make active
		putCache(initialAttributeValues);
		this.type = type;
		this.pk = pkCounter++; // TODO: THIS IS A HACK
		type.putActiveItem(this);
		writeCache();
	}
	
	/**
	 * Reactivation constructor.
	 * Is used for internal purposes only.
	 * Does not actually create a new item, but an passive item object for
	 * an already existing item.
	 */
	protected Item(final Type type, final int pk)
	{
		this.type = type;
		this.pk = pk;
		itemCache = null; // make passive
		//System.out.println("reactivate item:"+type+" "+pk);
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

		activate();
		return getCache(attribute);
	}
	
	protected final Object getAttribute(final Attribute attribute, final Object[] qualifiers)
	{
		final AttributeMapping mapping = attribute.mapping;
		if(mapping!=null)
			return mapping.mapJava(getAttribute(mapping.sourceAttribute));

		activate();
		return getCache(attribute);
	}

	/**
	 * @throws NotNullViolationException
	 *         if value is null and attribute is {@link Attribute#isNotNull() not-null}.
	 * @throws ReadOnlyViolationException
	 *         if attribute is {@link Attribute#isReadOnly() read-only}.
	 * @throws ClassCastException if <code>value</code> is not compatible to <code>attribute</code>.
	 */
	protected final void setAttribute(final Attribute attribute, final Object value)
	throws UniqueViolationException, NotNullViolationException, ReadOnlyViolationException
	{
		if(attribute.isReadOnly() || attribute.mapping!=null)
			throw new ReadOnlyViolationException(this, attribute);
		if(attribute.isNotNull() && value == null)
			throw new NotNullViolationException(this, attribute);

		activate();
		putCache(attribute, value);
		writeCache();
	}

	/**
	 * @throws ClassCastException if <code>value</code> is not compatible to <code>attribute</code>.
	 */
	protected final void setAttribute(final Attribute attribute, final Object[] qualifiers, final Object value)
	throws UniqueViolationException
	{
		activate();
		putCache(attribute, value);
		writeCache();
	}
	
	/**
	 * Returns a URL pointing to the data of this persistent media attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	protected final String getMediaURL(final MediaAttribute attribute, final String variant)
	{
		activate();

		final String mimeMajor = (String)getCache(attribute.mimeMajor);
		if(mimeMajor==null)
			return null;

		final StringBuffer bf = new StringBuffer("/medias/");

		bf.append(attribute.getType().getJavaClass().getName()).
			append('/').
			append(attribute.getName());
		
		if(variant!=null)
		{
			bf.append('/').
				append(variant);
		}

		bf.append('/').
			append(pk);

		final String mimeMinor = (String)getCache(attribute.mimeMinor);
		final String extension;
		if("image".equals(mimeMajor))
		{
			if("jpeg".equals(mimeMinor) || "pjpeg".equals(mimeMinor))
				extension = ".jpg";
			else if("gif".equals(mimeMinor))
				extension = ".gif";
			else if("png".equals(mimeMinor))
				extension = ".png";
			else
				extension = null;
		}
		else
			extension = null;
		
		if(extension==null)
		{
			bf.append('.').
				append(mimeMajor).
				append('.').
				append(mimeMinor);
		}
		else
			bf.append(extension);
		
		return bf.toString();
	}

	/**
	 * Returns the major mime type of this persistent media attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	protected final String getMediaMimeMajor(final MediaAttribute attribute)
	{
		activate();
		return (String)getCache(attribute.mimeMajor);
	}

	/**
	 * Returns the minor mime type of this persistent media attribute.
	 * Returns null, if there is no data for this attribute.
	 */
	protected final String getMediaMimeMinor(final MediaAttribute attribute)
	{
		activate();
		return (String)getCache(attribute.mimeMinor);
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
		activate();
		putCache(attribute.mimeMajor, mimeMajor);
		putCache(attribute.mimeMinor, mimeMinor);
		writeCache();
		if(data!=null)
			data.close();
	}

	// activation/deactivation -----------------------------------------------------
	
	public final boolean isActive()
	{
		return itemCache!=null;
	}

	protected final void activate()
	{
		if(itemCache==null)
		{
			itemCache = new HashMap();
			Database.theInstance.load(type, pk, itemCache);
			type.putActiveItem(this);
		}
	}

	public final void passivate()
	{
		if(itemCache!=null)
		{
			type.removeActiveItem(this);
			writeCache();
			itemCache = null;
		}
	}

	// item cache -------------------------------------------------------------------


	private HashMap itemCache = null;
	private boolean present = false;
	private boolean dirty = false;
	
	private Object getCache(final Attribute attribute)
	{
		return attribute.cacheToSurface(itemCache.get(attribute.getMainColumn()));
	}
	
	private Object getCache(final Column column)
	{
		return itemCache.get(column);
	}
	
	private void putCache(final AttributeValue[] attributeValues)
	{
		for(int i = 0; i<attributeValues.length; i++)
		{
			final Attribute attribute = attributeValues[i].attribute;
			itemCache.put(attribute.getMainColumn(), attribute.surfaceToCache(attributeValues[i].value));
		}
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	private void putCache(final Attribute attribute, final Object value)
	{
		itemCache.put(attribute.getMainColumn(), attribute.surfaceToCache(value));
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	private void putCache(final Column column, final Object value)
	{
		itemCache.put(column, value);
		dirty = true; // TODO: check, whether the written attribute got really a new value
	}
	
	private void writeCache()
	{
		if(!dirty)
			return;
		
		Database.theInstance.store(type, pk, itemCache, present);

		present = true;
		dirty = false;
	}
	
}
