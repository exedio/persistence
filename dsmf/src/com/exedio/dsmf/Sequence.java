/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.dsmf;

public final class Sequence extends Node
{
	final String name;
	private final int startWith;
	private final boolean required;
	private boolean exists;
	
	public Sequence(final Schema schema, final String name, final int startWith)
	{
		this(schema, name, startWith, true);
	}
	
	Sequence(final Schema schema, final String name, final int startWith, final boolean required)
	{
		super(schema.driver, schema.connectionProvider);
		
		if(schema==null)
			throw new RuntimeException();
		if(name==null)
			throw new RuntimeException();
		if(!schema.driver.supportsSequences())
			throw new RuntimeException("database does not support sequences");

		this.name = name;
		this.startWith = startWith;
		this.required = required;
		this.exists = !required;

		schema.register(this);
	}
	
	public String getName()
	{
		return name;
	}

	public int getStartWith()
	{
		return startWith;
	}

	final void notifyExists()
	{
		exists = true;
	}
	
	public boolean required()
	{
		return required;
	}
	
	public boolean exists()
	{
		return exists;
	}
		
	@Override
	void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		final String error;
		final Color particularColor;
		if(!exists)
		{
			error = "missing";
			particularColor = Color.ERROR;
		}
		else if(!required)
		{
			error = "not used";
			particularColor = Color.WARNING;
		}
		else
		{
			error = null;
			particularColor = Color.OK;
		}
				
		this.error = error;
		this.particularColor = particularColor;
		cumulativeColor = particularColor;
	}
	
	public void create()
	{
		create(null);
	}
	
	public void create(final StatementListener listener)
	{
		executeSQL(driver.createSequence(protectName(name), startWith), listener);
	}
	
	public void drop()
	{
		drop(null);
	}
	
	public void drop(final StatementListener listener)
	{
		executeSQL(driver.dropSequence(protectName(name)), listener);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
