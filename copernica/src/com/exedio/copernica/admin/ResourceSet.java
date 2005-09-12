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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


final class ResourceSet
{
	private final ArrayList resourceList = new ArrayList();
	private final HashMap resources = new HashMap();
	
	void add(final Resource r)
	{
		resourceList.add(r);
	}
	
	final void init()
	{
		for(Iterator i = resourceList.iterator(); i.hasNext(); )
		{
			final Resource resource = (Resource)i.next();
			resource.init();
			resources.put('/'+resource.name, resource);
		}
	}
	
	protected final boolean doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		final String pathInfo = request.getPathInfo();
		if(pathInfo==null)
			return false;
		
		final Resource resource = (Resource)resources.get(pathInfo);
		if(resource==null)
			return false;
		
		resource.doGet(request, response);
		return true;
	}
}
