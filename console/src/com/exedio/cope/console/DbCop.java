/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


final class DbCop extends AdminCop
{

	DbCop()
	{
		super("db");
		addParameter(TAB, TAB_DB);
	}
	
	private static final String replaceLineBreaks(final String s)
	{
		return (s==null) ? "n/a" : s.replaceAll("\n", "<br>");
	}

	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request) throws IOException
	{
		final java.util.Properties current = model.getDatabaseInfo();
		for(Iterator i = current.keySet().iterator(); i.hasNext(); )
		{
			final String name = (String)i.next();
			current.setProperty(name, replaceLineBreaks(current.getProperty(name)));
		}

		Properties_Jspm.writeDatabaseInfo(out, current);
		Properties_Jspm.writeTestInfo(out, current, makeTestedDatabases());
	}
	
	private final HashMap[] makeTestedDatabases()
	{
		final Properties p = new Properties();
		InputStream in = null;
		try
		{
			in = Cope.class.getResourceAsStream("testprotocol.properties");
			if(in==null)
				return null;
			
			p.load(in);
			in.close();
			in = null;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(in!=null)
			{
				try
				{
					in.close();
				}
				catch(IOException e)
				{
					// oops
				}
			}
		}
		
		final TreeMap<String, HashMap<String, Object>> testedDatabases = new TreeMap<String, HashMap<String, Object>>();
		for(Iterator i = p.keySet().iterator(); i.hasNext(); )
		{
			final String name = (String)i.next();
			final String value = replaceLineBreaks(p.getProperty(name));
			
			final int nameDot = name.indexOf('.');
			if(nameDot<=0)
				throw new RuntimeException(name);
			
			final String databaseName = name.substring(0, nameDot);
			HashMap<String, Object> database = testedDatabases.get(databaseName);
			if(database==null)
			{
				database = new HashMap<String, Object>();
				database.put("name", databaseName);
				testedDatabases.put(databaseName, database);
			}
			
			final String key = name.substring(nameDot+1);
			if(key.startsWith("cope."))
			{
				TreeMap<String, String> previousValue = castTreeMap(database.get("cope.properties"));
				if(previousValue==null)
				{
					previousValue = new TreeMap<String, String>();
					database.put("cope.properties", previousValue);
				}
				previousValue.put(key.substring("cope.".length()), value);
			}
			else
				database.put(key, value);
		}
		
		return testedDatabases.values().toArray(new HashMap[0]);
	}
	
	@SuppressWarnings("unchecked") // TODO testedDatabases contains Strings and Maps
	private static final TreeMap<String, String> castTreeMap(final Object o)
	{
		return (TreeMap<String, String>)o;
	}
	
	
}
