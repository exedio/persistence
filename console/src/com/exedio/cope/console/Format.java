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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

final class Format
{
	private Format()
	{
		// prevent instantiation
	}
	
	private static SimpleDateFormat fullDateFormat, todayDateFormat;
	private static DecimalFormat numberFormat;
	
	static
	{
		fullDateFormat = new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss'<small>'.SSS'</small>'");
		todayDateFormat = new SimpleDateFormat("HH:mm:ss'<small>'.SSS'</small>'");
		final DecimalFormatSymbols nfs = new DecimalFormatSymbols();
		nfs.setDecimalSeparator(',');
		nfs.setGroupingSeparator('\'');
		numberFormat = new DecimalFormat("", nfs);
	}
	
	private static final long todayInterval = 6 * 60 * 60 * 1000; // 6 hours
	
	static final String formatAndHide(final Date date)
	{
		return date!=null ? format(date) : "";
	}
	
	static final String format(final Date date)
	{
		final long dateMillis = date.getTime();
		final long now = System.currentTimeMillis();
		return (
			( (now-todayInterval) < dateMillis && dateMillis < (now+todayInterval) )
			? todayDateFormat : fullDateFormat).format(date);
	}
	
	static final String format(final long number)
	{
		if(number==Integer.MIN_VALUE)
			return "min32";
		if(number==Integer.MAX_VALUE)
			return "max32";
		if(number==Long.MIN_VALUE)
			return "min64";
		if(number==Long.MAX_VALUE)
			return "max64";
		
		return /*"fm" +*/ numberFormat.format(number);
	}
	
	static final String formatAndHide(final long hidden, final long number)
	{
		return /*("["+hidden+']') +*/ (number!=hidden ? format(number) : "");
	}
	
	private static final DecimalFormat RATIO_FORMAT = new DecimalFormat("###0.00");
	
	static String ratio(final long dividend, final long divisor)
	{
		if(dividend<0 || divisor<0)
			return "<0";
		if(divisor==0)
			return "";
		
		return RATIO_FORMAT.format(Math.log10(((double)dividend) / ((double)divisor)));
	}
	
	private static final String hs(String s, final String... keywords)
	{
		for(final String keyword : keywords)
			s = s.replaceAll("\\b(" + keyword +")\\b", "<b>" + keyword +"</b>");
		return s;
	}
	
	static String highlightSQL(final String s)
	{
		return hs(s,
				"alter", "create", "drop", "add",
				"table", "column",
				"select", "from", "where",
				"insert", "into", "values", "update", "set");
	}
}
