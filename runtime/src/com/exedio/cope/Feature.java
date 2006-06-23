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

package com.exedio.cope;


public abstract class Feature
{
	private Type<? extends Item> type;
	private String name;
	private String id;
	
	final static char ID_SEPARATOR = '.';

	/**
	 * Is called in the constructor of the containing type.
	 */
	void initialize(final Type<? extends Item> type, final String name)
	{
		if(type==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();

		assert this.type==null;
		assert this.name==null;
		assert this.id==null;

		this.type = type;
		this.name = name.intern();
		this.id =   (type.id + ID_SEPARATOR + name).intern();
		
		type.registerInitialization(this);
	}
	
	public boolean isInitialized()
	{
		return type!=null;
	}
	
	public Type<? extends Item> getType()
	{
		if(this.type==null)
			throw new FeatureNotInitializedException();

		return type;
	}
	
	public final String getName()
	{
		if(this.type==null)
			throw new FeatureNotInitializedException();
		assert name!=null;

		return name;
	}
	
	/**
	 * @see Model#findFeatureByID(String)
	 */
	public final String getID()
	{
		if(this.type==null)
			throw new FeatureNotInitializedException();
		assert id!=null;

		return id;
	}
	
	String toStringNonInitialized()
	{
		return super.toString();
	}
	
	@Override
	public final String toString() // TODO SOON use id
	{
		return type!=null ? (type.id+'#'+name) : toStringNonInitialized();
	}
	
}
