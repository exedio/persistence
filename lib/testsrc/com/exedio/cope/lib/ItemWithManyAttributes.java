
package com.exedio.cope.lib;

import com.exedio.cope.lib.mapping.UppercaseMapping;

/**
 * An item having many attributes.
 * @persistent
 */
public class ItemWithManyAttributes extends Item
{
	/**
	 * A string attribute.
	 * @persistent
	 */
	public static final StringAttribute someString = new StringAttribute(DEFAULT);

	/**
	 * The code of the item in upper case.
	 * @persistent
	 * @mapped
	 */
	public static final StringAttribute someStringUpperCase = new StringAttribute(DEFAULT, new UppercaseMapping(someString));

	/**
	 * A not-null string attribute.
	 * @persistent
	 */
	public static final StringAttribute someNotNullString = new StringAttribute(NOT_NULL);

	/**
	 * An integer attribute
	 * @persistent
	 */
	public static final IntegerAttribute someInteger = new IntegerAttribute(DEFAULT);

	/**
	 * A not-null integer attribute
	 * @persistent
	 */
	public static final IntegerAttribute someNotNullInteger = new IntegerAttribute(NOT_NULL);

	/**
	 * An boolean attribute
	 * @persistent
	 */
	public static final BooleanAttribute someBoolean = new BooleanAttribute(DEFAULT);

	/**
	 * A not-null boolean attribute
	 * @persistent
	 */
	public static final BooleanAttribute someNotNullBoolean = new BooleanAttribute(NOT_NULL);

	/**
	 * An attribute referencing another persistent item
	 * @persistent
	 */
	public static final ItemAttribute someItem = new ItemAttribute(DEFAULT, ItemWithoutAttributes.class);

	/**
	 * An not-null attribute referencing another persistent item
	 * @persistent
	 */
	public static final ItemAttribute someNotNullItem = new ItemAttribute(NOT_NULL, ItemWithoutAttributes.class);

	/**
	 * An enumeration attribute
	 * @persistent
	 */
	public static final EnumerationAttribute someEnumeration = new EnumerationAttribute(DEFAULT, SomeEnumeration.class);

	/**
	 * A not-null enumeration attribute
	 * @persistent
	 */
	public static final EnumerationAttribute someNotNullEnumeration = new EnumerationAttribute(NOT_NULL, SomeEnumeration.class);

	/**
	 * A media attribute.
	 * @persistent
	 * @variant SomeVariant
	 */
	public static final MediaAttribute someMedia = new MediaAttribute(DEFAULT);

	/**
	 * A qualified attribute.
	 * @persistent
	 * @qualifier ItemWithoutAttributes
	 */
	public static final StringAttribute someQualifiedString = new StringAttribute(DEFAULT);

	/**
	 * A class representing the possible states of the persistent enumeration attribute {@link #someEnumeration}.
	 */
	public static final class SomeEnumeration extends EnumerationValue
	{
		public static final int enumValue1NUM = 100;
		public static final SomeEnumeration enumValue1 = new SomeEnumeration();

		public static final int enumValue2NUM = 200;
		public static final SomeEnumeration enumValue2 = new SomeEnumeration();

