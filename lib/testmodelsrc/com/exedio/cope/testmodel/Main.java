package com.exedio.cope.testmodel;

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Type;

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
		MediaItem.TYPE,
		SumItem.TYPE,
		QualifiedItem.TYPE,
		QualifiedEmptyQualifier.TYPE,
		PointerItem2.TYPE,
		PointerItem.TYPE,
		Super.TYPE,
		FirstSub.TYPE,
		SecondSub.TYPE,
		CollisionItem1.TYPE,
		CollisionItem2.TYPE,
	};

	public static final Model model = new Model(modelTypes);

}
