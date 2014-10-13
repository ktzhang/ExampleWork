package edu.ucsd.placeit.db;

public final class Queries {
	static final String CREATE_TABLE_PLACEIT = 
			"CREATE TABLE %s (" +
			"%s INTEGER PRIMARY KEY AUTOINCREMENT, " + //ID
			"%s INTEGER, " +			// Type
			"%s TEXT, " +				// User
			"%s TEXT, " + 				// Title
			"%s TEXT, " + 				// Desc
			"%s INTEGER, " +			// State
			"%s DOUBLE, " +				// Longitude
			"%s DOUBLE, " +				// Latitude
			"%s DATETIME, " +			// Date created
			"%s DATETIME, " +			// Date posted
			"%s DATETIME, " +			// Date Freq Start
			"%s INTEGER, " +			// Frequency
			"%s TEXT, " +				// Cat 1
			"%s TEXT, " + 				// Cat 2
			"%s TEXT)";					// Cat 3
			
	static final String CREATE_TABLE_VERSION_STRING =
			"CREATE TABLE %s (" +
			"%s DATETIME)";
	
	static final String SELECT_PLACEIT =
			"SELECT * FROM %s " +
			"WHERE %s = %d";
	
	static final String SELECT_VERSION =
			"SELECT * FROM %s";
	
	static final String SELECT_ALL_PLACEIT =
			"SELECT * FROM %s";
	
	static final String SELECT_ALL_TYPE_PLACEIT = 
			"SELECT * FROM %s WHERE %s LIKE %s";
	/**
	 * Get the placeIt states
	 * Sample:
	 * 		SELECT * FROM placeIts WHERE sate LIKE '1' 
	 * 		AND (cat_1 LIKE grocery OR cat_1 LIKE grocery OR cat_1 LIKE grocery)
	 *  	AND type LIKE '1' 
	 */
	static final String SELECT_STATE_CAT_PLACEIT =
			"SELECT * FROM %s " +
			"WHERE %s LIKE '%s' " +
			"AND (" +
			"%s LIKE '%s' " +
			"OR %s LIKE '%s' " + 
			"OR %s LIKE '%s' " +
			")" +
			" AND " +
			"%s LIKE '%s'" + // State
			" AND " +
			"%s LIKE '%s'"; // Username
			
	static final String UPDATE_PLACEIT = 
			"UPDATE %s " +
			"SET " +
			"%s = '%s', " +	// Type
			"%s = '%s', " +	// user
			"%s = '%s', " +	// Title
			"%s = '%s', " +	// Desc
			"%s = '%s', " +	// State
			"%s = '%s', " +	// Longitude
			"%s = '%s', " +	// Latitude
			"%s = '%s', " +	// Date created
			"%s = '%s', " +	// Date posted
			"%s = '%s', " +	// Date freq start
			"%s = '%s', " +	// Frequency
			"%s = '%s', " +	// Cat 1
			"%s = '%s', " +	// Cat 2
			"%s = '%s' " +	// Cat 3
			"WHERE %s = '%d'"; // ID
	
	static final String CHANGE_STATE_PLACEIT =
			"UPDATE %s " +
			"SET " +
			"%s = '%s' " +
			"WHERE %s = '%d'";

	static final String CHANGE_VERSION=
			"UPDATE %s " +
			"SET " +
			"%s = '%s'";
	
	static final String DROP_TABLE =
			"DROP TABLE IF EXISTS %s";
	
	static final String CLEAR_ALL_TABLE =
			"DELETE FROM %s";
	static final String RESET_SEQUENCE =
			"DELETE FROM sqlite_sequence " +
			"WHERE name='%s'";
	
	private Queries() {
		throw new AssertionError();
	}
}