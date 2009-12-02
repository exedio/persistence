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
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.TestAnnotation;
import com.exedio.cope.Type;
import com.exedio.cope.ItemField.DeletePolicy;

class PatternTestPattern extends Pattern
{
	final StringField ownString = new StringField();
	@TestAnnotation("ownIntAnn")
	final IntegerField ownInt = new IntegerField();
	@TestAnnotation("ownItemAnn")
	private ItemField<PatternItem> ownItem;
	
	private Type<PatternItem> superType = null;
	static final String SUPER_TYPE_POSTFIX = "UperType";

	final StringField superTypeString = new StringField();
	static final String SUPER_TYPE_STRING = "string";
	
	final BooleanField superTypeBoolean = new BooleanField();
	static final String SUPER_TYPE_BOOLEAN = "boolean";
	
	private Type<PatternItem> subType = null;
	static final String SUBTYPE_POSTFIX = "SubType";
	
	final IntegerField subTypeInteger = new IntegerField();
	static final String SUBTYPE_INTEGER= "integer";
	
	PatternTestPattern()
	{
		addSource(ownString, "OwnString", annotationField("ownString"));
		addSource(ownInt, "OwnInt", annotationField("ownInt"));
	}
	
	@Override
	protected void initialize()
	{
		super.initialize();
		
		//Create the super type.
		LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>(); 
		features.put(SUPER_TYPE_STRING, this.superTypeString);
		features.put(SUPER_TYPE_BOOLEAN, this.superTypeBoolean);
		this.superType = newSourceType(PatternItem.class, true, null, features, SUPER_TYPE_POSTFIX);
		
		//Create sub type
		features = new LinkedHashMap<String, Feature>();
		features.put(SUBTYPE_INTEGER, subTypeInteger);
		this.subType = newSourceType(PatternItem.class, false, superType, features, SUBTYPE_POSTFIX);
		
		addSource(ownItem = subType.newItemField(DeletePolicy.NULLIFY), "OwnItem", annotationField("ownItem"));
	}
	
	ItemField<PatternItem> getOwnItem()
	{
		return ownItem;
	}
	
	public Type<? extends Item> getSuperType()
	{
		return superType;
	}

	public Type<? extends Item> getSubType()
	{
		return subType;
	}
}
