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

package com.exedio.cope.pattern;

import java.util.LinkedHashMap;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;

class PatternTestPattern extends Pattern
{
	private Type<PatternItem> abstractType = null;
	static final String ABSTRACTTYPE_POSTFIX = "AbstractType";

	final StringField superTypeString = new StringField();
	static final String ABSTRACTTYPE_STRING = "string";
	
	final BooleanField superTypeBoolean = new BooleanField();
	static final String ABSTRACTTYPE_BOOLEAN = "boolean";
	
	private Type<? extends Item> subType = null;
	static final String SUBTYPE_POSTFIX = "SubType";
	
	final IntegerField subTypeInteger = new IntegerField();
	static final String SUBTYPE_INTEGER= "integer";
	
	@Override
	protected void initialize()
	{
		super.initialize();
		
		//Create the super type.
		LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>(); 
		features.put(ABSTRACTTYPE_STRING, this.superTypeString);
		features.put(ABSTRACTTYPE_BOOLEAN, this.superTypeBoolean);
		this.abstractType = newSourceType(PatternItem.class, true, null, features, ABSTRACTTYPE_POSTFIX);
		
		//Create sub type
		features = new LinkedHashMap<String, Feature>();
		features.put(SUBTYPE_INTEGER, subTypeInteger);
		this.subType = newSourceType(PatternItem.class, false, abstractType, features, SUBTYPE_POSTFIX);
	}
	
	public Type<? extends Item> getAbstractType()
	{
		return abstractType;
	}

	public Type<? extends Item> getSubType()
	{
		return subType;
	}
}
