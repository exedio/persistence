/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.exedio.cope.junit.CopeAssert;

public class MediaBase64Test extends CopeAssert
{
	public void testIt()
	{
		assertIt(   "",   0);
		assertIt(  "B",   1);
		assertIt(  "C",   2);
		assertIt(  "9",  61);
		assertIt(  "-",  62);
		assertIt(  "_",  63);
		assertIt( "AB",  64);
		assertIt( "BB",  65);
		assertIt( "CB",  66);
		assertIt( "DB",  67);

		assertIt( ".B",   -1);
		assertIt( ".C",   -2);
		assertIt( ".-",  -62);
		assertIt( ".DB", -67);

		assertIt( "8_________H", Long.MAX_VALUE - 3);
		assertIt( "9_________H", Long.MAX_VALUE - 2);
		assertIt( "-_________H", Long.MAX_VALUE - 1);
		assertIt( "__________H", Long.MAX_VALUE    );

		assertIt( ".9_________H", Long.MIN_VALUE + 3);
		assertIt( ".-_________H", Long.MIN_VALUE + 2);
		assertIt( ".__________H", Long.MIN_VALUE + 1);
	}

	private static void assertIt(final String expected, final long actual)
	{
		final StringBuilder bf = new StringBuilder();
		MediaBase64.append(bf, actual);
		assertEquals(expected, bf.toString());
	}

	public void testShow() throws ParseException
	{
		final Date d = new Date();
		{
			final StringBuilder bf = new StringBuilder();
			MediaPath.appendFingerprintSegment(bf, d.getTime());
			print(bf.toString());
		}
		print(".f" + d.getTime() + "/");

		{
			final long millisOf40years = 1000l * 60 * 60 * 24 * 365 * 40;
			System.out.println("----- " + millisOf40years);
			final StringBuilder bf = new StringBuilder();
			MediaPath.appendFingerprintSegment(bf, d.getTime()-millisOf40years);
			print(bf.toString());
			print(".f" + (d.getTime()-millisOf40years) + "/");
		}
		{
			final long millisOf40years = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").parse("2010/01/01 00:00:00.000").getTime();
			System.out.println("----- " + millisOf40years);
			final StringBuilder bf = new StringBuilder();
			MediaPath.appendFingerprintSegment(bf, d.getTime()-millisOf40years);
			print(bf.toString());
			print(".f" + (d.getTime()-millisOf40years) + "/");
		}
		{
			final long millisOf43years = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").parse("2013/01/01 00:00:00.000").getTime();
			System.out.println("----- " + millisOf43years);
			final StringBuilder bf = new StringBuilder();
			MediaPath.appendFingerprintSegment(bf, d.getTime()-millisOf43years);
			print(bf.toString());
			print(".f" + (d.getTime()-millisOf43years) + "/");
		}
	}

	private static void print(final String s)
	{
		System.out.println("------- " + s + " / " + s.length() + " / " + (s.length()-3));
	}
}
