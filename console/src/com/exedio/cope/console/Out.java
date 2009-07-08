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
import java.net.InetAddress;
import java.util.Date;

import com.exedio.cops.Cop;
import com.exedio.cops.Resource;
import com.exedio.cops.XMLEncoder;

final class Out
{
	private final PrintStream bf;
	
	Out(final PrintStream bf)
	{
		assert bf!=null;
		this.bf = bf;
	}
	
	void write(final Resource resource)
	{
		bf.print(resource); // TODO use getURL(HttpServletRequest)
	}
	
	void write(final Cop cop)
	{
		bf.print(XMLEncoder.encode(cop.toString())); // TODO use getURL(HttpServletRequest) and response.encodeURL
	}
	
	void write(final String s)
	{
		bf.print(s);
	}
	
	void write(final char c)
	{
		bf.print(c);
	}
	
	void write(final int i)
	{
		bf.print(i);
	}
	
	void write(final long i)
	{
		bf.print(i);
	}
	
	void write(final InetAddress s)
	{
		bf.print(s);
	}
	
	void write(final Date date)
	{
		bf.print(Format.format(date));
	}
	
	void printStackTrace(final Throwable t)
	{
		t.printStackTrace(bf);
	}
	
	void flush()
	{
		bf.flush();
	}
	
	void close()
	{
		bf.close();
	}
}
