/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.copernica.admin;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cops.Cop;

abstract class AdminCop extends Cop
{
	final String name;

	protected AdminCop(final String name)
	{
		this.name = name;
	}
	
	final AdminCop[] getTabs()
	{
		return new AdminCop[]{
				new PropertiesCop(),
				new SchemaCop(null, false, false),
				new ConnectionStatsCop(),
				new MediaStatsCop(),
				new VmCop(false),
			};
	}
	
	void writeHead(HttpServletRequest request, PrintStream out) throws IOException
	{
		// default implementation does nothing
	}
	
	abstract void writeBody(PrintStream out, Model model, HttpServletRequest request) throws IOException;
	
	static final String TAB = "t";
	static final String TAB_CONNECTION_STATS = "cp";
	static final String TAB_MEDIA_STATS = "m";
	static final String TAB_VM = "vm";
	
	static final AdminCop getCop(final HttpServletRequest request)
	{
		final String tab = request.getParameter(TAB);
		if(TAB_CONNECTION_STATS.equals(tab))
			return new ConnectionStatsCop();
		if(TAB_MEDIA_STATS.equals(tab))
			return new MediaStatsCop();
		if(TAB_VM.equals(tab))
			return VmCop.getVmCop(request);

		final String schemaID = request.getParameter(SchemaCop.SCHEMA);
		if(schemaID!=null)
			return SchemaCop.getCop(schemaID, request);
		
		return new PropertiesCop();
	}

}
