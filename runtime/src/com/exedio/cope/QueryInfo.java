/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class QueryInfo
{
	private ArrayList<QueryInfo> childsLazilyInitialized;
	final String text;

	QueryInfo(final String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public Collection<QueryInfo> getChilds()
	{
		return
			childsLazilyInitialized==null
			? List.of()
			: Collections.unmodifiableList(childsLazilyInitialized);
	}

	void addChild(final QueryInfo newChild)
	{
		if(childsLazilyInitialized==null)
			childsLazilyInitialized = new ArrayList<>();

		childsLazilyInitialized.add(newChild);
	}

	public void print(final PrintStream o)
	{
		print(o, 0);
	}

	private void print(final PrintStream o, int level)
	{
		for(int i=0; i<level; i++)
			o.print("  ");
		o.println(text);
		level++;
		if(childsLazilyInitialized!=null)
			for(final QueryInfo s : childsLazilyInitialized)
				s.print(o, level);
	}

	@Override
	public String toString()
	{
		return text;
	}
}
