package com.exedio.cope.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.testmodel.NullabilityItem;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

class NullabilityTest
{
	@Test
	void nullability() throws HumanReadableException, IOException
	{
		new JavaRepositoryBuilder().filter(NullabilityItem.class).buildAndRun(
				repository ->
				{
					final LocalCopeType nullabilityItem = repository.getCopeType(NullabilityItem.class.getName());
					assertEquals("NullabilityItem", nullabilityItem.getName());
					final LocalCopeFeature optional = nullabilityItem.getFeatures().get(0);
					assertEquals("optional", optional.getName());
					final WrapperX allCanReturnNull = optional.getWrappers().get(0);
					assertEquals("allCanReturnNull", allCanReturnNull.name);
					assertEquals(Nullability.NULLABLE, allCanReturnNull.getMethodNullability());
				}
		);
	}

	@Test
	void nonnullClassName()
	{
		assertEquals(Nonnull.class.getName(), Nullability.NONNULL_CLASS_NAME);
	}
}
