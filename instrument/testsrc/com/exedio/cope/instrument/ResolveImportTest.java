package com.exedio.cope.instrument;

import com.exedio.cope.instrument.findtype.BothFindType;
import com.exedio.cope.instrument.findtype.FindType;
import com.exedio.cope.instrument.findtype.subfindtype.BothFindType2;
import com.exedio.cope.instrument.findtype.subfindtype.SubFindType;
import com.exedio.cope.instrument.findtype.subfindtype2.SubFindType2;
import com.exedio.cope.instrument.findtype.subfindtype2.SubFindType3Non;

public class ResolveImportTest extends InstrumentorTest
{
	public ResolveImportTest(final String name)
	{
		super(name);
	}
	
	public void testImports() throws InjectorParseException
	{
		final JavaFile file = new JavaFile();
		file.setPackage("com.exedio.cope.instrument.findtype");
		
		file.addImport("com.exedio.cope.instrument.findtype.subfindtype.*");
		file.addImport("com.exedio.cope.instrument.findtype.subfindtype2.SubFindType2");
		file.addImport(com.exedio.cope.instrument.findtype.subfindtype.BothFindType.class.getName());
		file.addImport("com.exedio.cope.instrument.findtype.collide.*");
		
		assertEquals(FindType.class, file.findType("FindType"));
		assertEquals(FindType.class, file.findType(FindType.class.getName()));

		assertEquals(SubFindType.class, file.findType("SubFindType"));
		assertEquals(SubFindType.class, file.findType(SubFindType.class.getName()));

		assertEquals(SubFindType2.class, file.findType("SubFindType2"));
		assertEquals(SubFindType2.class, file.findType(SubFindType2.class.getName()));

		assertEquals(BothFindType.class, file.findType("BothFindType"));
		assertEquals(BothFindType.class, file.findType(BothFindType.class.getName()));

		assertEquals(BothFindType2.class, file.findType("BothFindType2"));
		assertEquals(BothFindType2.class, file.findType(BothFindType2.class.getName()));

		try
		{
			file.findType("SubFindType3Non");
		}
		catch(InjectorParseException e)
		{
			assertEquals("type SubFindType3Non not found.", e.getMessage());
		}
		assertEquals(SubFindType3Non.class, file.findType(SubFindType3Non.class.getName()));

		assertEquals(
			com.exedio.cope.instrument.findtype.subfindtype.CollideType.class,
			file.findType(com.exedio.cope.instrument.findtype.subfindtype.CollideType.class.getName()));
		assertEquals(
			com.exedio.cope.instrument.findtype.collide.CollideType.class,
			file.findType(com.exedio.cope.instrument.findtype.collide.CollideType.class.getName()));
		try
		{
			file.findType("CollideType");
		}
		catch(InjectorParseException e)
		{
			assertEquals(
				"type CollideType found in imported packages [com.exedio.cope.instrument.findtype.collide, com.exedio.cope.instrument.findtype.subfindtype]. "
				+ "This is ambigous and forbidden by Java Language Specification 6.5.4.1. 'Simple Type Names' item 4.",
				e.getMessage());
		}
	}

}
