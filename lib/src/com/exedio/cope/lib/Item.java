
package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Item extends Search
{

	/**
	 * Must never be public, since it does not throw exceptions for constraint violations.
	 * Subclasses (usually generated) must care about throwing these exception by calling
	 * {@link #throwInitialNotNullViolationException} and/or 
	 * {@link #throwInitialUniqueViolationException}.
	 * All this fiddling is needed, because one cannot wrap a <code>super()</code> call into a
	 * try-catch statement.
	 */
	protected Item(final AttributeValue[] initialAttributesValues)
	{
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
		final AttributeMapping mapping = attribute.mapping;;
		if(mapping!=null)
			return mapping.mapJava(getAttribute(mapping.sourceAttribute));

		return null;
	}
	
	protected final Object getAttribute(final Attribute attribute, final Object[] qualifiers)
	{
		final AttributeMapping mapping = attribute.mapping;;
		if(mapping!=null)
			return mapping.mapJava(getAttribute(mapping.sourceAttribute));

		return null;
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
	}
	
	protected final void setAttribute(final Attribute attribute, final Object[] qualifiers, final Object value)
	throws UniqueViolationException
	{
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

}
