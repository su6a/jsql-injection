package sqli.view.jtree;

import database.model.Column;
import database.model.Database;
import database.model.ElementDatabase;
import database.model.Table;

public class TreeNodeModel<T extends ElementDatabase>{
	public String textNode;
	public T dataObject;
	public int childUpgradeCount = 0;
	
	public boolean isSelected = false;
	public boolean hasCheckBox = false;
	public boolean hasChildSelected = false;
	public boolean hasBeenSearched = false;
	public boolean hasIndeterminatedProgress = false;
	public boolean hasProgress = false;
	
	public TreeNodeModel(T newObject){
		this.dataObject = newObject;
	}
	
	public ElementDatabase getParent(){
		return dataObject.getParent();
	}
	public boolean isDatabase(){
		return dataObject instanceof Database;
	}
	public boolean isTable(){
		return dataObject instanceof Table;
	}
	public boolean isColumn(){
		return dataObject instanceof Column;
	}
	
	public String toString(){
		return this.dataObject.toFormattedString();
	}
}