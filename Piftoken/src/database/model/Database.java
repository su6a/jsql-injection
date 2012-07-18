package database.model;

import java.util.ArrayList;
import java.util.List;

public class Database extends ElementDatabase {
    public String tableCount;

    public Database(String newDatabaseName, String newTableCount) {
    	this.elementValue = newDatabaseName;
    	this.tableCount = newTableCount;
    }

	@Override
	public ElementDatabase getParent() {
		return null;
	}

	@Override
	public int getCount() {
		return Integer.parseInt(tableCount);
	}

	@Override
	public String toFormattedString() {
		return this.elementValue + " ("+tableCount+" tables)";
	}
}
