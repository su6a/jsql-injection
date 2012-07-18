package database.model;

public class Value extends ElementDatabase {
	public Column column;

    public Value(String newValue, Column newColumn) {
    	this.elementValue = newValue;
    	this.column = newColumn;
    }

	@Override
	public ElementDatabase getParent() {
		return column;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public String toFormattedString() {
		return toString();
	}
}

