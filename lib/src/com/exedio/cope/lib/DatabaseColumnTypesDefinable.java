
package com.exedio.cope.lib;

import java.sql.SQLException;
import java.sql.Statement;

import bak.pcj.list.IntList;

interface DatabaseColumnTypesDefinable
{
	void defineColumnTypes(IntList columnTypes, Statement statement)
			throws SQLException;

}
