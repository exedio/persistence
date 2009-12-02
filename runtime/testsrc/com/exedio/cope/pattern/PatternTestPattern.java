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

import com.exedio.cope.BooleanField;
import com.exedio.cope.Features;
import com.exedio.cope.IntegerField;
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
	private ItemField<PatternTestTypeItem> ownItem;
	
	private Type<PatternTestTypeItem> superType = null;
	static final String SUPER_TYPE_POSTFIX = "UperType";

	@TestAnnotation("superTypeStringAnn")
	final StringField superTypeString = new StringField();
	static final String SUPER_TYPE_STRING = "string";
	
	final BooleanField superTypeBoolean = new BooleanField();
	static final String SUPER_TYPE_BOOLEAN = "boolean";
	
	private Type<PatternTestTypeItem> subType = null;
	static final String SUBTYPE_POSTFIX = "SubType";
	
	@TestAnnotation("subTypeIntegerAnn")
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
		final Features features = new Features(); 
		features.put(SUPER_TYPE_STRING, this.superTypeString, annotationField("superTypeString"));
		features.put(SUPER_TYPE_BOOLEAN, this.superTypeBoolean, annotationField("superTypeBoolean"));
		this.superType = newSourceType(PatternTestTypeItem.class, true, null, features, SUPER_TYPE_POSTFIX);
		
		//Create sub type
		features.clear();
		features.put(SUBTYPE_INTEGER, subTypeInteger, annotationField("subTypeInteger"));
		this.subType = newSourceType(PatternTestTypeItem.class, false, superType, features, SUBTYPE_POSTFIX);
		
		addSource(ownItem = subType.newItemField(DeletePolicy.NULLIFY), "OwnItem", annotationField("ownItem"));
	}
	
	ItemField<PatternTestTypeItem> getOwnItem()
	{
		return ownItem;
	}
	
	public Type<PatternTestTypeItem> getSuperType()
	{
		return superType;
	}

	public Type<PatternTestTypeItem> getSubType()
	{
		return subType;
	}
}
