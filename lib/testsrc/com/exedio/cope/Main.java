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

import com.exedio.cope.testmodel.HashItem;
import com.exedio.cope.testmodel.MD5Item;
import com.exedio.cope.testmodel.VectorItem;


public class Main
{
	public static final Model hashModel = new Model(new Type[] { HashItem.TYPE });
	public static final Model md5Model = new Model(new Type[] { MD5Item.TYPE });
	public static final Model vectorModel = new Model(new Type[] { VectorItem.TYPE });

	private static final void tearDown(final Model model)
	{
		model.setPropertiesInitially(new Properties());
		model.tearDownDatabase();
	}
	
	public static void main(String[] args)
	{
		tearDown(com.exedio.cope.testmodel.Main.model);
		tearDown(hashModel);
		tearDown(md5Model);
		tearDown(vectorModel);
	}

}
