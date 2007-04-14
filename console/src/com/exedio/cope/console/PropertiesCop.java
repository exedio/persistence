/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.Model;
import com.exedio.cope.Properties;


final class PropertiesCop extends ConsoleCop
{
	PropertiesCop()
	{
		super("properties");
	}
	
	@Override
	void writeHead(final HttpServletRequest request, final PrintStream out)
	{
		Properties_Jspm.writeHead(out);
	}
	
	@Override
	final void writeBody(final PrintStream out, final Model model, final HttpServletRequest request)
	{
		final Properties props = model.getProperties();
		final String source = props.getSource();
		String sourceContent = null;
		try
		{
			final File f = new File(source);
			final FileReader r = new FileReader(f);
			final StringBuffer bf = new StringBuffer();

			final char[] b = new char[20*1024];
			for(int len = r.read(b); len>=0; len = r.read(b))
				bf.append(b, 0, len);

			sourceContent = bf.toString();
			for(final Properties.Field field : props.getFields())
			{
				if(field.hasHiddenValue())
				{
					final String key = field.getKey();
					sourceContent = sourceContent.replaceAll(key+".*", key+"=<i>hidden</i>");
				}
			}
		}
		catch(FileNotFoundException e)
		{
			// sourceContent is still null
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		
		Properties_Jspm.write(out, request, this, props, model.getInitializeDate(), model.getConnectDate(), sourceContent);
	}
	
	String formatObject(final Object o)
	{
		if(o==null)
			return null;
		else if(o instanceof Integer)
			return format(((Integer)o).longValue());
		else if(o instanceof Long)
			return format(((Long)o).longValue());
		else
			return o.toString();
	}
}
