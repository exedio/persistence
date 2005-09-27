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
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Cope;
import com.exedio.cope.Model;


final class VmCop extends AdminCop
{
	static final String ALL_PACKAGES = "ap";
	
	final boolean allPackages;

	VmCop(final boolean allPackages)
	{
		super("vm");
		this.allPackages = allPackages;
		
		addParameter(TAB, TAB_VM);
		if(allPackages)
			addParameter(ALL_PACKAGES, "t");
	}
	
	static final VmCop getVmCop(final HttpServletRequest request)
	{
		return new VmCop(request.getParameter(ALL_PACKAGES)!=null);
	}
	
	VmCop toToggleAllPackages()
	{
		return new VmCop(!allPackages);
	}
	
	private static final String replaceLineBreaks(final String s)
	{
		return (s==null) ? "n/a" : s.replaceAll("\n", "<br>");
	}

	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		Properties_Jspm.writeVm(out, this, allPackages ? Package.getPackages() : new Package[]{Cope.class.getPackage(), VmCop.class.getPackage()});
		
		final java.util.Properties current = model.getDatabaseInfo();
		for(Iterator i = current.keySet().iterator(); i.hasNext(); )
		{
			final String name = (String)i.next();
			current.setProperty(name, replaceLineBreaks(current.getProperty(name)));
		}

		Properties_Jspm.writeDatabaseInfo(out, current);
		
		final java.util.Properties p = new Properties();
		InputStream in = null;
		try
		{
			in = Cope.class.getResourceAsStream("testprotocol.properties");
			p.load(in);
		}
		finally
		{
			if(in!=null)
				in.close();
		}
		
		final TreeMap testedDatabases = new TreeMap();
		for(Iterator i = p.keySet().iterator(); i.hasNext(); )
		{
			final String name = (String)i.next();
			final String value = replaceLineBreaks(p.getProperty(name));
			
			final int nameDot = name.indexOf('.');
			if(nameDot<=0)
				throw new RuntimeException(name);
			
			final String databaseName = name.substring(0, nameDot);
			HashMap database = (HashMap)testedDatabases.get(databaseName);
			if(database==null)
			{
				database = new HashMap();
				database.put("name", databaseName);
				testedDatabases.put(databaseName, database);
			}
			
			final String key = name.substring(nameDot+1);
			if(key.startsWith("cope."))
			{
				final String copeValue = key.substring("cope.".length())+"="+value;
				final String previousValue = (String)database.get("cope.properties");
				database.put("cope.properties", (previousValue==null) ? copeValue : (previousValue + "<br>" + copeValue));
			}
			else
				database.put(key, value);
		}
		
		Properties_Jspm.writeTestInfo(out, current, (HashMap[])testedDatabases.values().toArray(new HashMap[0]));
	}
	
}
