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

package com.exedio.dsmf;

import static java.util.Objects.requireNonNull;

import com.exedio.dsmf.Dialect.ResultSetHandler;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import javax.annotation.Nonnull;

public abstract class Node
{
	public enum Color
	{
		OK("ok"),
		WARNING("warning"),
		ERROR("error");

		private final String style;

		Color(final String style)
		{
			this.style = style;
		}

		@Override
		public String toString()
		{
			return style;
		}
	}

	final Dialect dialect;
	final ConnectionProvider connectionProvider;

	private final boolean required;
	private boolean exists;
	private String additionalError;

	private Result resultIfSet;

	Node(final Dialect dialect, final ConnectionProvider connectionProvider, final boolean required)
	{
		this.dialect = requireNonNull(dialect, "dialect");
		this.connectionProvider = requireNonNull(connectionProvider, "connectionProvider");
		this.required = required;
		this.exists = !required;
	}

	final String quoteName(final String name)
	{
		return dialect.quoteName(name);
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	final void querySQL(final String statement, final ResultSetHandler resultSetHandler)
	{
		dialect.querySQL(connectionProvider, statement, resultSetHandler);
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	final void executeSQL(final String statement, final StatementListener listener)
	{
		// NOTE:
		// Should be done without holding resources such as connection, statement etc.
		if(listener!=null && !listener.beforeExecute(statement))
			return;

		final int rows;
		Connection connection = null;
		try
		{
			//noinspection resource OK: have to use putConnection
			connection = connectionProvider.getConnection();
			//System.out.println(statement);
			try(java.sql.Statement sqlStatement = connection.createStatement())
			{
				rows = sqlStatement.executeUpdate(statement);
				//System.out.println("  ("+rows+")");
			}
		}
		catch(final SQLException e)
		{
			//System.out.println("  -> "+e.getMessage());
			throw new SQLRuntimeException(e, statement);
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					connectionProvider.putConnection(connection);
				}
				catch(final SQLException ignored)
				{
					// exception is already thrown
				}
			}
		}
		// NOTE:
		// Should be done without holding resources such as connection, statement etc.
		if(listener!=null)
			listener.afterExecute(statement, rows);
	}

	final void notifyExistsNode()
	{
		if(exists)
			throw new IllegalStateException("duplicate notifyExistsNode on " + this);

		exists = true;
	}

	public final boolean required()
	{
		return required;
	}

	public final boolean exists()
	{
		return exists;
	}

	final void notifyAdditionalError(final String message)
	{
		requireNonNull(message);

		additionalError =
				(additionalError!=null)
				? additionalError + ", " + message
				: message;
	}

	final Result finish()
	{
		return this.resultIfSet = requireNonNull(computeResult(), "computeResult").
				additionalError(additionalError);
	}

	abstract Result computeResult();

	private Result result()
	{
		final Result result = resultIfSet;
		if(result==null)
			throw new IllegalStateException("result");
		return result;
	}

	public final String getError()
	{
		return result().error;
	}

	public final Color getParticularColor()
	{
		return result().particularColor;
	}

	public final Color getCumulativeColor()
	{
		return result().cumulativeColor;
	}

	static final class Result
	{
		final String error;
		final Color particularColor;
		final Color cumulativeColor;

		private Result(
				final String error,
				final Color particularColor,
				final Color cumulativeColor)
		{
			this.error = error;
			this.particularColor = particularColor;
			this.cumulativeColor = cumulativeColor;

			if(particularColor.ordinal()>cumulativeColor.ordinal())
				throw new IllegalArgumentException("" + particularColor + '>' + cumulativeColor);
		}

		private Result(
				final String error,
				final Color color)
		{
			this.error = error;
			this.particularColor = color;
			this.cumulativeColor = color;
		}

		Result cumulate(final Result child)
		{
			if(this .cumulativeColor.ordinal() >
				child.cumulativeColor.ordinal())
				return this;

			return new Result(
					this.error,
					this.particularColor,
					child.cumulativeColor);
		}

		Result additionalError(final String additionalError)
		{
			if(additionalError==null)
				return this;

			return new Result(
					(error!=null&&particularColor==Color.ERROR) ? error : additionalError,
					Color.ERROR,
					Color.ERROR);
		}

		static final Result ok = new Result(null, Color.OK);
		static final Result missing = new Result("missing", Color.ERROR);
		static final Result notSupported = new Result("not supported", Color.OK);
		static final Result notUsedWarning = new Result("not used", Color.WARNING);
		static final Result notUsedError = new Result("not used", Color.ERROR);

		static Result error(final String error)
		{
			return new Result(error, Color.ERROR);
		}
	}

	static final String requireNonEmptyTrimmed(
			@Nonnull final String value,
			@Nonnull final String message)
	{
		requireNonNull(value, message);
		if(value.isEmpty())
			throw new IllegalArgumentException(message + " must not be empty, but was " + value);
		if(!value.equals(value.trim()))
			throw new IllegalArgumentException(message + " must be trimmed, but was >" + value + '<');
		return value;
	}
}

