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
package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class JavaRepository
{
	private final ArrayList files = new ArrayList();
	private final HashMap copeClasses = new HashMap();
	private HashMap rtvalues = new HashMap();

	void add(final JavaFile file)
	{
		files.add(file);
	}
	
	final List getFiles()
	{
		return files;
	}
	
	void add(final CopeClass copeClass)
	{
		final String name = JavaFile.extractClassName(copeClass.javaClass.name);
		if(copeClasses.put(name, copeClass)!=null)
			throw new RuntimeException(name);
		//System.out.println("--------- put cope class: "+name);
	}
	
	CopeClass getCopeClass(final String className)
	{
		final CopeClass result = (CopeClass)copeClasses.get(className);
		if(result==null)
			throw new RuntimeException("no cope class for "+className);
		return result;
	}

	void putRtValue(final JavaAttribute attribute, final Object rtvalue)
	{
		rtvalues.put(rtvalue, attribute);
	}
	
	final JavaAttribute getByRtValue(final Object rtvalue)
	{
		return (JavaAttribute)rtvalues.get(rtvalue);
	}
	
}
