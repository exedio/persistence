/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


public class DeleteHierarchyTest extends AbstractLibTest
{
	public DeleteHierarchyTest()
	{
		super(Main.deleteHierarchyModel);
	}
	
	public void testIt()
	{
		// test model
		assertEquals(list(), DeleteHierarchySource.TYPE.getDeclaredReferences());
		assertEquals(list(), DeleteHierarchySource.TYPE.getReferences());
		assertEquals(list(DeleteHierarchySource.target), DeleteHierarchyTargetSuper.TYPE.getDeclaredReferences());
		assertEquals(list(DeleteHierarchySource.target), DeleteHierarchyTargetSuper.TYPE.getReferences());
		assertEquals(list(), DeleteHierarchyTargetSub.TYPE.getDeclaredReferences());
		assertEquals(list(DeleteHierarchySource.target), DeleteHierarchyTargetSub.TYPE.getReferences());
		
		// test persistence
		final DeleteHierarchyTargetSub target = new DeleteHierarchyTargetSub("target");
		final DeleteHierarchySource source = new DeleteHierarchySource(target);
		target.deleteCopeItem();
		assertFalse(source.existsCopeItem());
		assertFalse(target.existsCopeItem());
	}
}
