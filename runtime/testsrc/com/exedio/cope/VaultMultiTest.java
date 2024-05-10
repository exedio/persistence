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
import static com.exedio.cope.ConnectPropertiesTest.assertMatches;
import static com.exedio.cope.ConnectPropertiesTest.assertRg;
import static com.exedio.cope.ConnectPropertiesTest.getProbeTest;
import static com.exedio.cope.ConnectPropertiesTest.probe;
import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.PrometheusMeterRegistrar.meter;
import static com.exedio.cope.PrometheusMeterRegistrar.tag;
import static com.exedio.cope.RuntimeAssert.probes;
import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Hex.decodeLower;
import static com.exedio.cope.util.Hex.encodeLower;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.vault.VaultPropertiesTest.deresiliate;
import static java.lang.Double.NaN;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vault.VaultResilientService;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vaultmock.VaultMockService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Tags;
import java.util.ArrayList;
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
				single("vault.buckets", "default alpha beta"),
				single("vault.default.service", VaultMockService.class),
				single("vault.default.service.example", "defaultEx"),
				single("vault.default.service.probe.result", "probeResultDefault"),
				single("vault.alpha.service", VaultMockService.class),
				single("vault.alpha.service.example", "alphaEx"),
				single("vault.alpha.service.probe.result", "probeResultAlpha"),
				single("vault.alpha.service.bucketTagAction", "alpha"),
				single("vault.beta.service", VaultMockService.class),
				single("vault.beta.service.example", "betaEx"),
				single("vault.beta.service.probe.result", "probeResultBeta"),
				single("vault.beta.service.bucketTagAction", "beta"),
				TestSources.minimal()
		)));
		final Map<String, VaultService> vaults = deresiliate(MODEL.connect().vaults);
		serviceDefault = (VaultMockService)vaults.get("default");
		serviceAlpha   = (VaultMockService)vaults.get("alpha");
		serviceBeta    = (VaultMockService)vaults.get("beta");
	}

	@Test void testInfo()
	{
		final Map<String, VaultResilientService> vaults = MODEL.connect().vaults;
		assertEquals(asList("default", "alpha", "beta"), new ArrayList<>(vaults.keySet()));
		assertEquals("defaultEx", serviceDefault.serviceProperties.example);
		assertEquals("alphaEx",   serviceAlpha  .serviceProperties.example);
		assertEquals("betaEx",    serviceBeta   .serviceProperties.example);
		assertEquals(Vault.DEFAULT, serviceDefault.bucket);
		assertEquals("alpha",       serviceAlpha  .bucket);
		assertEquals("beta",        serviceBeta   .bucket);
		assertEquals(null,                         AnItem.none   .getVaultBucket());
		assertEquals(Vault.DEFAULT,                AnItem.defaulT.getVaultBucket());
		assertEquals("alpha",                      AnItem.alpha  .getVaultBucket());
		assertEquals("beta",                       AnItem.beta1  .getVaultBucket());
		assertEquals("beta",                       AnItem.beta2  .getVaultBucket());
		assertEquals(null,                         AnItem.none   .getVaultInfo());
		assertEquals(Vault.DEFAULT,                AnItem.defaulT.getVaultInfo().getBucket());
		assertEquals("alpha",                      AnItem.alpha  .getVaultInfo().getBucket());
		assertEquals("beta",                       AnItem.beta1  .getVaultInfo().getBucket());
		assertEquals("beta",                       AnItem.beta2  .getVaultInfo().getBucket());
		assertEquals("VaultMockService:defaultEx", AnItem.defaulT.getVaultInfo().getService());
		assertEquals("VaultMockService:alphaEx",   AnItem.alpha  .getVaultInfo().getService());
		assertEquals("VaultMockService:betaEx",    AnItem.beta1  .getVaultInfo().getService());
		assertEquals("VaultMockService:betaEx",    AnItem.beta2  .getVaultInfo().getService());
	}

	@Test void testProbe() throws Exception
	{
		final String VAULT = "\\[" +
				"VaultMockService:defaultEx"+" [0-9a-f]{16}xx128, mock:default, " +
				"VaultMockService:alphaEx"  +" [0-9a-f]{16}xx128, mock:alpha, "   +
				"VaultMockService:betaEx"   +" [0-9a-f]{16}xx128, mock:beta]";
		final ConnectProperties p = MODEL.getConnectProperties();
		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"Connect",
				"vault.default.Contract",
				"vault.default.BucketTag",
				"vault.default.service.Mock",
				"vault.alpha.Contract",
				"vault.alpha.BucketTag",
				"vault.alpha.service.Mock",
				"vault.beta.Contract",
				"vault.beta.BucketTag",
				"vault.beta.service.Mock"),
				new ArrayList<>(probes.keySet()));
		assertIt("Connect", HSQLDB_PROBE, EnvironmentInfo.class, probes);
		assertRg("vault.default.Contract", "VaultMockService:defaultEx [0-9a-f]{16}xx128", probes);
		assertIt("vault.default.BucketTag", "mock:default",          String.class, probes);
		assertRg("vault.alpha.Contract",   "VaultMockService:alphaEx [0-9a-f]{16}xx128",   probes);
		assertIt("vault.alpha.BucketTag",   "mock:alpha",            String.class, probes);
		assertRg("vault.beta.Contract",    "VaultMockService:betaEx [0-9a-f]{16}xx128",    probes);
		assertIt("vault.beta.BucketTag",    "mock:beta",             String.class, probes);
		assertIt("vault.default.service.Mock", "probeResultDefault", String.class, probes);
		assertIt("vault.alpha.service.Mock",   "probeResultAlpha",   String.class, probes);
		assertIt("vault.beta.service.Mock",    "probeResultBeta",    String.class, probes);

		assertMatches(HSQLDB_PROBE + " " + VAULT, probe(p));
		assertRg("probe", HSQLDB_PROBE + " " + VAULT, getProbeTest(p));
	}

	@Test void testDifferent()
	{
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction("VaultTest");
		final AnItem i = new AnItem(VALUE1, VALUE2, VALUE3, VALUE4, VALUE5);

		serviceDefault.assertIt(HASH2, VALUE2,
				"putBytes\n");
		serviceAlpha.assertIt(HASH3, VALUE3,
				"putBytes\n");
		serviceBeta.assertIt(HASH4, VALUE4, HASH5, VALUE5,
				"putBytes\n" +
				"putBytes\n");

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
				"putBytes\n");
		serviceAlpha.assertIt(HASH1, VALUE1,
				"putBytes\n");
		serviceBeta.assertIt(HASH1, VALUE1,
				"putBytes\n" +
				"putBytes\n");

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

	@Test void testRequiredToMarkPut()
	{
		assertFails(
				() -> MODEL.isVaultRequiredToMarkPut(null),
				NullPointerException.class,
				"bucket");
		assertFails(
				() -> MODEL.isVaultRequiredToMarkPut(""),
				IllegalArgumentException.class,
				"bucket must not be empty");
		assertFails(
				() -> MODEL.isVaultRequiredToMarkPut("zack"),
				IllegalArgumentException.class,
				"bucket zack does not exist, use one of [default, alpha, beta]");
		assertFails(
				() -> MODEL.setVaultRequiredToMarkPut(null, false),
				NullPointerException.class,
				"bucket");
		assertFails(
				() -> MODEL.setVaultRequiredToMarkPut("", false),
				IllegalArgumentException.class,
				"bucket must not be empty");
		assertFails(
				() -> MODEL.setVaultRequiredToMarkPut("zack", false),
				IllegalArgumentException.class,
				"bucket zack does not exist, use one of [default, alpha, beta]");

		final Gauge gaugeDefault = (Gauge)meter(VaultService.class, "markPut", Tags.of("bucket", "default").and(tag(MODEL)));
		final Gauge gaugeAlpha   = (Gauge)meter(VaultService.class, "markPut", Tags.of("bucket", "alpha"  ).and(tag(MODEL)));
		final Gauge gaugeBeta    = (Gauge)meter(VaultService.class, "markPut", Tags.of("bucket", "beta"   ).and(tag(MODEL)));

		assertEquals(false, MODEL.isVaultRequiredToMarkPut("default"));
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("alpha"));
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("beta"));
		assertEquals(false, serviceDefault.requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceAlpha  .requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceBeta   .requiresToMarkPut.getAsBoolean());
		assertEquals(0, gaugeDefault.value());
		assertEquals(0, gaugeAlpha  .value());
		assertEquals(0, gaugeBeta   .value());

		MODEL.setVaultRequiredToMarkPut("default", true);
		assertEquals(true,  MODEL.isVaultRequiredToMarkPut("default"));
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("alpha"));
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("beta"));
		assertEquals(true,  serviceDefault.requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceAlpha  .requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceBeta   .requiresToMarkPut.getAsBoolean());
		assertEquals(1, gaugeDefault.value());
		assertEquals(0, gaugeAlpha  .value());
		assertEquals(0, gaugeBeta   .value());

		MODEL.setVaultRequiredToMarkPut("alpha", true);
		assertEquals(true,  MODEL.isVaultRequiredToMarkPut("default"));
		assertEquals(true,  MODEL.isVaultRequiredToMarkPut("alpha"));
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("beta"));
		assertEquals(true,  serviceDefault.requiresToMarkPut.getAsBoolean());
		assertEquals(true,  serviceAlpha  .requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceBeta   .requiresToMarkPut.getAsBoolean());
		assertEquals(1, gaugeDefault.value());
		assertEquals(1, gaugeAlpha  .value());
		assertEquals(0, gaugeBeta   .value());

		MODEL.setVaultRequiredToMarkPut("default", false);
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("default"));
		assertEquals(true,  MODEL.isVaultRequiredToMarkPut("alpha"));
		assertEquals(false, MODEL.isVaultRequiredToMarkPut("beta"));
		assertEquals(false, serviceDefault.requiresToMarkPut.getAsBoolean());
		assertEquals(true,  serviceAlpha  .requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceBeta   .requiresToMarkPut.getAsBoolean());
		assertEquals(0, gaugeDefault.value());
		assertEquals(1, gaugeAlpha  .value());
		assertEquals(0, gaugeBeta   .value());

		MODEL.disconnect();
		assertFails(
				() -> MODEL.isVaultRequiredToMarkPut("default"),
				Model.NotConnectedException.class,
				"model not connected, use Model#connect for "  + MODEL);
		assertFails(
				() -> MODEL.setVaultRequiredToMarkPut("default", false),
				Model.NotConnectedException.class,
				"model not connected, use Model#connect for "  + MODEL);
		assertEquals(false, serviceDefault.requiresToMarkPut.getAsBoolean());
		assertEquals(true,  serviceAlpha  .requiresToMarkPut.getAsBoolean());
		assertEquals(false, serviceBeta   .requiresToMarkPut.getAsBoolean());
		assertEquals(NaN, gaugeDefault.value());
		assertEquals(NaN, gaugeAlpha  .value());
		assertEquals(NaN, gaugeBeta   .value());
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
				com.exedio.cope.SetValue.map(AnItem.none,none),
				com.exedio.cope.SetValue.map(AnItem.defaulT,defaulT),
				com.exedio.cope.SetValue.map(AnItem.alpha,alpha),
				com.exedio.cope.SetValue.map(AnItem.beta1,beta1),
				com.exedio.cope.SetValue.map(AnItem.beta2,beta2),
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
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
