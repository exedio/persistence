
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
	 * An boolean attribute
	 * @persistent
	 */
	public static final BooleanAttribute someBoolean = new BooleanAttribute();

	/**
	 * A not-null boolean attribute
	 * @persistent
	 * @not-null
	 */
	public static final BooleanAttribute someNotNullBoolean = new BooleanAttribute();

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
	 * A qualified attribute.
	 * @persistent
	 * @qualifier ItemWithoutAttributes
	 */
	public static final StringAttribute someQualifiedString = new StringAttribute();

/**

	 **
	 * Constructs a new ItemWithManyAttributes with all the attributes initially needed.
	 * @param initialSomeNotNullInteger the initial value for attribute {@link #someNotNullInteger}.
	 * @param initialSomeNotNullBoolean the initial value for attribute {@link #someNotNullBoolean}.
	 * @generated
	 *
 */public ItemWithManyAttributes(
				final int initialSomeNotNullInteger,
				final boolean initialSomeNotNullBoolean)
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
			new com.exedio.cope.lib.AttributeValue(someNotNullInteger,new Integer(initialSomeNotNullInteger)),
			new com.exedio.cope.lib.AttributeValue(someNotNullBoolean,(initialSomeNotNullBoolean?Boolean.TRUE:Boolean.TRUE)),
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
	 * Returns the value of the persistent attribute {@link #someBoolean}.
	 * @generated
	 *
 */public final Boolean getSomeBoolean()
	{
		return (Boolean)getAttribute(this.someBoolean);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someBoolean}.
	 * @generated
	 *
 */public final void setSomeBoolean(final Boolean someBoolean)
	{
		try
		{
			setAttribute(this.someBoolean,someBoolean);
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
	 * Returns the value of the persistent attribute {@link #someNotNullBoolean}.
	 * @generated
	 *
 */public final boolean getSomeNotNullBoolean()
	{
		return ((Boolean)getAttribute(this.someNotNullBoolean)).booleanValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullBoolean}.
	 * @generated
	 *
 */public final void setSomeNotNullBoolean(final boolean someNotNullBoolean)
	{
		try
		{
			setAttribute(this.someNotNullBoolean,(someNotNullBoolean?Boolean.TRUE:Boolean.TRUE));
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
	 * Returns the value of the persistent attribute {@link #someQualifiedString}.
	 * @generated
	 *
 */public final String getSomeQualifiedString(final ItemWithoutAttributes itemWithoutAttributes)
	{
		return (String)getAttribute(this.someQualifiedString,new Object[]{itemWithoutAttributes});
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someQualifiedString}.
	 * @generated
	 *
 */public final void setSomeQualifiedString(final ItemWithoutAttributes itemWithoutAttributes,final String someQualifiedString)
	{
		try
		{
			setAttribute(this.someQualifiedString,new Object[]{itemWithoutAttributes},someQualifiedString);
		}
		catch(com.exedio.cope.lib.UniqueViolationException e)
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
				someBoolean,
				someNotNullBoolean,
				someItem,
				someMedia,
				someQualifiedString,
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
					someBoolean.initialize("someBoolean",false,false);
					someNotNullBoolean.initialize("someNotNullBoolean",false,true);
					someItem.initialize("someItem",false,false);
					someMedia.initialize("someMedia",false,false);
					someQualifiedString.initialize("someQualifiedString",false,false);
				}
			}
		)
;}
