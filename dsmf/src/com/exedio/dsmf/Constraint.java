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

package com.exedio.dsmf;

public abstract class Constraint extends Node
{
	public static final int MASK_PK = 1;
	public static final int MASK_FK = 2;
	public static final int MASK_UNIQUE = 4;
	public static final int MASK_CHECK = 8;
	public static final int MASK_ALL = MASK_PK | MASK_FK | MASK_UNIQUE | MASK_CHECK;
	
	final Table table;
	final String name;
	final int mask;
	final boolean secondPhase;
	private final boolean required;
	final String requiredCondition;
	private boolean exists = false;
	private String existingCondition;
		
	Constraint(final Table table, final String name, final int mask, final boolean secondPhase, final boolean required, final String condition)
	{
		super(table.driver, table.connectionProvider);
		
		if(table==null)
			throw new RuntimeException(name);
		if(name==null)
			throw new RuntimeException(table.name);
		switch(mask)
		{
			case MASK_PK:
			case MASK_FK:
			case MASK_UNIQUE:
			case MASK_CHECK:
				break;
			default:
				throw new RuntimeException(String.valueOf(mask));
		}

		this.table = table;
		this.name = name;
		this.mask = mask;
		this.secondPhase = secondPhase;
		this.required = required;
		if(required)
			this.requiredCondition = condition;
		else
		{
			this.requiredCondition = null;
			this.existingCondition = condition;
			this.exists = true;
		}
		table.register(this);
	}
	
	public final Table getTable()
	{
		return table;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public boolean matchesMask(final int mask)
	{
		return (this.mask & mask) > 0;
	}
	
	public final String getRequiredCondition()
	{
		return requiredCondition;
	}
	
	final void notifyExists()
	{
		exists = true;
	}

	final void notifyExistsCondition(final String condition)
	{
		assert !exists;
		assert existingCondition==null;
		
		exists = true;
		this.existingCondition = condition;
	}

	@Override
	final void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		// TODO: make this dependend on type of constraint:
		// check/not null constraint are yellow only if missing
		// foreign key/unique constraint are red when missing or unused
		final String error;
		final Color particularColor;
		if(!exists)
		{
			if(isSupported())
			{
				error = "missing";
				particularColor = Color.ERROR;
			}
			else
			{
				error = "not supported";
				particularColor = Color.OK;
			}
		}
		else if(!required)
		{
			error = "not used";
			if(!table.required())
				particularColor = Color.WARNING;
			else
				particularColor = Color.ERROR;
		}
		else
		{
			if(requiredCondition!=null && existingCondition!=null &&
				!requiredCondition.equals(existingCondition))
			{
				error = "different condition in database: expected ---" + requiredCondition + "---, but was ---" + existingCondition + "---";
				particularColor = Color.ERROR;
			}
			else if(requiredCondition==null && existingCondition!=null)
			{
				error = "surplus condition in database: ---" + existingCondition + "---";
				particularColor = Color.ERROR;
			}
			else if(requiredCondition!=null && existingCondition==null)
			{
				error = "missing condition in database: ---" + requiredCondition + "---";
				particularColor = Color.ERROR;
			}
			else
			{
				error = null;
				particularColor = Color.OK;
			}
		}

		this.error = error;
		this.particularColor = particularColor;
		cumulativeColor = particularColor;
	}

	public final boolean required()
	{
		return required;
	}

	public final boolean exists()
	{
		return exists;
	}
	
	@Override
	public final String toString()
	{
		return name;
	}
	
	public boolean isSupported()
	{
		return true;
	}
	
	public int check()
	{
		throw new RuntimeException("no yet implemented"); // TODO
	}
	
	public final void create()
	{
		create(null);
	}
	
	public final void drop()
	{
		drop(null);
	}
	
	abstract void createInTable(StringBuffer bf);
	public abstract void create(StatementListener listener);
	public abstract void drop(StatementListener listener);

}
