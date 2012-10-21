package mvc.model;

public abstract class Interruptable implements Runnable {
	public Boolean stopFlag = false;
	public boolean suspendFlag = false;

	public boolean isInterrupted(){
		synchronized(this) {
			while(suspendFlag) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(stopFlag){
				return true;
			}else{
				return false;
			}
		}
	}
	
	@Override
	public void run() {
		action();
	}
	
	public void begin(){
		Thread t = new Thread(this, "Interruptable - begin");
		t.start();
	}
	
	public synchronized void myresume() {
		notify();
	}
	
	public abstract void action(Object... args);
}
