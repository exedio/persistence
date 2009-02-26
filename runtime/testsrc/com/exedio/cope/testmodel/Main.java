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

package com.exedio.cope.testmodel;

import java.util.Date;

import com.exedio.cope.Revision;
import com.exedio.cope.Model;
import com.exedio.cope.Type;

public class Main
{
	public static final Type[] modelTypes = new Type[]
	{
		ItemWithSingleUnique.TYPE,
		UniqueFinal.TYPE,
		ItemWithSingleUniqueNotNull.TYPE,
		ItemWithDoubleUnique.TYPE,
		EmptyItem.TYPE,
		EmptyItem2.TYPE,
		AttributeItem.TYPE,
		StringItem.TYPE,
		PointerTargetItem.TYPE,
		PointerItem.TYPE,
		FinalItem.TYPE,
		CollisionItem1.TYPE,
		CollisionItem2.TYPE,
	};
	
	@SuppressWarnings("unused") // OK: to be used by model for debugging
	private static final Revision[] revisions = new Revision[]
	{
		new Revision(2, "comment2 a bit longer",
				"select nice sql statement of revision two which does not work",
				"select second nice sql statement of revision two which does not work"),
		new Revision(1, "comment1<andTag>",
				"select nice sql statement of revision one which does not work <andAnotherTag>"),
	};

	public static final Date beforeModel = new Date();
	
	public static final Model model = new Model(
		//revisions,
		//0,
		modelTypes);
	
	public static final Date afterModel = new Date();
}
