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

import static com.exedio.cope.ConnectPropertiesTest.HSQLDB_PROBE;
import static com.exedio.cope.ConnectPropertiesTest.assertIt;
import static com.exedio.cope.ConnectPropertiesTest.getProbeTest;
import static com.exedio.cope.ConnectPropertiesTest.probe;
import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator")
public class VaultMultiTest
{
	static final String HASH1 = "01ee706ed305c3fd0a5b1bdadadaa99424f67a73291419ac69729cc54ee55b9dd47b6743dbe88be50034bda145345adc7e2a339871aed2727e87e204560dd500";
	static final String HASH2 = "e3f5fea91629ce0c9a4fec0b8fb40db561a4cd63b3d3e43212e6013e94984eb6881efafa8a46ca48b8ee0dea560d5b5404912b985a2082abd1e0f7dbcd317732";
	static final String HASH3 = "33db6319f46b410fe19df7a5b5323e5b2865c2bacc093d2258b445fb42ddfdbaf84a12e56e95388cd285bfffe50dfa8eea17855b7e1852a185011b1810e00be0";
	static final String HASH4 = "c665cb3dd08b32c85e6d50149ea3c46ac9f56878f4965f85c1e40d535d980842d591a25d5ad232eedfed6f1d32b2ae950efe2957cdd93ea2b9c5fe794b113608";
	static final String HASH5 = "52b8c77bebdef6f008784916e726b1da073cf5fc826f5f442d2cf7e868b1b0c9197dc2146b80faaf292f0898abb3f41687c270d68537cd6b2584651269869fde";
	static final String VALUE1 = "001122";
	static final String VALUE2 = "223344";
	static final String VALUE3 = "445566";
	static final String VALUE4 = "aabbcc";
	static final String VALUE5 = "ccddee";

