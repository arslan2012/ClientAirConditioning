package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	private int roomNum;
	private Label runningState;
	private Label currentTemperature;
	private Label runningMode;
	private Label currentTurboSpeed;
	private Label currentCost;
	private Label currentEnergyConsumption;
	private Label sumCost;
	private Label sumEnergyConsumption;
	private Spinner<Double> userTemperature;
	private Controller defaultController;
	private Text actiontarget;

	@Override
	public void start(Stage primaryStage) {
		Font.loadFont(getClass().getResourceAsStream("/msyh.ttf"), 14);
		LoginScene(primaryStage);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	public void LoginScene(Stage thisStage) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("欢迎使用");
		grid.add(scenetitle, 0, 0, 2, 1);
		scenetitle.setId("welcome-text");

		Label userName = new Label("房间号:");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);
		
		Button customIP = new Button("自定IP");
		HBox hbcustomIP = new HBox(10);
		hbcustomIP.setAlignment(Pos.BOTTOM_RIGHT);
		hbcustomIP.getChildren().add(customIP);
		grid.add(hbcustomIP, 0, 4);
		
		TextField IPField = new TextField();
		IPField.setText("127.0.0.1");
		IPField.setVisible(false);
		grid.add(IPField, 0, 4);
		
		customIP.setOnAction((ActionEvent e) -> {
			customIP.setVisible(false);
			IPField.setVisible(true);
		});

		Button btn = new Button("登入");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);

		final Text actiontarget = new Text();
		grid.add(actiontarget, 0, 6, 2, 1);
		actiontarget.setId("actiontarget");

		btn.setOnAction((ActionEvent e) -> {
			try {
				roomNum = Integer.parseInt(userTextField.getText());
				defaultController = Controller.getInstance(roomNum,IPField.getText());
				if (defaultController.getState() == 1)
					mainScene(thisStage);
				else
					actiontarget.setText("连接主控机失败，请与管理员联系");
			} catch (Exception e1) {
				e1.printStackTrace();
				actiontarget.setText("请输入正确的房间号");
			}
		});

		Scene scene = new Scene(grid, 300, 250);

		thisStage.setTitle("登录");
		thisStage.setScene(scene);
		scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
		thisStage.show();
	}

	public void mainScene(Stage thisStage) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(70));

		Text scenetitle = new Text("空调遥控");
		grid.add(scenetitle, 0, 0, 2, 1);
		scenetitle.setId("welcome-text");

		Label roomNumber = new Label("房间号:" + Integer.toString(roomNum));
		grid.add(roomNumber, 0, 1, 2, 1);

		runningState = new Label("运行状态:    ");
		grid.add(runningState, 2, 1, 4, 1);

		currentTemperature = new Label("当前温度:     ");
		grid.add(currentTemperature, 6, 1, 4, 1);

		runningMode = new Label("工作模式:制冷");
		grid.add(runningMode, 0, 2, 4, 1);

		currentTurboSpeed = new Label("当前风速:    ");
		grid.add(currentTurboSpeed, 6, 2, 2, 1);
		
		sumCost = new Label("总费用:");
		HBox hb6 = new HBox();
		hb6.setId("hbox");
		hb6.getChildren().add(sumCost);
		grid.add(hb6, 2, 3, 4, 1);

		currentCost = new Label("当前费用:");
		HBox hb7 = new HBox();
		hb7.setId("hbox");
		hb7.getChildren().add(currentCost);
		grid.add(hb7, 6, 3, 2, 1);

		sumEnergyConsumption = new Label("总能耗:");
		HBox hb8 = new HBox();
		hb8.setId("hbox");
		hb8.getChildren().add(sumEnergyConsumption);
		grid.add(hb8, 2, 4, 4, 1);
		
		currentEnergyConsumption = new Label("当前能耗:");
		HBox hb9 = new HBox();
		hb9.setId("hbox");
		hb9.getChildren().add(currentEnergyConsumption);
		grid.add(hb9, 6, 4, 2, 1);

		Label settings = new Label("设定:");
		grid.add(settings, 1, 5, 4, 1);

		Label label1 = new Label("目标温度:");
		grid.add(label1, 0, 6, 2, 1);

		userTemperature = new Spinner<Double>(18, 25, 25,0.5);
		grid.add(userTemperature, 2, 6, 4, 1);

		Label label2 = new Label("目标风速:");
		grid.add(label2, 0, 7, 2, 1);

		ComboBox<String> userTurboSpeed = new ComboBox<String>();
		userTurboSpeed.getItems().addAll("低速风", "中速风", "高速风");
		userTurboSpeed.setValue("中速风");
		grid.add(userTurboSpeed, 2, 7, 4, 1);

		Button btn = new Button("确定");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 5, 8);

		actiontarget = new Text();
		grid.add(actiontarget, 0, 8, 4, 1);
		actiontarget.setId("actiontarget");

		btn.setOnAction((ActionEvent e) -> {
			try {
				double temp1 = userTemperature.getValue();
				if (userTurboSpeed.getValue().equals("低速风")) {
					if (!defaultController.sendDesiredTemperatureAndWind(temp1, Wind.LOW))
						actiontarget.setText("连接主控机失败，请与管理员联系");
					else
						actiontarget.setText("");
				} else if (userTurboSpeed.getValue().equals("中速风")) {
					if (!defaultController.sendDesiredTemperatureAndWind(temp1, Wind.MEDIUM))
						actiontarget.setText("连接主控机失败，请与管理员联系");
					else
						actiontarget.setText("");
				} else if (userTurboSpeed.getValue().equals("高速风")) {
					if (!defaultController.sendDesiredTemperatureAndWind(temp1, Wind.HIGH))
						actiontarget.setText("连接主控机失败，请与管理员联系");
					else
						actiontarget.setText("");
				} else
					actiontarget.setText("请选择风速");
			} catch (Exception e1) {
				actiontarget.setText("请输入正确的温度值");
			}
		});

		Scene scene = new Scene(grid, 650, 500);

		thisStage.setTitle("主界面");
		thisStage.setScene(scene);
		scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
		thisStage.show();
		defaultController.registerObserver(this);
		thisStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		    	defaultController.closeConnection();
		    	Platform.exit();
		    	System.exit(0);
		    }
		});
	}

	public void update(Client client) {
		if (client.getState() == State.RUNNING)
			runningState.setText("运行状态:运行中");
		else if (client.getState() == State.WAITING)
			runningState.setText("运行状态:等待中");
		else
			runningState.setText("运行状态:待机中");

		currentTemperature.setText(String.format("当前温度:%.2f",client.getTemperature()));

		if (client.getMode() == Mode.COOL){
			if(runningMode.getText().equals("工作模式:制热")){
				userTemperature.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(18,25,25,0.5));
			}
			runningMode.setText("工作模式:制冷");
			}
		else{
			if(runningMode.getText().equals("工作模式:制冷")){
				userTemperature.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(25,30,25,0.5));
			}
			runningMode.setText("工作模式:制热");
			}

		if (client.getWind() == Wind.LOW)
			currentTurboSpeed.setText("当前风速:低速");
		else if (client.getWind() == Wind.MEDIUM)
			currentTurboSpeed.setText("当前风速:中速");
		else
			currentTurboSpeed.setText("当前风速:高速");

		sumCost.setText(String.format("总费用:%.1f",client.getSumFee()));

		sumEnergyConsumption.setText(String.format("总能耗:%.1f" ,client.getSumConsumption()));
		
		currentCost.setText(String.format("当前费用:%.1f" ,client.getFee()));

		currentEnergyConsumption.setText(String.format("当前能耗:%.1f" ,client.getConsumption()));
		if (!client.getPower()) actiontarget.setText("主控机可能掉线！");
		else if (actiontarget.getText().equals("主控机可能掉线！")) actiontarget.setText("");
	}
}
