package application;

public class getClientStats implements Runnable {

	@Override
	public void run() {
		while(!Controller.getInstance(0).mutexLock){
			Controller.getInstance(0).mutexLock=true;
			Controller.getInstance(0).decode();
			Controller.getInstance(0).mutexLock=false;
			}
	}

}
