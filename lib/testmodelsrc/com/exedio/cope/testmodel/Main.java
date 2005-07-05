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
package com.exedio.cope.testmodel;

import com.exedio.cope.Model;
import com.exedio.cope.Properties;
import com.exedio.cope.Type;

public class Main
{
	public static final Type[] modelTypes = new Type[]
	{
		ItemWithSingleUnique.TYPE,
		ItemWithSingleUniqueReadOnly.TYPE,
		ItemWithSingleUniqueNotNull.TYPE,
		ItemWithDoubleUnique.TYPE,
		EmptyItem.TYPE,
		EmptyItem2.TYPE,
		AttributeItem.TYPE,
		AttributeEmptyItem.TYPE,
		StringItem.TYPE,
		HashItem.TYPE,
		DataItem.TYPE,
		SumItem.TYPE,
		VectorItem.TYPE,
		QualifiedItem.TYPE,
		QualifiedEmptyQualifier.TYPE,
		QualifiedStringQualifier.TYPE,
		QualifiedIntegerEnumQualifier.TYPE,
		PointerItem2.TYPE,
		PointerItem.TYPE,
		FirstSub.TYPE,
		SecondSub.TYPE,
		Super.TYPE, // deliberately put this type below it's sub types to test correct functionality
		CollisionItem1.TYPE,
		CollisionItem2.TYPE,
	};

	public static final Model model = new Model(modelTypes);

	public static void main(String[] args)
	{
		Main.model.setPropertiesInitially(new Properties());
		Main.model.tearDownDatabase();
	}

}
