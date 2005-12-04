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
import java.util.Iterator;

import com.exedio.cope.Feature;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.UniqueConstraint;


final class CopeUniqueConstraint extends CopeFeature
{
	final CopeAttribute[] attributes;
	
	/**
	 * For constraints covering more than one attribute.
	 */
	CopeUniqueConstraint(final JavaAttribute javaAttribute, final CopeAttribute[] attributes)
	{
		super(javaAttribute);
		this.attributes = attributes;
	}
	
	void show() // TODO remove
	{
		final ArrayList xAttributeNames = new ArrayList();
		for(int i = 0; i<attributes.length; i++)
			xAttributeNames.add(attributes[i].name);
		System.out.println("------uniqueconstraint:"+name+xAttributeNames);

		final Feature rtvalueF = getInstance();
		if(rtvalueF instanceof UniqueConstraint)
		{
			final UniqueConstraint rtvalue = (UniqueConstraint)rtvalueF;
			final ArrayList rtAttributeNames = new ArrayList();
			for(Iterator i = rtvalue.getUniqueAttributes().iterator(); i.hasNext(); )
			{
				final ObjectAttribute attribute = (ObjectAttribute)i.next();
				final JavaAttribute ja = (JavaAttribute)getParent().file.repository.getByRtValue(attribute);
				rtAttributeNames.add(ja.name);
			}
			System.out.println("------uniqueconstraint:"+name+rtAttributeNames);
		}
		else if(rtvalueF instanceof ObjectAttribute)
		{
			final ObjectAttribute rtvalue = (ObjectAttribute)rtvalueF;
			final JavaAttribute ja = (JavaAttribute)getParent().file.repository.getByRtValue(rtvalue);
			System.out.println("------uniqueconstraint:"+name+'/'+ja.name);
		}
		else
			throw new RuntimeException(rtvalueF.toString());
		
		System.out.println("------");
	}
	
	/**
	 * For constraints covering exactly one attribute.
	 */
	CopeUniqueConstraint(final CopeAttribute copeAttribute, final JavaAttribute javaAttribute)
	{
		super(javaAttribute);
		this.attributes = new CopeAttribute[]{copeAttribute};
	}
	
}
