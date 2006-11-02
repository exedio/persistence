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
	static final Model MODEL = new Model(DeleteHierarchySource.TYPE, DeleteHierarchyTargetSuper.TYPE, DeleteHierarchyTargetSub.TYPE);

	public DeleteHierarchyTest()
	{
		super(MODEL);
	}
	
	public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(list(), DeleteHierarchySource.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(), DeleteHierarchySource.TYPE.getReferences());
		assertEqualsUnmodifiable(list(DeleteHierarchySource.target), DeleteHierarchyTargetSuper.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(DeleteHierarchySource.target), DeleteHierarchyTargetSuper.TYPE.getReferences());
		assertEqualsUnmodifiable(list(), DeleteHierarchyTargetSub.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(DeleteHierarchySource.target), DeleteHierarchyTargetSub.TYPE.getReferences());
		
		// test persistence
		final DeleteHierarchyTargetSub target = new DeleteHierarchyTargetSub("target");
		final DeleteHierarchySource source = new DeleteHierarchySource(target);
		target.deleteCopeItem();
		assertFalse(source.existsCopeItem());
		assertFalse(target.existsCopeItem());
	}
}
