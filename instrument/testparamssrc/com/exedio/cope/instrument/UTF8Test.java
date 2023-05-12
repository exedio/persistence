package com.exedio.cope.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.parameters.UTF8Item;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class UTF8Test
{
	@Test
	void utf8() throws HumanReadableException, IOException
	{
		new JavaRepositoryBuilder()
				.withSrcDir(new File("instrument/testparamssrc"))
				.withClasspath(new File("build/classes/instrument/testparamssrc"))
				.filter(new File("instrument/testparamssrc/" + UTF8Item.class.getName().replace(".", "/") + ".java"))
				.charset(StandardCharsets.UTF_8)
				.buildAndRun(
					repository ->
					{
						final LocalCopeType nullabilityItem = repository.getCopeType(UTF8Item.class.getName());
						assertEquals("UTF8Item", nullabilityItem.getName());
						final LocalCopeFeature string = nullabilityItem.getFeatures().get(0);
						assertEquals("string", string.getName());
						assertEquals("new StringField().defaultTo(\"Hall\\u00f6le! Arabic(\\u0625) Cyrillic(\\u0425) Emoji(\\ud83d\\ude31\\ud83c\\udd92)\")", string.getInitializer());
					}
		);
	}
}
