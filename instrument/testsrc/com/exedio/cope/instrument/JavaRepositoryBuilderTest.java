package com.exedio.cope.instrument;

import static com.exedio.cope.instrument.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.testmodel.BlockFully;
import com.exedio.cope.instrument.testmodel.CompositeFully;
import com.exedio.cope.instrument.testmodel.DoubleUnique;
import com.exedio.cope.instrument.testmodel.Enum2;
import com.exedio.cope.instrument.testmodel.EnumContainer;
import com.exedio.cope.instrument.testmodel.Standard;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.Composite;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class JavaRepositoryBuilderTest
{
	@Test
	void all() throws HumanReadableException, IOException
	{
		new JavaRepositoryBuilder().buildAndRun(
				javaRepository ->
				{
					// picked some random classes from instrument/testsrc, just to see that they are in the repository:
					assertEquals(Block.class, javaRepository.getJavaClass(BlockFully.class.getCanonicalName()).kind.topClass);
					assertEquals(Composite.class, javaRepository.getJavaClass(CompositeFully.class.getCanonicalName()).kind.topClass);
					assertEquals(Item.class, javaRepository.getJavaClass(DoubleUnique.class.getCanonicalName()).kind.topClass);
				}
		);
	}

	@Test
	void filtered_inner()
	{
		assertFails(
				()->new JavaRepositoryBuilder().filter(Inner.class).buildAndRun(repository -> fail()),
				RuntimeException.class,
				"not a file: instrument"+File.separator+"testsrc"+File.separator+"com"+File.separator+"exedio"+
						File.separator+"cope"+File.separator+"instrument"+File.separator+
						"JavaRepositoryBuilderTest$Inner.java"
		);
	}

	@SuppressWarnings("EmptyClass")
	class Inner
	{
	}

	@Test
	void filtered_noOther() throws HumanReadableException, IOException
	{
		new JavaRepositoryBuilder().filter(Standard.class).filter(EnumContainer.class).filter(Enum2.class).buildAndRun(
				repository -> assertEquals(null, repository.getJavaClass(DoubleUnique.class.getCanonicalName()))
		);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Test
	void filtered_standard() throws HumanReadableException, IOException
	{
		new JavaRepositoryBuilder().filter(Standard.class).filter(EnumContainer.class).filter(Enum2.class).buildAndRun(
				repository ->
				{
					final JavaClass standard = repository.getJavaClass(Standard.class.getCanonicalName());
					assertEquals("Standard", standard.name);
					assertEquals("com.exedio.cope.Item", standard.fullyQualifiedSuperclass);
					final JavaField defaultString = standard.getFields().get(0);
					assertEquals(StringField.class.getName(), defaultString.typeFullyQualified);
					final JavaField mandatoryEnum = standard.getFields().stream().filter(f->"mandatoryEnum".equals(f.name)).findFirst().get();
					assertEquals("Enum1", mandatoryEnum.getTypeParameter(0));
					assertEquals(EnumField.class.getName(), mandatoryEnum.typeFullyQualified);
					final LocalCopeType standardType = repository.getCopeType(standard);
					final LocalCopeFeature mandatoryEnumFeature = standardType.getFeature("mandatoryEnum");
					final EnumField<?> instance = (EnumField<?>) mandatoryEnumFeature.getInstance();
					assertEquals(Standard.Enum1.ENUM1A, instance.getDefaultConstant());
				}
		);
	}
}
