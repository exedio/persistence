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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Model;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.util.EmptyJobContext;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.util.TimeZoneStrict;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class PurgePropertiesTest extends ConnectedTest
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(clockRule);

	@Test public void testPurge() throws ParseException
	{
		samplerModel.createSchema();

		PurgeProperties.transaction.setValue(10);
		PurgeProperties.itemCache.setValue(11);
		PurgeProperties.clusterNode.setValue(12);
		PurgeProperties.media.setValue(13);
		PurgeProperties.model.setValue(16);
		final SamplerProperties props = PurgeProperties.initProperties(model);

		final MC mc = new MC();
		final String time = "12:34:56.789";
		clockRule.override(clock);
		final SimpleDateFormat result = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		result.setTimeZone(TimeZoneStrict.getTimeZone("UTC"));
		clock.add(result.parse("1987/08/20 " + time));
		// for SamplerPurge instances
		for(int i = 0; i<5; i++)
			clock.add(result.parse("1999/09/09 " + time));
		props.purge(sampler, mc);
		clock.assertEmpty();
		assertEquals(
				"purge select this from SamplerTransaction where date<'1987/08/10 " + time + "'\n"+
				"purge select this from SamplerItemCache where date<'1987/08/09 " + time + "'\n"+
				"purge select this from SamplerClusterNode where date<'1987/08/08 " + time + "'\n"+
				"purge select this from SamplerMedia where date<'1987/08/07 " + time + "'\n"+
				"purge select this from SamplerModel where date<'1987/08/04 " + time + "'\n",
				mc.getMessages());
	}

	@Test public void modelDependingLimit()
	{
		samplerModel.createSchema();

		PurgeProperties.model.setValue(4);
		PurgeProperties.itemCache.setValue(3);
		try
		{
			PurgeProperties.initProperties(model);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property purgeDays.transaction in desc1 / desc2 " +   // TODO wrong key
					"must not be larger than property purgeDays.model, but was 10 which is larger than 4",
					e.getMessage());
		}
	}



	private static enum PurgeProperties
	{
		model,
		transaction,
		itemCache,
		clusterNode,
		media;

		private final String prefix = "purgeDays.";
		private int value = 8;
		private boolean matches(final String key)
		{
			return key.equals(prefix+name());
		}

		void setValue(final int value)
		{
			this.value=value;
		}

		static String getValue(final String key)
		{
			for (final PurgeProperties p : values())
			{
				if (p.matches(key))
				{
					return Integer.toString(p.value);
				}
			}
			return null;
		}

		@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
		static SamplerProperties initProperties(final Model model)
		{
			final Source sou = model.getConnectProperties().getSourceObject();
			final java.util.Properties properties = new java.util.Properties();
			return SamplerProperties.factory().create(
					Sources.cascade(
						Sources.view(properties, "desc1"),
						new Source()
						{
							@Override
							public String get(final String key)
							{
								final String connect_prefix = "cope.";
								if (key.startsWith(connect_prefix))
									return sou.get(key.substring(connect_prefix.length()));
								else
									return PurgeProperties.getValue(key);
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
					)
				);
		}

	}

	private static class MC extends EmptyJobContext
	{
		private final StringBuilder bf = new StringBuilder();

		String getMessages()
		{
			return bf.toString();
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
			bf.append(message).append('\n');
		}
	}
}
