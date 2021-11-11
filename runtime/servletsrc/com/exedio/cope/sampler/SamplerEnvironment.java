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

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.sampler.Util.maC;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DateField;
import com.exedio.cope.EnvironmentInfo;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Transaction;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

final class SamplerEnvironment extends Item
{
	private static final DateField connectDate    = new DateField().toFinal().unique();
	private static final DateField initializeDate = new DateField().toFinal();
	@SuppressWarnings("unused")
	private static final DateField sampleDate     = new DateField().toFinal().defaultToNow();

	static void sample(final Model model, final String buildTag)
	{
		final Date connectDate = model.getConnectDate();
		if(connectDate==null || forConnectDate(connectDate)!=null)
			return;

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		sv.add(map(SamplerEnvironment.connectDate, connectDate));
		sv.add(map(initializeDate, model.getInitializeDate()));
		addHostname(sv);
		addConnection(sv, model);
		addEnvironmentInfo(sv, model);
		addBuildTag(sv, buildTag);
		TYPE.newItem(sv);

		addFromRevisions(model);
	}

	private static SamplerEnvironment forConnectDate(final Date connectDate)
	{
		return TYPE.searchSingleton(SamplerEnvironment.connectDate.equal(connectDate));
	}


	private static final StringField hostname = new StringField().toFinal().optional().lengthMax(1000);

	private static void addHostname(final ArrayList<SetValue<?>> sv)
	{
		try
		{
			sv.add(map(hostname, InetAddress.getLocalHost().getHostName()));
		}
		catch(final UnknownHostException ignored)
		{
			// do not put in hostname
		}
	}

	private static void addHostname(final ArrayList<SetValue<?>> sv, final Map<String, String> environment)
	{
		sv.add(map(hostname, getS(environment, "hostname")));
	}


	private static final StringField connectionUrl      = new StringField().toFinal().lengthMax(1000);
	private static final StringField connectionUsername = new StringField().toFinal().lengthMax(1000);

	private static void addConnection(final ArrayList<SetValue<?>> sv, final Model model)
	{
		final ConnectProperties p = model.getConnectProperties();
		sv.add(map(connectionUrl     , p.getConnectionUrl     ()));
		sv.add(map(connectionUsername, p.getConnectionUsername()));
	}

	private static void addConnection(final ArrayList<SetValue<?>> sv, final Map<String, String> environment)
	{
		sv.add(map(connectionUrl     , getS(environment, "connection.url",  "jdbc.url" )));
		sv.add(map(connectionUsername, getS(environment, "connection.user", "jdbc.user")));
	}


	private static final  StringField databaseProductName    = new StringField ().toFinal().lengthMax(1000);
	private static final  StringField databaseProductVersion = new StringField ().toFinal().lengthMax(1000);
	private static final IntegerField databaseVersionMajor   = new IntegerField().toFinal();
	private static final IntegerField databaseVersionMinor   = new IntegerField().toFinal();
	private static final  StringField driverName             = new StringField ().toFinal().lengthMax(1000);
	private static final  StringField driverVersion          = new StringField ().toFinal().lengthMax(1000);
	private static final IntegerField driverVersionMajor     = new IntegerField().toFinal();
	private static final IntegerField driverVersionMinor     = new IntegerField().toFinal();

	private static void addEnvironmentInfo(final ArrayList<SetValue<?>> sv, final Model model)
	{
		final EnvironmentInfo i = model.getEnvironmentInfo();
		sv.add(map(databaseProductName   , i.getDatabaseProductName   ()));
		sv.add(map(databaseProductVersion, i.getDatabaseProductVersion()));
		sv.add(map(databaseVersionMajor  , i.getDatabaseMajorVersion  ()));
		sv.add(map(databaseVersionMinor  , i.getDatabaseMinorVersion  ()));
		sv.add(map(driverName            , i.getDriverName            ()));
		sv.add(map(driverVersion         , i.getDriverVersion         ()));
		sv.add(map(driverVersionMajor    , i.getDriverMajorVersion    ()));
		sv.add(map(driverVersionMinor    , i.getDriverMinorVersion    ()));
	}

	private static void addEnvironmentInfo(final ArrayList<SetValue<?>> sv, final Map<String, String> environment)
	{
		sv.add(map(databaseProductName   , getS(environment, "database.name")));
		sv.add(map(databaseProductVersion, getS(environment, "database.version")));
		sv.add(map(databaseVersionMajor  , getI(environment, "database.version.major")));
		sv.add(map(databaseVersionMinor  , getI(environment, "database.version.minor")));
		sv.add(map(driverName            , getS(environment, "driver.name")));
		sv.add(map(driverVersion         , getS(environment, "driver.version")));
		sv.add(map(driverVersionMajor    , getI(environment, "driver.version.major")));
		sv.add(map(driverVersionMinor    , getI(environment, "driver.version.minor")));
	}


	private static final StringField buildTag = new StringField().toFinal().optional().lengthMax(1000);

	private static void addBuildTag(final ArrayList<SetValue<?>> sv, final String buildTag)
	{
		sv.add(maC(SamplerEnvironment.buildTag, buildTag));
	}


	private static void addFromRevisions(final Model model)
	{
		if(model.getRevisions()==null)
			return;

		final Map<Integer, byte[]> logs;
		if(model.hasCurrentTransaction())
		{
			final Transaction tx = model.leaveTransaction(); // TODO do not leave transaction
			try
			{
				logs = model.getRevisionLogs();
			}
			finally
			{
				model.joinTransaction(tx);
			}
		}
		else
		{
			logs = model.getRevisionLogs();
		}

		final TreeMap<Date, ArrayList<RevisionInfo>> revisions = new TreeMap<>();
		for(final Map.Entry<Integer, byte[]> entry : logs.entrySet())
		{
			final RevisionInfo info = RevisionInfo.read(entry.getValue());
			if(info!=null)
			{
				final Date date = info.getDate();
				if(forConnectDate(date)!=null)
					continue;

				revisions.computeIfAbsent(date, k -> new ArrayList<>()).add(info);
			}
		}

		for(final ArrayList<RevisionInfo> list : revisions.values())
		{
			final RevisionInfo info = list.get(0);
			final Date date = info.getDate();
			final Map<String, String> environment = info.getEnvironment();

			final ArrayList<SetValue<?>> sv = new ArrayList<>();
			sv.add(map(connectDate, date));
			sv.add(map(initializeDate, date));
			addHostname(sv, environment);
			addConnection(sv, environment);
			addEnvironmentInfo(sv, environment);
			addBuildTag(sv, RevisionInfo.class.getName() + ' ' + info.getNumber() + '-' + list.get(list.size()-1).getNumber());
			TYPE.newItem(sv);
		}
	}

	private static String getS(final Map<String, String> environment, final String key)
	{
		return environment.get(key);
	}

	private static String getS(final Map<String, String> environment, final String key, final String deprecatedKey)
	{
		if(key.equals(deprecatedKey))
			throw new IllegalArgumentException(key);

		final String value = environment.get(key);
		if(value!=null)
			return value;

		return environment.get(deprecatedKey);
	}

	private static int getI(final Map<String, String> environment, final String key)
	{
		return Integer.parseInt(getS(environment, key));
	}


	private SamplerEnvironment(final ActivationParameters ap){ super(ap); }
	private static final long serialVersionUID = 1l;
	static final Type<SamplerEnvironment> TYPE = TypesBound.newType(SamplerEnvironment.class, SamplerEnvironment::new);
}
