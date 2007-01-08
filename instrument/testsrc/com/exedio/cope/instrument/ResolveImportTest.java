/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import com.exedio.cope.instrument.findtype.BothFindType;
import com.exedio.cope.instrument.findtype.FindType;
import com.exedio.cope.instrument.findtype.subfindtype.BothFindType2;
import com.exedio.cope.instrument.findtype.subfindtype.SubFindType;
import com.exedio.cope.instrument.findtype.subfindtype2.SubFindType2;
import com.exedio.cope.instrument.findtype.subfindtype2.SubFindType3Non;

public class ResolveImportTest extends InstrumentorTest
{
	
	public void testImports() throws InjectorParseException
	{
		final JavaRepository repository = new JavaRepository();
		final JavaFile file = new JavaFile(repository);
		file.setPackage("com.exedio.cope.instrument.findtype");
		
		file.addImport("com.exedio.cope.instrument.findtype.subfindtype.*");
		file.addImport("com.exedio.cope.instrument.findtype.subfindtype2.SubFindType2");
		file.addImport(com.exedio.cope.instrument.findtype.subfindtype.BothFindType.class.getName());
		file.addImport("com.exedio.cope.instrument.findtype.collide.*");
		
		repository.endBuildStage();
		
		assertEquals(list(file), repository.getFiles());
		
		assertEquals(FindType.class, file.findTypeExternally("FindType"));
		assertEquals(FindType.class, file.findTypeExternally(FindType.class.getName()));

		assertEquals(SubFindType.class, file.findTypeExternally("SubFindType"));
		assertEquals(SubFindType.class, file.findTypeExternally(SubFindType.class.getName()));

		assertEquals(SubFindType2.class, file.findTypeExternally("SubFindType2"));
		assertEquals(SubFindType2.class, file.findTypeExternally(SubFindType2.class.getName()));

		assertEquals(com.exedio.cope.instrument.findtype.subfindtype.BothFindType.class /* TODO should be BothFindType.class */, file.findTypeExternally("BothFindType"));
		assertEquals(BothFindType.class, file.findTypeExternally(BothFindType.class.getName()));

		assertEquals(BothFindType2.class, file.findTypeExternally("BothFindType2"));
		assertEquals(BothFindType2.class, file.findTypeExternally(BothFindType2.class.getName()));

		assertEquals(null, file.findTypeExternally("SubFindType3Non"));
		assertEquals(SubFindType3Non.class, file.findTypeExternally(SubFindType3Non.class.getName()));

		assertEquals(
			com.exedio.cope.instrument.findtype.subfindtype.CollideType.class,
			file.findTypeExternally(com.exedio.cope.instrument.findtype.subfindtype.CollideType.class.getName()));
		assertEquals(
			com.exedio.cope.instrument.findtype.collide.CollideType.class,
			file.findTypeExternally(com.exedio.cope.instrument.findtype.collide.CollideType.class.getName()));
		assertEquals(
			com.exedio.cope.instrument.findtype.collide.CollideType.class/* TODO should be null, because of collision */,
			file.findTypeExternally("CollideType"));
	}

}