		public static final int enumValue3NUM = 300;
		public static final SomeEnumeration enumValue3 = new SomeEnumeration();
	}


/**

	 **
	 * Constructs a new ItemWithManyAttributes with all the attributes initially needed.
	 * @param initialSomeNotNullString the initial value for attribute {@link #someNotNullString}.
	 * @param initialSomeNotNullInteger the initial value for attribute {@link #someNotNullInteger}.
	 * @param initialSomeNotNullBoolean the initial value for attribute {@link #someNotNullBoolean}.
	 * @param initialSomeNotNullItem the initial value for attribute {@link #someNotNullItem}.
	 * @param initialSomeNotNullEnumeration the initial value for attribute {@link #someNotNullEnumeration}.
	 * @throws com.exedio.cope.lib.NotNullViolationException if initialSomeNotNullString, initialSomeNotNullItem, initialSomeNotNullEnumeration is not null.
	 * @author cope instrumentor
	 *
 */public ItemWithManyAttributes(
				final String initialSomeNotNullString,
				final int initialSomeNotNullInteger,
				final boolean initialSomeNotNullBoolean,
				final ItemWithoutAttributes initialSomeNotNullItem,
				final SomeEnumeration initialSomeNotNullEnumeration)
			throws
				com.exedio.cope.lib.NotNullViolationException
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
			new com.exedio.cope.lib.AttributeValue(someNotNullString,initialSomeNotNullString),
			new com.exedio.cope.lib.AttributeValue(someNotNullInteger,new Integer(initialSomeNotNullInteger)),
			new com.exedio.cope.lib.AttributeValue(someNotNullBoolean,(initialSomeNotNullBoolean?Boolean.TRUE:Boolean.FALSE)),
			new com.exedio.cope.lib.AttributeValue(someNotNullItem,initialSomeNotNullItem),
			new com.exedio.cope.lib.AttributeValue(someNotNullEnumeration,initialSomeNotNullEnumeration),
		});
		throwInitialNotNullViolationException();
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */private ItemWithManyAttributes(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someString}.
	 * @author cope instrumentor
	 *
 */public final String getSomeString()
	{
		return (String)getAttribute(this.someString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someString}.
	 * @author cope instrumentor
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
	 * Returns the value of the persistent attribute {@link #someStringUpperCase}.
	 * @author cope instrumentor
	 *
 */public final String getSomeStringUpperCase()
	{
		return (String)getAttribute(this.someStringUpperCase);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #someNotNullString}.
	 * @author cope instrumentor
	 *
 */public final String getSomeNotNullString()
	{
		return (String)getAttribute(this.someNotNullString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullString}.
	 * @author cope instrumentor
	 *
 */public final void setSomeNotNullString(final String someNotNullString)
			throws
				com.exedio.cope.lib.NotNullViolationException
	{
		try
		{
			setAttribute(this.someNotNullString,someNotNullString);
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
	 * @author cope instrumentor
	 *
 */public final Integer getSomeInteger()
	{
		return (Integer)getAttribute(this.someInteger);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someInteger}.
	 * @author cope instrumentor
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
	 * @author cope instrumentor
	 *
 */public final int getSomeNotNullInteger()
	{
		return ((Integer)getAttribute(this.someNotNullInteger)).intValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullInteger}.
	 * @author cope instrumentor
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
	 * @author cope instrumentor
	 *
 */public final Boolean getSomeBoolean()
	{
		return (Boolean)getAttribute(this.someBoolean);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someBoolean}.
	 * @author cope instrumentor
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
	 * @author cope instrumentor
	 *
 */public final boolean getSomeNotNullBoolean()
	{
		return ((Boolean)getAttribute(this.someNotNullBoolean)).booleanValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullBoolean}.
	 * @author cope instrumentor
	 *
 */public final void setSomeNotNullBoolean(final boolean someNotNullBoolean)
	{
		try
		{
			setAttribute(this.someNotNullBoolean,(someNotNullBoolean?Boolean.TRUE:Boolean.FALSE));
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
	 * @author cope instrumentor
	 *
 */public final ItemWithoutAttributes getSomeItem()
	{
		return (ItemWithoutAttributes)getAttribute(this.someItem);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someItem}.
	 * @author cope instrumentor
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
	 * Returns the value of the persistent attribute {@link #someNotNullItem}.
	 * @author cope instrumentor
	 *
 */public final ItemWithoutAttributes getSomeNotNullItem()
	{
		return (ItemWithoutAttributes)getAttribute(this.someNotNullItem);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullItem}.
	 * @author cope instrumentor
	 *
 */public final void setSomeNotNullItem(final ItemWithoutAttributes someNotNullItem)
			throws
				com.exedio.cope.lib.NotNullViolationException
	{
		try
		{
			setAttribute(this.someNotNullItem,someNotNullItem);
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
	 * Returns the value of the persistent attribute {@link #someEnumeration}.
	 * @author cope instrumentor
	 *
 */public final SomeEnumeration getSomeEnumeration()
	{
		return (SomeEnumeration)getAttribute(this.someEnumeration);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someEnumeration}.
	 * @author cope instrumentor
	 *
 */public final void setSomeEnumeration(final SomeEnumeration someEnumeration)
	{
		try
		{
			setAttribute(this.someEnumeration,someEnumeration);
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
	 * Returns the value of the persistent attribute {@link #someNotNullEnumeration}.
	 * @author cope instrumentor
	 *
 */public final SomeEnumeration getSomeNotNullEnumeration()
	{
		return (SomeEnumeration)getAttribute(this.someNotNullEnumeration);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someNotNullEnumeration}.
	 * @author cope instrumentor
	 *
 */public final void setSomeNotNullEnumeration(final SomeEnumeration someNotNullEnumeration)
			throws
				com.exedio.cope.lib.NotNullViolationException
	{
		try
		{
			setAttribute(this.someNotNullEnumeration,someNotNullEnumeration);
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
	 * @author cope instrumentor
	 *
 */public final java.lang.String getSomeMediaURL()
	{
		return getMediaURL(this.someMedia,null);
	}/**

	 **
	 * Returns a URL pointing to the varied data of the persistent attribute {@link #someMedia}.
	 * @author cope instrumentor
	 *
 */public final java.lang.String getSomeMediaURLSomeVariant()
	{
		return getMediaURL(this.someMedia,"SomeVariant");
	}/**

	 **
	 * Returns the major mime type of the persistent media attribute {@link #someMedia}.
	 * @author cope instrumentor
	 *
 */public final java.lang.String getSomeMediaMimeMajor()
	{
		return getMediaMimeMajor(this.someMedia);
	}/**

	 **
	 * Returns the minor mime type of the persistent media attribute {@link #someMedia}.
	 * @author cope instrumentor
	 *
 */public final java.lang.String getSomeMediaMimeMinor()
	{
		return getMediaMimeMinor(this.someMedia);
	}/**

	 **
	 * Returns a stream for fetching the data of the persistent media attribute {@link #someMedia}.
	 * @author cope instrumentor
	 *
 */public final java.io.InputStream getSomeMediaData()
	{
		return getMediaData(this.someMedia);
	}/**

	 **
	 * Provides data for the persistent media attribute {@link #someMedia}.
	 * @author cope instrumentor
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
	 * @author cope instrumentor
	 *
 */public final String getSomeQualifiedString(final ItemWithoutAttributes itemWithoutAttributes)
	{
		return (String)getAttribute(this.someQualifiedString,new Object[]{itemWithoutAttributes});
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #someQualifiedString}.
	 * @author cope instrumentor
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
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithManyAttributes.class,
			new com.exedio.cope.lib.Attribute[]{
				someString.initialize(false,false),
				someStringUpperCase.initialize(false,false),
				someNotNullString.initialize(false,true),
				someInteger.initialize(false,false),
				someNotNullInteger.initialize(false,true),
				someBoolean.initialize(false,false),
				someNotNullBoolean.initialize(false,true),
				someItem.initialize(false,false),
				someNotNullItem.initialize(false,true),
				someEnumeration.initialize(false,false),
				someNotNullEnumeration.initialize(false,true),
				someMedia.initialize(false,false),
				someQualifiedString.initialize(false,false),
			},
			null
		)
;}
