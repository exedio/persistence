/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.ItemField.DeletePolicy.NULLIFY;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Features;
import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.TestAnnotation;
import com.exedio.cope.Type;
import java.lang.reflect.AnnotatedElement;

class PatternTestPattern extends Pattern
{
	private static final long serialVersionUID = 1l;

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
		assertSame(ownString, addSourceFeature(ownString, "ownString", af("ownString")));
		assertSame(ownInt,    addSourceFeature(ownInt, "ownInt", af("ownInt")));
	}

	@Override
	protected void onMount()
	{
		super.onMount();

		//Create the super type.
		final Features features = new Features();
		features.put(SUPER_TYPE_STRING, superTypeString, af("superTypeString"));
		features.put(SUPER_TYPE_BOOLEAN, superTypeBoolean, af("superTypeBoolean"));
		this.superType = newSourceType(PatternTestTypeItem.class, true, null, features, SUPER_TYPE_POSTFIX);

		//Create sub type
		features.clear();
		features.put(SUBTYPE_INTEGER, subTypeInteger, af("subTypeInteger"));
		this.subType = newSourceType(PatternTestTypeItem.class, false, superType, features, SUBTYPE_POSTFIX);

		ownItem = subType.newItemField(NULLIFY);
		assertSame(ownItem, addSourceFeature(ownItem, "ownItem", af("ownItem")));
	}

	ItemField<PatternTestTypeItem> getOwnItem()
	{
		return ownItem;
	}

	public Type<PatternTestTypeItem> getPatternSuperType()
	{
		return superType;
	}

	public Type<PatternTestTypeItem> getPatternSubType()
	{
		return subType;
	}

	private AnnotatedElement af(final String name)
	{
		try
		{
			return getClass().getDeclaredField(name);
		}
		catch(final NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
}
