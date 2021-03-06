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

package com.exedio.cope;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;


public abstract class FieldTest extends TestmodelTest
{

	protected EmptyItem emptyItem, emptyItem2;
	protected AttributeItem item;
	protected AttributeItem item2;

	@BeforeEach final void setUpFieldTest()
	{
		emptyItem = new EmptyItem();
		emptyItem2 = new EmptyItem();
		item = new AttributeItem("someString", 5, 6l, 2.2, true, emptyItem, AttributeItem.SomeEnum.enumValue1);
		item2 = new AttributeItem("someString2", 6, 7l, 2.3, false, emptyItem2, AttributeItem.SomeEnum.enumValue2);
	}

	protected static <R> List<? extends R> search(final FunctionField<? extends R> selectAttribute)
	{
		return search(selectAttribute, null);
	}

	protected static <R> List<? extends R> search(final FunctionField<? extends R> selectAttribute, final Condition condition)
	{
		return new Query<>(selectAttribute, condition).search();
	}

}
