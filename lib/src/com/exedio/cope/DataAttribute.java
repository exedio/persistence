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


public final class DataAttribute extends Attribute
{
	/**
	 * @see Item#dataAttribute(Option)
	 */
	DataAttribute(final Option option)
	{
		super(option);

		if(option.unique)
			throw new RuntimeException("DataAttribute cannot be unique");
		if(option.mandatory)
			throw new RuntimeException("DataAttribute cannot be mandatory");
		if(option.readOnly)
			throw new RuntimeException("DataAttribute cannot be read-only");
	}
	
	String filePath = null;
	
	void initialize(final Type type, final String name)
	{
		super.initialize(type, name);

		filePath = type.getID() + '/' + name + '/';
	}

	// second initialization phase ---------------------------------------------------

	protected Column createColumn(final Table table, final String name, final boolean notNull)
	{
		// make sure, data configuration properties are set
		getType().getModel().getProperties().getDatadirPath();
		// TODO on some fine day, the BLOB column will be created here
		return null;
	}
	
}
