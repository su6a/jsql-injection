package sqli.model;

import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;


public class ModelObservable extends Observable {
	
	protected class GUIThread extends Thread{
		private String message;
		private Object arg;
		
		GUIThread(String newMessage, Object newObject){
			super();
			this.message = newMessage;
			this.arg = newObject;
		}
		
		GUIThread(String newMessage){
			this(newMessage, null);
		}
		
		@Override
		public void run () {
            SwingUtilities.invokeLater(new Runnable() {
                public void run () {
                	ModelObservable.this.setChanged();
                	ModelObservable.this.notifyObservers( new ObserverEvent(message, arg ));
                }
            });
        }
	}

	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
	}
	
	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
	}

}
