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

package com.exedio.cope;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

final class DatabaseLogConfig
{
	final int threshold;
	final String sql;
	private final PrintStream out;
	
	DatabaseLogConfig(final int threshold, final String sql, final PrintStream out)
	{
		if(threshold<0)
			throw new IllegalArgumentException("threshold must not be negative, but was " + threshold);
		if(out==null)
			throw new NullPointerException("out");
		
		this.threshold = threshold;
		this.sql = sql;
		this.out = out;
	}

	void log(final Statement statement, final long... times)
	{
		if((times[times.length-1]-times[0])>=threshold &&
			(sql==null || statement.text.indexOf(sql)>=0))
		{
			final StringBuilder bf = new StringBuilder(
					new SimpleDateFormat("yyyy/dd/MM HH:mm:ss.SSS").format(new Date(times[0])));
			
			for(int i = 1; i<times.length; i++)
			{
				bf.append('|');
				bf.append(times[i]-times[i-1]);
			}
			
			bf.append('|');
			bf.append(statement.text.toString());
			
			final ArrayList<Object> parameters = statement.parameters;
			if(parameters!=null)
			{
				bf.append('|');
				bf.append(parameters);
			}
			
			out.println(bf.toString());
		}
	}
}
