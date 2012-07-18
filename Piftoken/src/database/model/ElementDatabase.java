package database.model;

public abstract class ElementDatabase {
	protected String elementValue;
	
	public abstract ElementDatabase getParent();
	public abstract int getCount();
	public abstract String toFormattedString();
	
    public String toString() {
        return this.elementValue;
    }

	public String getElementValue() {
		return elementValue;
	}
}
