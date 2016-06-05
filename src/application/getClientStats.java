package application;

import javafx.application.Platform;

public class getClientStats implements Runnable {
	private Controller defaultController;

	public getClientStats(Controller controller) {
		defaultController = controller;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				Platform.runLater(() -> defaultController.decode());
			}
		}
	}

}
