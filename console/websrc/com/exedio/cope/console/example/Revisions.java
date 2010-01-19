/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.Revision;
import com.exedio.cope.RevisionInfo;
import com.exedio.cope.RevisionInfoCreate;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.dsmf.SQLRuntimeException;

final class Revisions
{
	static final com.exedio.cope.Revisions revisions(final int length)
	{
		final Revision[] result = new Revision[length];
		
		int i = 0;
		int revision = length;
		result[i++] =
			new Revision(revision--,
					"not yet applied",
					"drop table \"Item\"");
		result[i++] =
			new Revision(revision--,
					"not yet applied2",
					"drop table \"Item\"");
		result[i++] =
			new Revision(revision--,
					"already applied together with its predecessor at the same time",
					"create table Mail( " +
					"this integer," +
					"created bigint, " +
					"body blob," +
					"toSend integer, " +
					"sentDate bigint," +
					"failedDate bigint, " +
					"exceptionStacktrace text character set utf8 binary, " +
					"constraint Mail_Pk primary key(this), " +
					"constraint Mail_this_CkPk check((this>=-2147483647) AND (this<=2147483647)), " +
					"constraint Mail_created_Ck check((created IS NOT NULL) AND ((created>=-9223372036854775808) AND (created<=9223372036854775807))), " +
					"constraint Mail_body_Ck check((LENGTH(body)<=100000) OR (body IS NULL)), " +
					"constraint Mail_toSend_Ck check((toSend IS NOT NULL) AND (toSend IN (0,1))), " +
					"constraint Mail_sentDate_Ck check(((sentDate>=-9223372036854775808) AND (sentDate<=9223372036854775807)) OR (sentDate IS NULL)), " +
					"constraint Mail_failedDate_Ck check(((failedDate>=-9223372036854775808) AND (failedDate<=9223372036854775807)) OR (failedDate IS NULL)), " +
					"constraint Mail_excepStack_Ck check((LENGTH(exceptionStacktrace)<=1500) OR (exceptionStacktrace IS NULL)))");
		result[i++] =
			new Revision(revision--, "with two sql statements",
					"alter table Article add column imageContentType varchar(61) character set utf8 binary",
					"update Article set imageContentType='image/jpeg' where image is not null");
		result[i++] =
			new Revision(revision--,
					"before change of environment",
					"drop table \"Item\"");
		
		for(; i<length; i++, revision--)
		{
			final String[] body = new String[(i%4) + 1];
			for(int j = 0; j<body.length; j++)
				body[j] = "sql " + revision + "/" + j;
			result[i] = new Revision(revision, "comment " + revision, body);
		}
		return new com.exedio.cope.Revisions(result);
	}
	
	static final void revisions(final Model model)
	{
		final java.util.Properties dbinfo = model.getDatabaseInfo();
		final HashMap<String, String> environment = new HashMap<String, String>();
		for(final Object key : dbinfo.keySet())
			environment.put((String)key, dbinfo.getProperty((String)key));
		
		final Iterator<Revision> revisions = model.getRevisions().getList().iterator();
		
		final ConnectProperties p = model.getConnectProperties();
		Connection con = null;
		PreparedStatement stat = null;
		try
		{
			con = DriverManager.getConnection(p.getDatabaseUrl(), p.getDatabaseUser(), p.getDatabasePassword());
			stat = con.prepareStatement("insert into \"while\" (\"v\",\"i\") values (?,?)");
			
			// skip first two revisions not yet applied
			revisions.next();
			revisions.next();
			
			for(int i = 0; i<5; i++)
			{
				final Revision revision = revisions.next();
				final ArrayList<RevisionInfoRevise.Body> body = new ArrayList<RevisionInfoRevise.Body>();
				int j = 0;
				for(final String sql : revision.getBody())
				{
					body.add(new RevisionInfoRevise.Body(sql, (100*i)+j+1000, (100*i)+(10*j)+10000));
					j++;
				}
				save(stat, new RevisionInfoRevise(
						revision.getNumber(),
						new Date(),
						environment,
						revision.getComment(),
						body.toArray(new RevisionInfoRevise.Body[body.size()])));
				
				if("before change of environment".equals(revision.getComment()))
				{
					environment.put("database.name",    environment.get("database.name")    + " - Changed");
					environment.put("database.version", environment.get("database.version") + " - Changed");
				}
			}
			{
				final Revision revision = revisions.next();
				save(stat, new RevisionInfoCreate(
						revision.getNumber(),
						new Date(),
						environment));
			}
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "create");
		}
		finally
		{
			if(stat!=null)
			{
				try
				{
					stat.close();
					stat = null;
				}
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, "close");
				}
			}
			if(con!=null)
			{
				try
				{
					con.close();
					con = null;
				}
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, "close");
				}
			}
		}
	}
	
	private static final void save(final PreparedStatement stat, final RevisionInfo info) throws SQLException
	{
		stat.setInt(1, info.getNumber());
		stat.setBytes(2, info.toBytes());
		stat.execute();
	}
}
