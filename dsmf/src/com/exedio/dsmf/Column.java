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


public final class Column extends Node
{
	final Table table;
	final String name;
	private final String requiredType;
	private String existingType;
		
	public Column(final Table table, final String name, final String type)
	{
		this(table, name, type, true);
	}

	Column(final Table table, final String name, final String type, final boolean required)
	{
		super(table.dialect, table.connectionProvider);

		if(table==null)
			throw new RuntimeException(name);
		if(name==null)
			throw new RuntimeException(type);
		if(type==null)
			throw new RuntimeException(name);

		this.table = table;
		this.name = name;
		if(required)
		{
			this.requiredType = type;
			this.existingType = null;
		}
		else
		{
			this.requiredType = null;
			this.existingType = type;
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

	void notifyExists(final String existingType)
	{
		if(existingType==null)
			throw new RuntimeException(name);
		if(this.existingType!=null && !this.existingType.equals(existingType))
			throw new RuntimeException(name);

		this.existingType = existingType;
	}

	@Override
	void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		final String error;
		final Color particularColor;
		if(existingType==null)
		{
			error = "missing";
			particularColor = Color.ERROR;
		}
		else if(requiredType==null)
		{
			error = "not used";
			particularColor = Color.WARNING;
		}
		else
		{
			if(requiredType!=null &&
				existingType!=null &&
				!requiredType.equals(existingType))
			{
				error = "different type in database: >"+existingType+"<";
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
		return requiredType!=null;
	}
	
	public final boolean exists()
	{
		return existingType!=null;
	}
		
	public final String getType()
	{
		if(requiredType!=null)
			return requiredType;
		else
			return existingType;
	}
		
	public final void create()
	{
		create(null);
	}
	
	public final void create(final StatementListener listener)
	{
		//System.out.println("createColumn:"+bf);
		executeSQL(
			dialect.createColumn(
				protectName(table.name),
				protectName(name),
				getType()), listener);
	}

	public final void renameTo(final String newName)
	{
		renameTo(newName, null);
	}
	
	public final void renameTo(final String newName, final StatementListener listener)
	{
		//System.err.println("renameColumn:"+bf);
		executeSQL(
			dialect.renameColumn(
				protectName(table.name),
				protectName(name),
				protectName(newName),
				existingType), listener);
	}
	
	public final void modify(final String newType)
	{
		modify(newType, null);
	}
	
	public final void modify(final String newType, final StatementListener listener)
	{
		executeSQL(
			dialect.modifyColumn(
				protectName(table.name),
				protectName(name),
				newType), listener);
	}

	public final void drop()
	{
		drop(null);
	}
	
	public final void drop(final StatementListener listener)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(protectName(table.name)).
			append(" drop column ").
			append(protectName(name));

		//System.out.println("dropColumn:"+bf);
		executeSQL(bf.toString(), listener);
	}
	
	@Override
	public final String toString()
	{
		return name;
	}
	
}
	
