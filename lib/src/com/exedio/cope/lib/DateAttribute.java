
package com.exedio.cope.lib;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class DateAttribute extends ObjectAttribute
{
	final boolean forbidTimestampColumn;

	public DateAttribute(final Option option)
	{
		super(option);
		this.forbidTimestampColumn = false;
	}
	
	/**
	 * @param forbidTimestampColumn
	 * 		forces this date attribute to be implemented with an integer column
	 * 		holding the time value of this date,
	 * 		even if the database supports timestamp columns.
	 */
	public DateAttribute(final Option option, final boolean forbidTimestampColumn)
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
