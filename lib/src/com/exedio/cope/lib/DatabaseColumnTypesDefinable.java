
package com.exedio.cope.lib;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

interface DatabaseColumnTypesDefinable
{
	void defineColumnTypes(List columnTypes, Statement statement)
			throws SQLException;

}
