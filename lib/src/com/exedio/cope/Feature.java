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
package com.exedio.cope;


public abstract class Feature
{
	private Type type;
	private String name;

	/**
	 * Is called in the constructor of the containing type.
	 */
	void initialize(final Type type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		if(this.type!=null)
			throw new RuntimeException();
		if(this.name!=null)
			throw new RuntimeException();

		this.type = type;
		this.name = name.intern();
	}
	
	public Type getType()
	{
		if(this.type==null)
			throw new RuntimeException();

		return type;
	}
	
	public Type getTypeIfInitialized()
	{
		return type;
	}
	
	public final String getName()
	{
		if(this.type==null)
			throw new RuntimeException();

		return name;
	}
	
	public String toString()
	{
		return type!=null ? (type.getID()+'#'+name) : super.toString();
	}
	
}
