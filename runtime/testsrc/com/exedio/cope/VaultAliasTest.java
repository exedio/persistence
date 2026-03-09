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
import static com.exedio.cope.instrument.Wrapper.ALL_WRAPS;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.vault.VaultPropertiesTest.deresiliate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class VaultAliasTest
{
	@Test void model()
	{
		assertEquals(null,   AnItem.none.getAnnotatedVaultValue());
		assertEquals("eins", AnItem.eins.getAnnotatedVaultValue());
		assertEquals("zwei", AnItem.zwei.getAnnotatedVaultValue());
		assertEquals("drei", AnItem.drei.getAnnotatedVaultValue());
	}

	@Test void noAlias()
	{
		connect(props(
				single("vault.buckets", "eins zwei drei"),
				single("vault.eins.service", VaultMockService.class),
				single("vault.eins.service.example", "einsEx"),
				single("vault.zwei.service", VaultMockService.class),
				single("vault.zwei.service.example", "zweiEx"),
				single("vault.drei.service", VaultMockService.class),
				single("vault.drei.service.example", "dreiEx")));
		assertEquals(Set.of("eins", "zwei", "drei"), connect.vaultBuckets());
		assertInfo(null,   AnItem.none);
		assertInfo("eins", AnItem.eins);
		assertInfo("zwei", AnItem.zwei);
		assertInfo("drei", AnItem.drei);

		newItem();
		//noinspection resource
		final VaultMockService einsS = service("eins");
		//noinspection resource
		final VaultMockService zweiS = service("zwei");
		//noinspection resource
		final VaultMockService dreiS = service("drei");
		assertEquals("einsEx", einsS.serviceProperties.example);
		assertEquals("zweiEx", zweiS.serviceProperties.example);
		assertEquals("dreiEx", dreiS.serviceProperties.example);
		einsS.assertIt(HASH_EINS, "010203", "putBytes");
		zweiS.assertIt(HASH_ZWEI, "020304", "putBytes");
		dreiS.assertIt(HASH_DREI, "030405", "putBytes");
	}

	@Test void dreiIntoZwei()
	{
		final var props = props(
				single("vault.buckets", "eins zwei"),
				single("vault.eins.service", VaultMockService.class),
				single("vault.eins.service.example", "einsEx"),
				single("vault.zwei.service", VaultMockService.class),
				single("vault.zwei.service.example", "zweiEx"),
				single("vault.aliases.count", 1),
				single("vault.aliases.0", "drei>zwei"));
		final var vp = props.getVaultProperties();
		assertEquals("eins", vp.bucket("eins").getKey());
		assertEquals("zwei", vp.bucket("zwei").getKey());
		assertSame(vp.bucket("zwei"), vp.bucket("drei"));

		connect(props);
		assertEquals(Set.of("eins", "zwei"), connect.vaultBuckets());
		assertInfo(null,   AnItem.none);
		assertInfo("eins", AnItem.eins);
		assertInfo("zwei", AnItem.zwei);
		assertInfo("zwei", AnItem.drei);

		newItem();
		//noinspection resource
		final VaultMockService einsS = service("eins");
		//noinspection resource
		final VaultMockService zweiS = service("zwei");
		assertFails(
				() -> connect.vault("drei"),
				IllegalArgumentException.class,
				"bucket drei does not exist, use one of [eins, zwei]");
		assertEquals("einsEx", einsS.serviceProperties.example);
		assertEquals("zweiEx", zweiS.serviceProperties.example);
		einsS.assertIt(HASH_EINS, "010203", "putBytes");
		zweiS.assertIt(HASH_ZWEI, "020304", HASH_DREI, "030405", "putBytes putBytes");
	}

	@Test void dreiIntoZweiFactory()
	{
		final var props = ConnectProperties.factory().
				vault(VaultProperties.factory().aliases(Map.of("drei", "zwei"))).
				create(cascade(
						single("vault", true),
						single("vault.buckets", "eins zwei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.eins.service.example", "einsEx"),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.zwei.service.example", "zweiEx"),
						TestSources.minimal()));
		assertEquals(Set.of(), props.getOrphanedKeys(), "orphanedKeys");
		final var vp = props.getVaultProperties();
		assertEquals("eins", vp.bucket("eins").getKey());
		assertEquals("zwei", vp.bucket("zwei").getKey());
		assertSame(vp.bucket("zwei"), vp.bucket("drei"));

		connect(props);
		assertEquals(Set.of("eins", "zwei"), connect.vaultBuckets());
		assertInfo(null,   AnItem.none);
		assertInfo("eins", AnItem.eins);
		assertInfo("zwei", AnItem.zwei);
		assertInfo("zwei", AnItem.drei);

		newItem();
		//noinspection resource
		final VaultMockService einsS = service("eins");
		//noinspection resource
		final VaultMockService zweiS = service("zwei");
		assertFails(
				() -> connect.vault("drei"),
				IllegalArgumentException.class,
				"bucket drei does not exist, use one of [eins, zwei]");
		assertEquals("einsEx", einsS.serviceProperties.example);
		assertEquals("zweiEx", zweiS.serviceProperties.example);
		einsS.assertIt(HASH_EINS, "010203", "putBytes");
		zweiS.assertIt(HASH_ZWEI, "020304", HASH_DREI, "030405", "putBytes putBytes");
	}

	@Test void dreiIntoZweiButExists()
	{
		final var props = props(
				single("vault.buckets", "eins zwei drei"),
				single("vault.eins.service", VaultMockService.class),
				single("vault.eins.service.example", "einsEx"),
				single("vault.zwei.service", VaultMockService.class),
				single("vault.zwei.service.example", "zweiEx"),
				single("vault.drei.service", VaultMockService.class),
				single("vault.drei.service.example", "dreiEx"),
				single("vault.aliases.count", 1),
				single("vault.aliases.0", "drei>zwei"));
		final var vp = props.getVaultProperties();
		assertEquals("eins", vp.bucket("eins").getKey());
		assertEquals("zwei", vp.bucket("zwei").getKey());
		assertSame(vp.bucket("zwei"), vp.bucket("drei"));

		connect(props);
		assertEquals(Set.of("eins", "zwei", "drei"), connect.vaultBuckets());
		assertInfo(null,   AnItem.none);
		assertInfo("eins", AnItem.eins);
		assertInfo("zwei", AnItem.zwei);
		assertInfo("zwei", AnItem.drei);

		newItem();
		//noinspection resource
		final VaultMockService einsS = service("eins");
		//noinspection resource
		final VaultMockService zweiS = service("zwei");
		//noinspection resource
		final VaultMockService dreiS = service("drei");
		assertEquals("einsEx", einsS.serviceProperties.example);
		assertEquals("zweiEx", zweiS.serviceProperties.example);
		assertEquals("dreiEx", dreiS.serviceProperties.example);
		einsS.assertIt(HASH_EINS, "010203", "putBytes");
		zweiS.assertIt(HASH_ZWEI, "020304", HASH_DREI, "030405", "putBytes putBytes");
		dreiS.assertIt("");
	}

	@Test void dreiSwappedWithZwei()
	{
		connect(props(
				single("vault.buckets", "eins zwei drei"),
				single("vault.eins.service", VaultMockService.class),
				single("vault.eins.service.example", "einsEx"),
				single("vault.zwei.service", VaultMockService.class),
				single("vault.zwei.service.example", "zweiEx"),
				single("vault.drei.service", VaultMockService.class),
				single("vault.drei.service.example", "dreiEx"),
				single("vault.aliases.count", 2),
				single("vault.aliases.0", "drei>zwei"),
				single("vault.aliases.1", "zwei>drei")));
		assertEquals(Set.of("eins", "zwei", "drei"), connect.vaultBuckets());
		assertInfo(null,   AnItem.none);
		assertInfo("eins", AnItem.eins);
		assertInfo("drei", AnItem.zwei);
		assertInfo("zwei", AnItem.drei);

		newItem();
		//noinspection resource
		final VaultMockService einsS = service("eins");
		//noinspection resource
		final VaultMockService zweiS = service("zwei");
		//noinspection resource
		final VaultMockService dreiS = service("drei");
		assertEquals("einsEx", einsS.serviceProperties.example);
		assertEquals("zweiEx", zweiS.serviceProperties.example);
		assertEquals("dreiEx", dreiS.serviceProperties.example);
		einsS.assertIt(HASH_EINS, "010203", "putBytes");
		zweiS.assertIt(HASH_DREI, "030405", "putBytes");
		dreiS.assertIt(HASH_ZWEI, "020304", "putBytes");
	}

	@Test void toNonExisting()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins zwei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.aliases.count", 1),
						single("vault.aliases.0", "drei>vier")),
				IllegalPropertiesException.class,
				"property vault.aliases.0 in DESC " +
				"'vier' does not exist, use one of [eins, zwei]");
	}

	@Test void toItself()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins zwei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.aliases.count", 1),
						single("vault.aliases.0", "drei>drei")),
				IllegalPropertiesException.class,
				"property vault.aliases.0 in DESC " +
				"'drei' cannot be an alias of itself");
	}

	@Test void transitively()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.aliases.count", 2),
						single("vault.aliases.0", "zwei>eins"),
						single("vault.aliases.1", "drei>zwei")),
				IllegalPropertiesException.class,
				"property vault.aliases.1 in DESC " +
				"'zwei' does not exist, use one of [eins]");
	}

	@Test void twice()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins zwei drei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.drei.service", VaultMockService.class),
						single("vault.aliases.count", 2),
						single("vault.aliases.0", "eins>zwei"),
						single("vault.aliases.1", "eins>drei")),
				IllegalPropertiesException.class,
				"property vault.aliases.1 in DESC " +
				"'eins' is an alias of 'zwei' already, cannot be an alias of 'drei' as well");
	}

	@Test void brokenLeft()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins zwei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.aliases.count", 1),
						single("vault.aliases.0", "drei.>zwei")),
				IllegalPropertiesException.class,
				"property vault.aliases.0 in DESC " +
				"left must contain just [---,0-9,A-Z,a-z], " +
				"but was >drei.< containing a forbidden character at position 4");
	}

	@Test void brokenRight()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins zwei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.aliases.count", 1),
						single("vault.aliases.0", "drei>zwei.")),
				IllegalPropertiesException.class,
				"property vault.aliases.0 in DESC " +
				"right must contain just [---,0-9,A-Z,a-z], " +
				"but was >zwei.< containing a forbidden character at position 4");
	}

	@Test void brokenAlias()
	{
		assertFails(
				() -> props(
						single("vault.buckets", "eins zwei"),
						single("vault.eins.service", VaultMockService.class),
						single("vault.zwei.service", VaultMockService.class),
						single("vault.aliases.count", 1),
						single("vault.aliases.0", "drei/zwei")),
				IllegalPropertiesException.class,
				"property vault.aliases.0 in DESC " +
				"must contain exactly one '>', but was 'drei/zwei'");
	}

	private static ConnectProperties props(final Source... sources)
	{
		final ArrayList<Source> allSources = new ArrayList<>();
		allSources.add(single("vault", true));
		allSources.addAll(List.of(sources));
		allSources.add(TestSources.minimal());
		final var result = ConnectProperties.create(describe("DESC", cascade(allSources.toArray(new Source[0]))));
		assertEquals(Set.of(), result.getOrphanedKeys(), "orphanedKeys");
		return result;
	}

	private void connect(final ConnectProperties props)
	{
		MODEL.connect(props);
		MODEL.createSchema();
		connect = MODEL.connect();
	}

	private Connect connect;

	private VaultMockService service(final String bucket)
	{
		return (VaultMockService)deresiliate(connect.vault(bucket).service);
	}

	private static void assertInfo(final String bucket, final DataField field)
	{
		assertEquals(bucket, field.getVaultBucket(), "bucket");
		if(bucket==null)
			assertEquals(null, field.getVaultInfo(), "bucket info");
		else
			assertEquals(bucket, field.getVaultInfo().getBucket(), "bucket info");
	}

	private static void newItem()
	{
		MODEL.startTransaction("new AnItem");
		new AnItem(
				DataField.toValue(new byte[]{0,1,2}),
				DataField.toValue(new byte[]{1,2,3}),
				DataField.toValue(new byte[]{2,3,4}),
				DataField.toValue(new byte[]{3,4,5}));
		MODEL.rollback();
	}

	private static final String HASH_EINS = "27864cc5219a951a7a6e52b8c8dddf6981d098da1658d96258c870b2c88dfbcb51841aea172a28bafa6a79731165584677066045c959ed0f9929688d04defc29";
	private static final String HASH_ZWEI = "31c5ea6cb50f6df3e1110e08bc3fbe3f5e02ee959e2aa6d2106c6b30429dd0b6e183db5aa35973b1998a534956c78a8b117239fd39f63f1256d867f11cb9a073";
	private static final String HASH_DREI = "4e0dc0ea63dc55fa356caf1ae439592b02e2d85439d801159a3df41ae815ab25a0f71feba022a121a03e070ec8634828a8d7501fe3b53a0689a760a6fedb5693";

	@AfterEach void tearDown()
	{
		if(MODEL.isConnected())
		{
			MODEL.rollbackIfNotCommitted();
			MODEL.tearDownSchema();
			MODEL.disconnect();
		}
		connect = null;
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		static final DataField none = new DataField();

		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		@Vault("eins")
		static final DataField eins = new DataField();

		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		@Vault("zwei")
		static final DataField zwei = new DataField();

		@Wrapper(wrap=ALL_WRAPS, visibility=NONE)
		@Vault("drei")
		static final DataField drei = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value none,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value eins,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value zwei,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value drei)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.none,none),
				com.exedio.cope.SetValue.map(AnItem.eins,eins),
				com.exedio.cope.SetValue.map(AnItem.zwei,zwei),
				com.exedio.cope.SetValue.map(AnItem.drei,drei),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
