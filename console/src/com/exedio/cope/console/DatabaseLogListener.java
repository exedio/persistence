/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.exedio.cope.misc.DatabaseListener;

final class DatabaseLogListener implements DatabaseListener
{
	final int threshold;
	final String sql;
	private final PrintStream out;
	
	DatabaseLogListener(final int threshold, final String sql, final PrintStream out)
	{
		if(threshold<0)
			throw new IllegalArgumentException("threshold must not be negative, but was " + threshold);
		if(out==null)
			throw new NullPointerException("out");
		
		this.threshold = threshold;
		this.sql = sql;
		this.out = out;
	}
	
	public void onStatement(
			final String statement,
			final List<Object> parameters,
			final long durationPrepare,
			final long durationExecute,
			final long durationRead,
			final long durationClose)
	{
		if((durationPrepare+durationExecute+durationRead+durationClose)>=threshold &&
			(sql==null || statement.indexOf(sql)>=0))
		{
			final StringBuilder bf = new StringBuilder(
					new SimpleDateFormat("yyyy/dd/MM HH:mm:ss.SSS").format(new Date()));
			
			bf.append('|');
			bf.append(durationPrepare);
			bf.append('|');
			bf.append(durationExecute);
			bf.append('|');
			bf.append(durationRead);
			bf.append('|');
			bf.append(durationClose);
			bf.append('|');
			bf.append(statement);
			
			if(parameters!=null)
			{
				bf.append('|');
				bf.append(parameters);
			}
			
			out.println(bf.toString());
		}
	}
}
