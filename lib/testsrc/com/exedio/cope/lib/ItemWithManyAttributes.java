
package com.exedio.cope.lib;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.MediaAttribute;
import com.exedio.cope.lib.StringAttribute;

/**
 * An item having many attributes.
 * @persistent
 */
public class ItemWithManyAttributes extends Item
{
	/**
	 * An attribute that is unique.
	 * @persistent
	 */
	public static final StringAttribute someString = new StringAttribute();

	/**
	 * An integer attribute
	 * @persistent
	 */
	public static final IntegerAttribute someInteger = new IntegerAttribute();

	/**
	 * A not-null integer attribute
	 * @persistent
	 * @not-null
	 */
	public static final IntegerAttribute someNotNullInteger = new IntegerAttribute();

	/**
	 * An attribute referencing another persistent item
	 * @persistent ItemWithoutAttributes
	 */
	public static final ItemAttribute someItem = new ItemAttribute();

	/**
	 * A media attribute.
	 * @persistent
	 * @variant SomeVariant
	 */
	public static final MediaAttribute someMedia = new MediaAttribute();

/**

	 **
	 * Constructs a new ItemWithManyAttributes with all the attributes initially needed.
	 * @param initialSomeNotNullInteger the initial value for attribute {@link #someNotNullInteger}.
	 * @generated
	 *
 */public ItemWithManyAttributes(
				final int initialSomeNotNullInteger)
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
			new com.exedio.cope.lib.AttributeValue(someNotNullInteger,new Integer(initialSomeNotNullInteger)),
		});
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someString}.
	 * @generated
	 *
 */public final String getSomeString()
	{
		return (String)getAttribute(this.someString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someString}.
	 * @generated
	 *
 */public final void setSomeString(final String someString)
	{
		try
		{
			setAttribute(this.someString,someString);
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.UniqueViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someInteger}.
	 * @generated
	 *
 */public final Integer getSomeInteger()
	{
		return (Integer)getAttribute(this.someInteger);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someInteger}.
	 * @generated
	 *
 */public final void setSomeInteger(final Integer someInteger)
	{
		try
		{
			setAttribute(this.someInteger,someInteger);
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.UniqueViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullInteger}.
	 * @generated
	 *
 */public final int getSomeNotNullInteger()
	{
		return ((Integer)getAttribute(this.someNotNullInteger)).intValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullInteger}.
	 * @generated
	 *
 */public final void setSomeNotNullInteger(final int someNotNullInteger)
	{
		try
		{
			setAttribute(this.someNotNullInteger,new Integer(someNotNullInteger));
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.UniqueViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someItem}.
	 * @generated
	 *
 */public final ItemWithoutAttributes getSomeItem()
	{
		return (ItemWithoutAttributes)getAttribute(this.someItem);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someItem}.
	 * @generated
	 *
 */public final void setSomeItem(final ItemWithoutAttributes someItem)
	{
		try
		{
			setAttribute(this.someItem,someItem);
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.UniqueViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * Returns a URL pointing to the data of the persistent attribute {@link #someMedia}.
	 * @generated
	 *
 */public final java.lang.String getSomeMediaURL()
	{
		return getMediaURL(this.someMedia,null);
	}/**

	 **
	 * Returns a URL pointing to the varied data of the persistent attribute {@link #someMedia}.
	 * @generated
	 *
 */public final java.lang.String getSomeMediaURLSomeVariant()
	{
		return getMediaURL(this.someMedia,"SomeVariant");
	}/**

	 **
	 * Returns the major mime type of the persistent media attribute {@link #someMedia}.
	 * @generated
	 *
 */public final java.lang.String getSomeMediaMimeMajor()
	{
		return getMediaMimeMajor(this.someMedia);
	}/**

	 **
	 * Returns the minor mime type of the persistent media attribute {@link #someMedia}.
	 * @generated
	 *
 */public final java.lang.String getSomeMediaMimeMinor()
	{
		return getMediaMimeMinor(this.someMedia);
	}/**

	 **
	 * Returns a stream for fetching the data of the persistent media attribute {@link #someMedia}.
	 * @generated
	 *
 */public final java.io.InputStream getSomeMediaData()
	{
		return getMediaData(this.someMedia);
	}/**

	 **
	 * Provides data for the persistent media attribute {@link #someMedia}.
	 * @generated
	 *
 */public final void setSomeMediaData(final java.io.OutputStream data,final java.lang.String mimeMajor,final java.lang.String mimeMinor)throws java.io.IOException
	{
		try
		{
			setMediaData(this.someMedia,data,mimeMajor,mimeMinor);
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * The persistent type information for itemWithManyAttributes.
	 * @generated
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithManyAttributes.class,
			new com.exedio.cope.lib.Attribute[]{
				someString,
				someInteger,
				someNotNullInteger,
				someItem,
				someMedia,
			},
			new com.exedio.cope.lib.UniqueConstraint[]{
			},
			new Runnable()
			{
				public void run()
				{
					someString.initialize("someString",false,false);
					someInteger.initialize("someInteger",false,false);
					someNotNullInteger.initialize("someNotNullInteger",false,true);
					someItem.initialize("someItem",false,false);
					someMedia.initialize("someMedia",false,false);
				}
			}
		)
;}
