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

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.util.XMLEncoder;
import com.exedio.cops.Cop;
import com.exedio.cops.Resource;
import com.exedio.dsmf.Constraint;

final class Out extends OutBasic
{
	private final HttpServletRequest request;
	
	Out(final HttpServletRequest request, final PrintStream bf)
	{
		super(bf);
		assert request!=null;
		this.request = request;
	}
	
	void writeRaw(final String s)
	{
		bf.print(s);
	}
	
	void writeRaw(final char c)
	{
		bf.print(c);
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
		if(date!=null)
			bf.print(Format.format(date));
	}
	
	void write(final Constraint.Type type)
	{
		switch(type)
		{
			case PrimaryKey: bf.append("pk"    ); break;
			case ForeignKey: bf.append("fk"    ); break;
			case Unique:     bf.append("unique"); break;
			case Check:      bf.append("check" ); break;
			default:
				throw new RuntimeException(type.name());
		}
	}
	
	void writeStackTrace(final Throwable t)
	{
		t.printStackTrace(bf);
	}
	
	void writeSQL(final String s)
	{
		bf.print(Format.highlightSQL(XMLEncoder.encode(s)));
	}
	
	void write(final Resource resource)
	{
		bf.print(resource.getURL(request));
	}
	
	void write(final Cop cop)
	{
		// here we don't have to call HttpServletResponse.encodeURL
		// since HttpSessions are not used at all
		bf.print(XMLEncoder.encode(cop.getURL(request)));
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
