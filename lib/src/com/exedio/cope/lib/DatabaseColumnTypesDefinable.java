
package com.exedio.cope.lib;

import java.util.List;
import java.sql.Statement;
import java.sql.SQLException;;

interface DatabaseColumnTypesDefinable
{
	abstract void defineColumnTypes(List columnTypes, Statement statement)
			throws SQLException;

}
