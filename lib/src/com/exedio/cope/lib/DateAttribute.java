
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class DateAttribute extends ObjectAttribute
{
	final boolean forbidTimestampColumn;

	/**
	 * @see Item#dateAttribute(Option)
	 */
	DateAttribute(final Option option)
	{
		super(option);
		this.forbidTimestampColumn = false;
	}
	
	/**
	 * @see Item#dateAttribute(Option, boolean)
	 */
	DateAttribute(final Option option, final boolean forbidTimestampColumn)
	{
		super(option);
		this.forbidTimestampColumn = forbidTimestampColumn;
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		final boolean useLong =
			forbidTimestampColumn ||
			!(getType().getModel().getDatabase() instanceof DatabaseTimestampCapable);
		
		return
			Collections.singletonList(
				useLong
				? (Column)new IntegerColumn(table, name, notNull, 20, true, null)
				: (Column)new TimestampColumn(table, name, notNull)
			);
	}
	
	Object cacheToSurface(final Object cache)
	{
		return cache==null ? null : new Date(((Long)cache).longValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return surface==null ? null : new Long(((Date)surface).getTime());
	}
	
}
