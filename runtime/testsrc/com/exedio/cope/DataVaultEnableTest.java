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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.minimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class DataVaultEnableTest
{
	@Test void testAnnotationsField()
	{
		assertEquals(false, MyBlank.blankF.isAnnotationPresent(Vault.class));
		assertEquals(false, MyBlank.blankM.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyBlank.vaultF.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyBlank.vaultM.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyBlank.nonneF.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyBlank.nonneM.isAnnotationPresent(Vault.class));

		assertEquals(false, AnVault.blankF.isAnnotationPresent(Vault.class));
		assertEquals(false, AnVault.blankM.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.vaultF.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.vaultM.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.nonneF.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.nonneM.isAnnotationPresent(Vault.class));

		assertEquals(false, MyNonne.blankF.isAnnotationPresent(Vault.class));
		assertEquals(false, MyNonne.blankM.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyNonne.vaultF.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyNonne.vaultM.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyNonne.nonneF.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyNonne.nonneM.isAnnotationPresent(Vault.class));
	}
	@Test void testAnnotationsType()
	{
		assertEquals(false, MyBlank.TYPE.isAnnotationPresent(Vault.class));
		assertEquals(true,  AnVault.TYPE.isAnnotationPresent(Vault.class));
		assertEquals(true,  MyNonne.TYPE.isAnnotationPresent(Vault.class));
	}
	@Test void testIsAnnotatedVault()
	{
		assertEquals(false, MyBlank.blankF.isAnnotatedVault());
		assertEquals(false, MyBlank.blankM.isAnnotatedVault());
		assertEquals(true,  MyBlank.vaultF.isAnnotatedVault());
		assertEquals(true,  MyBlank.vaultM.isAnnotatedVault());
		assertEquals(false, MyBlank.nonneF.isAnnotatedVault());
		assertEquals(false, MyBlank.nonneM.isAnnotatedVault());

		assertEquals(true,  AnVault.blankF.isAnnotatedVault());
		assertEquals(true,  AnVault.blankM.isAnnotatedVault());
		assertEquals(true,  AnVault.vaultF.isAnnotatedVault());
		assertEquals(true,  AnVault.vaultM.isAnnotatedVault());
		assertEquals(false, AnVault.nonneF.isAnnotatedVault());
		assertEquals(false, AnVault.nonneM.isAnnotatedVault());

		assertEquals(false, MyNonne.blankF.isAnnotatedVault());
		assertEquals(false, MyNonne.blankM.isAnnotatedVault());
		assertEquals(true,  MyNonne.vaultF.isAnnotatedVault());
		assertEquals(true,  MyNonne.vaultM.isAnnotatedVault());
		assertEquals(false, MyNonne.nonneF.isAnnotatedVault());
		assertEquals(false, MyNonne.nonneM.isAnnotatedVault());
	}
	@Test void testGetAnnotatedVaultValue()
	{
		assertEquals(null,               MyBlank.blankF.getAnnotatedVaultValue());
		assertEquals(null,               MyBlank.blankM.getAnnotatedVaultValue());
		assertEquals("MyBlank-vaultF-V", MyBlank.vaultF.getAnnotatedVaultValue());
		assertEquals("MyBlank-vaultM-V", MyBlank.vaultM.getAnnotatedVaultValue());
		assertEquals(null,               MyBlank.nonneF.getAnnotatedVaultValue());
		assertEquals(null,               MyBlank.nonneM.getAnnotatedVaultValue());

		assertEquals("AnVault-V",        AnVault.blankF.getAnnotatedVaultValue());
		assertEquals("AnVault-V",        AnVault.blankM.getAnnotatedVaultValue());
		assertEquals("AnVault-vaultF-V", AnVault.vaultF.getAnnotatedVaultValue());
		assertEquals("AnVault-vaultM-V", AnVault.vaultM.getAnnotatedVaultValue());
		assertEquals(null,               AnVault.nonneF.getAnnotatedVaultValue());
		assertEquals(null,               AnVault.nonneM.getAnnotatedVaultValue());

		assertEquals(null,               MyNonne.blankF.getAnnotatedVaultValue());
		assertEquals(null,               MyNonne.blankM.getAnnotatedVaultValue());
		assertEquals("MyNonne-vaultF-V", MyNonne.vaultF.getAnnotatedVaultValue());
		assertEquals("MyNonne-vaultM-V", MyNonne.vaultM.getAnnotatedVaultValue());
		assertEquals(null,               MyNonne.nonneF.getAnnotatedVaultValue());
		assertEquals(null,               MyNonne.nonneM.getAnnotatedVaultValue());
	}
	@Test void testDisabled()
	{
		model.connect(ConnectProperties.create(minimal()));

		assertIt(MyBlank.blankF);
		assertIt(MyBlank.blankM);
		assertIt(MyBlank.vaultF);
		assertIt(MyBlank.vaultM);
		assertIt(MyBlank.nonneF);
		assertIt(MyBlank.nonneM);

		assertIt(AnVault.blankF);
		assertIt(AnVault.blankM);
		assertIt(AnVault.vaultF);
		assertIt(AnVault.vaultM);
		assertIt(AnVault.nonneF);
		assertIt(AnVault.nonneM);

		assertIt(MyNonne.blankF);
		assertIt(MyNonne.blankM);
		assertIt(MyNonne.vaultF);
		assertIt(MyNonne.vaultM);
		assertIt(MyNonne.nonneF);
		assertIt(MyNonne.nonneM);
	}
	@Test void testEnabledAny()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.buckets", "default MyBlank-vaultF-V MyBlank-vaultM-V AnVault-V AnVault-vaultF-V AnVault-vaultM-V MyNonne-vaultF-V MyNonne-vaultM-V"),
				single("vault.default.service",          VaultMockService.class),
				single("vault.default.service.example",          "default-X"),
				single("vault.MyBlank-vaultF-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultF-V.service.example", "MyBlank-vaultF-X"),
				single("vault.MyBlank-vaultM-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultM-V.service.example", "MyBlank-vaultM-X"),
				single("vault.AnVault-V.service",        VaultMockService.class),
				single("vault.AnVault-V.service.example",        "AnVault-X"),
				single("vault.AnVault-vaultF-V.service", VaultMockService.class),
				single("vault.AnVault-vaultF-V.service.example", "AnVault-vaultF-X"),
				single("vault.AnVault-vaultM-V.service", VaultMockService.class),
				single("vault.AnVault-vaultM-V.service.example", "AnVault-vaultM-X"),
				single("vault.MyNonne-vaultF-V.service", VaultMockService.class),
				single("vault.MyNonne-vaultF-V.service.example", "MyNonne-vaultF-X"),
				single("vault.MyNonne-vaultM-V.service", VaultMockService.class),
				single("vault.MyNonne-vaultM-V.service.example", "MyNonne-vaultM-X"),
				single("vault.isAppliedToAllFields", true),
				minimal()
		)));

		assertIt(MyBlank.blankF, "VaultMockService:default-X");
		assertIt(MyBlank.blankM, "VaultMockService:default-X");
		assertIt(MyBlank.vaultF, "VaultMockService:MyBlank-vaultF-X");
		assertIt(MyBlank.vaultM, "VaultMockService:MyBlank-vaultM-X");
		assertIt(MyBlank.nonneF, "VaultMockService:default-X");
		assertIt(MyBlank.nonneM, "VaultMockService:default-X");

		assertIt(AnVault.blankF, "VaultMockService:AnVault-X");
		assertIt(AnVault.blankM, "VaultMockService:AnVault-X");
		assertIt(AnVault.vaultF, "VaultMockService:AnVault-vaultF-X");
		assertIt(AnVault.vaultM, "VaultMockService:AnVault-vaultM-X");
		assertIt(AnVault.nonneF, "VaultMockService:default-X");
		assertIt(AnVault.nonneM, "VaultMockService:default-X");

		assertIt(MyNonne.blankF, "VaultMockService:default-X");
		assertIt(MyNonne.blankM, "VaultMockService:default-X");
		assertIt(MyNonne.vaultF, "VaultMockService:MyNonne-vaultF-X");
		assertIt(MyNonne.vaultM, "VaultMockService:MyNonne-vaultM-X");
		assertIt(MyNonne.nonneF, "VaultMockService:default-X");
		assertIt(MyNonne.nonneM, "VaultMockService:default-X");
	}
	@Test void testEnabled()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.buckets", "MyBlank-vaultF-V MyBlank-vaultM-V AnVault-V AnVault-vaultF-V AnVault-vaultM-V MyNonne-vaultF-V MyNonne-vaultM-V"),
				single("vault.MyBlank-vaultF-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultF-V.service.example", "MyBlank-vaultF-X"),
				single("vault.MyBlank-vaultM-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultM-V.service.example", "MyBlank-vaultM-X"),
				single("vault.AnVault-V.service",        VaultMockService.class),
				single("vault.AnVault-V.service.example",        "AnVault-X"),
				single("vault.AnVault-vaultF-V.service", VaultMockService.class),
				single("vault.AnVault-vaultF-V.service.example", "AnVault-vaultF-X"),
				single("vault.AnVault-vaultM-V.service", VaultMockService.class),
				single("vault.AnVault-vaultM-V.service.example", "AnVault-vaultM-X"),
				single("vault.MyNonne-vaultF-V.service", VaultMockService.class),
				single("vault.MyNonne-vaultF-V.service.example", "MyNonne-vaultF-X"),
				single("vault.MyNonne-vaultM-V.service", VaultMockService.class),
				single("vault.MyNonne-vaultM-V.service.example", "MyNonne-vaultM-X"),
				minimal()
		)));

		assertIt(MyBlank.blankF);
		assertIt(MyBlank.blankM);
		assertIt(MyBlank.vaultF, "VaultMockService:MyBlank-vaultF-X");
		assertIt(MyBlank.vaultM, "VaultMockService:MyBlank-vaultM-X");
		assertIt(MyBlank.nonneF);
		assertIt(MyBlank.nonneM);

		assertIt(AnVault.blankF, "VaultMockService:AnVault-X");
		assertIt(AnVault.blankM, "VaultMockService:AnVault-X");
		assertIt(AnVault.vaultF, "VaultMockService:AnVault-vaultF-X");
		assertIt(AnVault.vaultM, "VaultMockService:AnVault-vaultM-X");
		assertIt(AnVault.nonneF);
		assertIt(AnVault.nonneM);

		assertIt(MyNonne.blankF);
		assertIt(MyNonne.blankM);
		assertIt(MyNonne.vaultF, "VaultMockService:MyNonne-vaultF-X");
		assertIt(MyNonne.vaultM, "VaultMockService:MyNonne-vaultM-X");
		assertIt(MyNonne.nonneF);
		assertIt(MyNonne.nonneM);
	}
	@Test void testMissingBucketAppliedToAllFields()
	{
		final ConnectProperties props = ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.buckets", "MyBlank-vaultF-V MyBlank-vaultM-V"),
				single("vault.MyBlank-vaultF-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultM-V.service", VaultMockService.class),
				single("vault.isAppliedToAllFields", true),
				minimal()
		));
		assertFails(
				() -> model.connect(props),
				IllegalArgumentException.class,
				"@Vault for buckets [default, AnVault-V, AnVault-vaultF-V, AnVault-vaultM-V, MyNonne-vaultF-V, MyNonne-vaultM-V] " +
				"not supported by ConnectProperties.");
	}
	@Test void testMissingBucket()
	{
		final ConnectProperties props = ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.buckets", "MyBlank-vaultF-V MyBlank-vaultM-V"),
				single("vault.MyBlank-vaultF-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultM-V.service", VaultMockService.class),
				minimal()
		));
		assertFails(
				() -> model.connect(props),
				IllegalArgumentException.class,
				"@Vault for buckets [AnVault-V, AnVault-vaultF-V, AnVault-vaultM-V, MyNonne-vaultF-V, MyNonne-vaultM-V] " +
				"not supported by ConnectProperties.");
	}
	@Test void testAlgorithm()
	{
		model.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.MyBlank-vaultF-V.algorithm", "MD5"),
				single("vault.buckets", "MyBlank-vaultF-V MyBlank-vaultM-V AnVault-V AnVault-vaultF-V AnVault-vaultM-V MyNonne-vaultF-V MyNonne-vaultM-V"),
				single("vault.MyBlank-vaultF-V.service", VaultMockService.class),
				single("vault.MyBlank-vaultM-V.service", VaultMockService.class),
				single("vault.AnVault-V.service",        VaultMockService.class),
				single("vault.AnVault-vaultF-V.service", VaultMockService.class),
				single("vault.AnVault-vaultM-V.service", VaultMockService.class),
				single("vault.MyNonne-vaultF-V.service", VaultMockService.class),
				single("vault.MyNonne-vaultM-V.service", VaultMockService.class),
				minimal()
		)));
		final DataFieldVaultInfo info = MyBlank.vaultF.getVaultInfo();
		assertNotNull(info);
		assertSame(MyBlank.vaultF, info.getField(), "field");
		assertEquals("MyBlank-vaultF-V", info.getBucket(), "bucket");
		assertEquals("VaultMockService:exampleDefault", info.getService(), "service");
		assertEquals("VARCHAR(32) not null", type(MyBlank.vaultF));
	}


	private static void assertIt(
			final DataField field)
	{
		assertNull(field.getVaultInfo());
		assertEquals("BLOB not null", type(field));
	}

	private static void assertIt(
			final DataField field,
			final String service)
	{
		final DataFieldVaultInfo actual = field.getVaultInfo();
		assertNotNull(actual);
		assertSame(field, actual.getField(), "field");
		assertEquals(service, actual.getService(), "service");
		assertEquals("VARCHAR(128) not null", type(field));
	}

	private static void assertIt(
			final Media media)
	{
		assertIt(media.getBody());
	}

	private static void assertIt(
			final Media media,
			final String service)
	{
		assertIt(media.getBody(), service);
	}

	private static String type(final DataField field)
	{
		return model.
				getSchema().
				getTable(SchemaInfo.getTableName(field.getType())).
				getColumn(SchemaInfo.getColumnName(field)).
				getType();
	}

	@AfterEach final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	static final Model model = new Model(MyBlank.TYPE, AnVault.TYPE, MyNonne.TYPE);

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyBlank extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blankF = new DataField();

		@Vault("MyBlank-vaultF-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField vaultF = new DataField();

		@Vault(Vault.NONE)
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField nonneF = new DataField();

		@Wrapper(wrap="*", visibility=NONE)
		static final Media blankM = new Media();

		@Vault("MyBlank-vaultM-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final Media vaultM = new Media();

		@Vault(Vault.NONE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media nonneM = new Media();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyBlank> TYPE = com.exedio.cope.TypesBound.newType(MyBlank.class,MyBlank::new);

		@com.exedio.cope.instrument.Generated
		protected MyBlank(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Vault("AnVault-V")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class AnVault extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blankF = new DataField();

		@Vault("AnVault-vaultF-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField vaultF = new DataField();

		@Vault(Vault.NONE)
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField nonneF = new DataField();

		@Wrapper(wrap="*", visibility=NONE)
		static final Media blankM = new Media();

		@Vault("AnVault-vaultM-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final Media vaultM = new Media();

		@Vault(Vault.NONE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media nonneM = new Media();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnVault> TYPE = com.exedio.cope.TypesBound.newType(AnVault.class,AnVault::new);

		@com.exedio.cope.instrument.Generated
		protected AnVault(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Vault(Vault.NONE)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyNonne extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField blankF = new DataField();

		@Vault("MyNonne-vaultF-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField vaultF = new DataField();

		@Vault(Vault.NONE)
		@Wrapper(wrap="*", visibility=NONE)
		static final DataField nonneF = new DataField();

		@Wrapper(wrap="*", visibility=NONE)
		static final Media blankM = new Media();

		@Vault("MyNonne-vaultM-V")
		@Wrapper(wrap="*", visibility=NONE)
		static final Media vaultM = new Media();

		@Vault(Vault.NONE)
		@Wrapper(wrap="*", visibility=NONE)
		static final Media nonneM = new Media();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyNonne> TYPE = com.exedio.cope.TypesBound.newType(MyNonne.class,MyNonne::new);

		@com.exedio.cope.instrument.Generated
		protected MyNonne(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
