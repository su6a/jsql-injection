package database.model;

import java.util.ArrayList;
import java.util.List;

public class Table extends ElementDatabase {
	public Database parentDatabase;
    public String rowCount;

    public Table(String newTableName, String newRowCount, Database newParentDatabase) {
    	this.elementValue = newTableName;
    	this.rowCount = newRowCount;
    	this.parentDatabase = newParentDatabase;
    }

	@Override
	public ElementDatabase getParent() {
		return this.parentDatabase;
	}

	@Override
	public int getCount() {
		return Integer.parseInt(rowCount);
	}

	@Override
	public String toFormattedString() {
		return this.elementValue + " ("+
        		((parentDatabase+"").equals("information_schema")?"?":rowCount)
        	+" rows)";
	}
}
