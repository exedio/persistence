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
import java.util.ArrayList;
import java.util.Iterator;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.HttpEntity;


final class HttpEntityStatsCop extends AdminCop
{
	static final String STATISTICS = "httpentitystats";

	HttpEntityStatsCop()
	{
		super("http entity stats");
		addParameter(STATISTICS, "true");
	}

	final void writeBody(final PrintStream out, final Model model) throws IOException
	{
		final ArrayList entities = new ArrayList();

		for(Iterator i = model.getTypes().iterator(); i.hasNext(); )
		{
			final Type type = (Type)i.next();
			for(Iterator j = type.getDeclaredFeatures().iterator(); j.hasNext(); )
			{
				final Feature feature = (Feature)j.next();
				if(feature instanceof HttpEntity)
				{
					entities.add(feature);
				}
			}
		}

		Admin_Jspm.writeHttpEntityStats(out, entities, model.getProperties().getDatadirUrl());
	}
	
}
