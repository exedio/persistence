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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Stuff.sampler;
import static com.exedio.cope.sampler.Stuff.samplerModel;
import static java.time.Duration.ofDays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.EmptyJobContext;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Factory;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.util.TimeZoneStrict;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;

@MainRule.Tag
@SuppressWarnings("HardcodedLineSeparator")
public class PurgePropertiesTest extends ConnectedTest
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@Test void testPurge() throws ParseException
	{
		samplerModel.createSchema();

		final EnumMap<PurgedType, Integer> days = new EnumMap<>(PurgedType.class);
		days.put(PurgedType.transaction, 10);
		days.put(PurgedType.itemCache, 11);
		days.put(PurgedType.clusterNode, 12);
		days.put(PurgedType.media, 13);
		days.put(PurgedType.model, 16);
		final SamplerProperties props = factory.create(newSource(true, days));

		final MC mc = new MC();
		final String time = "12:34:56.789";
		clockRule.override(clock);
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));
		clock.add(result.parse("1987-08-20 " + time));
		// for SamplerPurge instances
		for(int i = 0; i<5; i++)
			clock.add(result.parse("1999-09-09 " + time));
		props.purge(sampler, mc);
		clock.assertEmpty();
		assertEquals(
				"purge select this from SamplerTransaction where date<'1987-08-10 " + time + "'\n"+
				"purge select this from SamplerItemCache where date<'1987-08-09 " + time + "'\n"+
				"purge select this from SamplerClusterNode where date<'1987-08-08 " + time + "'\n"+
				"purge select this from SamplerMedia where date<'1987-08-07 " + time + "'\n"+
				"purge select this from SamplerModel where date<'1987-08-04 " + time + "'\n",
				mc.getMessages());
	}

	@Test void testPurgeMinimum() throws ParseException
	{
		samplerModel.createSchema();

		final EnumMap<PurgedType, Integer> days = new EnumMap<>(PurgedType.class);
		days.put(PurgedType.transaction, 1);
		days.put(PurgedType.itemCache, 1);
		days.put(PurgedType.clusterNode, 1);
		days.put(PurgedType.media, 1);
		days.put(PurgedType.model, 1);
		final SamplerProperties props = factory.create(newSource(true, days));

		final MC mc = new MC();
		final String time = "12:34:56.789";
		clockRule.override(clock);
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));
		clock.add(result.parse("1987-08-20 " + time));
		// for SamplerPurge instances
		for(int i = 0; i<5; i++)
			clock.add(result.parse("1999-09-09 " + time));
		props.purge(sampler, mc);
		clock.assertEmpty();
		assertEquals(
				"purge select this from SamplerTransaction where date<'1987-08-19 " + time + "'\n"+
				"purge select this from SamplerItemCache where date<'1987-08-19 " + time + "'\n"+
				"purge select this from SamplerClusterNode where date<'1987-08-19 " + time + "'\n"+
				"purge select this from SamplerMedia where date<'1987-08-19 " + time + "'\n"+
				"purge select this from SamplerModel where date<'1987-08-19 " + time + "'\n",
				mc.getMessages());
	}

	@Test void testPurgeDisabled()
	{
		samplerModel.createSchema();

		final SamplerProperties props = factory.create(newSource(false, new EnumMap<>(PurgedType.class)));
		assertEquals(null, props.purge);

		final MC mc = new MC();
		clockRule.override(clock);
		props.purge(sampler, mc);
		clock.assertEmpty();
		assertEquals("", mc.getMessages());
	}

	@Test void testDefaults()
	{
		samplerModel.createSchema();

		final SamplerProperties props = factory.create(newSource(
				null, new EnumMap<>(PurgedType.class)));
		assertEquals(ofDays(57), props.purge.model);
		assertEquals(ofDays(57), props.purge.transaction);
		assertEquals(ofDays( 8), props.purge.itemCache);
		assertEquals(ofDays(29), props.purge.clusterNode);
		assertEquals(ofDays(29), props.purge.media);
	}

	@Test void testTooSmallTransaction()
	{
		samplerModel.createSchema();

		final EnumMap<PurgedType, Integer> days = new EnumMap<>(PurgedType.class);
		days.put(PurgedType.model, 500);
		days.put(PurgedType.transaction, 501);
		final Source source = newSource(null, days);
		try
		{
			factory.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property purge.transaction in desc1 / desc2 " +
					"must be a duration between P1D and P500D, " +
					"but was P501D",
					e.getMessage());
		}
	}

	@Test void testTooSmallItemCache()
	{
		samplerModel.createSchema();

		final EnumMap<PurgedType, Integer> days = new EnumMap<>(PurgedType.class);
		days.put(PurgedType.model, 500);
		days.put(PurgedType.itemCache, 501);
		final Source source = newSource(null, days);
		try
		{
			factory.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property purge.itemCache in desc1 / desc2 " +
					"must be a duration between P1D and P500D, " +
					"but was P501D",
					e.getMessage());
		}
	}

	@Test void testTooSmallClusterNode()
	{
		samplerModel.createSchema();

		final EnumMap<PurgedType, Integer> days = new EnumMap<>(PurgedType.class);
		days.put(PurgedType.model, 500);
		days.put(PurgedType.clusterNode, 501);
		final Source source = newSource(null, days);
		try
		{
			factory.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property purge.clusterNode in desc1 / desc2 " +
					"must be a duration between P1D and P500D, " +
					"but was P501D",
					e.getMessage());
		}
	}

	@Test void testTooSmallMedia()
	{
		samplerModel.createSchema();

		final EnumMap<PurgedType, Integer> days = new EnumMap<>(PurgedType.class);
		days.put(PurgedType.model, 500);
		days.put(PurgedType.media, 501);
		final Source source = newSource(null, days);
		try
		{
			factory.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property purge.media in desc1 / desc2 " +
					"must be a duration between P1D and P500D, " +
					"but was P501D",
					e.getMessage());
		}
	}



	private enum PurgedType
	{
		model,
		transaction,
		itemCache,
		clusterNode,
		media
	}

	private final Factory<SamplerProperties> factory = SamplerProperties.factory();

	Source newSource(final Boolean enabled, final EnumMap<PurgedType, Integer> days)
	{
		final Source sou = model.getConnectProperties().getSourceObject();

		final java.util.Properties properties = new java.util.Properties();
		if(enabled!=null)
			properties.setProperty("purge", String.valueOf(enabled));
		for(final Map.Entry<PurgedType, Integer> e : days.entrySet())
			properties.setProperty("purge." + e.getKey().name(), ofDays(e.getValue()).toString());

		return
				Sources.cascade(
					Sources.view(properties, "desc1"),
					new Source()
					{
						@Override
						public String get(final String key)
						{
							final String prefix = "cope.";
							return
								key.startsWith(prefix)
								? sou.get(key.substring(prefix.length()))
								: null;
						}
						@Override
						public Collection<String> keySet()
						{
							throw new RuntimeException();
						}
						@Override
						public String getDescription()
						{
							return "desc2";
						}
					}
				);
	}

	private static class MC extends EmptyJobContext
	{
		private final StringBuilder sb = new StringBuilder();

		String getMessages()
		{
			return sb.toString();
		}

		MC()
		{
			// make non-private
		}

		@Override
		public boolean supportsMessage()
		{
			return true;
		}

		@Override
		public void setMessage(final String message)
		{
			sb.append(message).append('\n');
		}
	}
}