	private VaultMockService serviceDefault;
	private VaultMockService serviceAlpha;
	private VaultMockService serviceBeta;

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.services", "default alpha beta"),
				single("vault.service.default", VaultMockService.class),
				single("vault.service.default.example", "defaultEx"),
				single("vault.service.default.probe.result", "probeResultDefault"),
				single("vault.service.alpha", VaultMockService.class),
				single("vault.service.alpha.example", "alphaEx"),
				single("vault.service.alpha.probe.result", "probeResultAlpha"),
				single("vault.service.beta", VaultMockService.class),
				single("vault.service.beta.example", "betaEx"),
				single("vault.service.beta.probe.result", "probeResultBeta"),
				TestSources.minimal()
		)));
		final Map<String, VaultService> vaults = MODEL.connect().vaults;
		serviceDefault = (VaultMockService)vaults.get("default");
		serviceAlpha   = (VaultMockService)vaults.get("alpha");
		serviceBeta    = (VaultMockService)vaults.get("beta");
	}

	@Test void testInfo()
	{
		final Map<String, VaultService> vaults = MODEL.connect().vaults;
		assertEquals(asList("default", "alpha", "beta"), new ArrayList<>(vaults.keySet()));
		assertEquals("defaultEx", serviceDefault.serviceProperties.example);
		assertEquals("alphaEx",   serviceAlpha  .serviceProperties.example);
		assertEquals("betaEx",    serviceBeta   .serviceProperties.example);
		assertEquals(Vault.DEFAULT, serviceDefault.serviceKey);
		assertEquals("alpha",       serviceAlpha  .serviceKey);
		assertEquals("beta",        serviceBeta   .serviceKey);
		assertEquals(null,                         AnItem.none   .getVaultInfo());
		assertEquals(Vault.DEFAULT,                AnItem.defaulT.getVaultInfo().getServiceKey());
		assertEquals("alpha",                      AnItem.alpha  .getVaultInfo().getServiceKey());
		assertEquals("beta",                       AnItem.beta1  .getVaultInfo().getServiceKey());
		assertEquals("beta",                       AnItem.beta2  .getVaultInfo().getServiceKey());
		assertEquals("VaultMockService:defaultEx", AnItem.defaulT.getVaultInfo().getService());
		assertEquals("VaultMockService:alphaEx",   AnItem.alpha  .getVaultInfo().getService());
		assertEquals("VaultMockService:betaEx",    AnItem.beta1  .getVaultInfo().getService());
		assertEquals("VaultMockService:betaEx",    AnItem.beta2  .getVaultInfo().getService());
	}

	@Test void testProbe() throws Exception
	{
		final String VAULT = "[VaultMockService:defaultEx, VaultMockService:alphaEx, VaultMockService:betaEx]";
		final ConnectProperties p = MODEL.getConnectProperties();
		final Iterator<? extends Callable<?>> probes = p.getProbes().iterator();
		assertIt("Connect", HSQLDB_PROBE, EnvironmentInfo.class, probes.next());
		assertIt("vault.default", "VaultMockService:defaultEx",      String.class, probes.next());
		assertIt("vault.alpha",   "VaultMockService:alphaEx",        String.class, probes.next());
		assertIt("vault.beta",    "VaultMockService:betaEx",         String.class, probes.next());
		assertIt("vault.service.default.Mock", "probeResultDefault", String.class, probes.next());
		assertIt("vault.service.alpha.Mock",   "probeResultAlpha",   String.class, probes.next());
		assertIt("vault.service.beta.Mock",    "probeResultBeta",    String.class, probes.next());
		assertFalse(probes.hasNext());

		assertEquals(HSQLDB_PROBE + " " + VAULT, probe(p));
		assertIt("probe", HSQLDB_PROBE + " " + VAULT, String.class, getProbeTest(p));
	}

	@Test void testDifferent()
	{
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction("VaultTest");
		final AnItem i = new AnItem(VALUE1, VALUE2, VALUE3, VALUE4, VALUE5);

		serviceDefault.assertIt(HASH2, VALUE2,
				"putBytes AnItem.defaulT " + i + "\n");
		serviceAlpha.assertIt(HASH3, VALUE3,
				"putBytes AnItem.alpha " + i + "\n");
		serviceBeta.assertIt(HASH4, VALUE4, HASH5, VALUE5,
				"putBytes AnItem.beta1 " + i + "\n" +
				"putBytes AnItem.beta2 " + i + "\n");

		assertEquals(VALUE1, encodeLower(i.getNoneArray()));
		assertEquals(VALUE2, encodeLower(i.getDefaulTArray()));
		assertEquals(VALUE3, encodeLower(i.getAlphaArray()));
		assertEquals(VALUE4, encodeLower(i.getBeta1Array()));
		assertEquals(VALUE5, encodeLower(i.getBeta2Array()));

		serviceDefault.assertIt(HASH2, VALUE2,
				"getBytes\n");
		serviceAlpha.assertIt(HASH3, VALUE3,
				"getBytes\n");
		serviceBeta.assertIt(HASH4, VALUE4, HASH5, VALUE5,
				"getBytes\n" +
				"getBytes\n");
	}

	@Test void testSame()
	{
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction("VaultTest");
		final AnItem i = new AnItem(VALUE1, VALUE1, VALUE1, VALUE1, VALUE1);

		serviceDefault.assertIt(HASH1, VALUE1,
				"putBytes AnItem.defaulT " + i + "\n");
		serviceAlpha.assertIt(HASH1, VALUE1,
				"putBytes AnItem.alpha " + i + "\n");
		serviceBeta.assertIt(HASH1, VALUE1,
				"putBytes AnItem.beta1 " + i + "\n" +
				"putBytes AnItem.beta2 " + i + "\n");

		assertEquals(VALUE1, encodeLower(i.getNoneArray()));
		assertEquals(VALUE1, encodeLower(i.getDefaulTArray()));
		assertEquals(VALUE1, encodeLower(i.getAlphaArray()));
		assertEquals(VALUE1, encodeLower(i.getBeta1Array()));
		assertEquals(VALUE1, encodeLower(i.getBeta2Array()));

		serviceDefault.assertIt(HASH1, VALUE1,
				"getBytes\n");
		serviceAlpha.assertIt(HASH1, VALUE1,
				"getBytes\n");
		serviceBeta.assertIt(HASH1, VALUE1,
				"getBytes\n" +
				"getBytes\n");
	}

	@AfterEach void tearDown()
	{
		if(MODEL.isConnected())
		{
			MODEL.rollbackIfNotCommitted();
			MODEL.tearDownSchema();
			MODEL.disconnect();
		}
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		@Wrapper(wrap="getArray", visibility=DEFAULT)
		static final DataField none = new DataField();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		@Wrapper(wrap="getArray", visibility=DEFAULT)
		@Vault
		static final DataField defaulT = new DataField();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		@Wrapper(wrap="getArray", visibility=DEFAULT)
		@Vault("alpha")
		static final DataField alpha = new DataField();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		@Wrapper(wrap="getArray", visibility=DEFAULT)
		@Vault("beta")
		static final DataField beta1 = new DataField();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		@Wrapper(wrap="getArray", visibility=DEFAULT)
		@Vault("beta")
		static final DataField beta2 = new DataField();

		AnItem(
				@javax.annotation.Nonnull final String none,
				@javax.annotation.Nonnull final String defaulT,
				@javax.annotation.Nonnull final String alpha,
				@javax.annotation.Nonnull final String beta1,
				@javax.annotation.Nonnull final String beta2)
		{
			this(
					toValue(decodeLower(none)),
					toValue(decodeLower(defaulT)),
					toValue(decodeLower(alpha)),
					toValue(decodeLower(beta1)),
					toValue(decodeLower(beta2)));
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value none,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value defaulT,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value alpha,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value beta1,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value beta2)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.none.map(none),
				AnItem.defaulT.map(defaulT),
				AnItem.alpha.map(alpha),
				AnItem.beta1.map(beta1),
				AnItem.beta2.map(beta2),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		byte[] getNoneArray()
		{
			return AnItem.none.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		byte[] getDefaulTArray()
		{
			return AnItem.defaulT.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		byte[] getAlphaArray()
		{
			return AnItem.alpha.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		byte[] getBeta1Array()
		{
			return AnItem.beta1.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		byte[] getBeta2Array()
		{
			return AnItem.beta2.getArray(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
