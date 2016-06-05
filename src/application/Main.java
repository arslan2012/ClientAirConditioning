package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.*;
import javafx.stage.Stage;


public class Main extends Application implements Observer{
	private int roomNum;
	private Label runningState;
	private Label currentTemperature;
	private Label runningMode;
	private Label currentTurboSpeed;
	private Label currentCost;
	private Label currentEnergyConsumption;
    @Override
    public void start(Stage primaryStage) {
        LoginScene(primaryStage);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void LoginScene(Stage thisStage){
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
        
        Button btn = new Button("登入");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");
        
        btn.setOnAction((ActionEvent e) -> {
        	try{
        		roomNum=Integer.parseInt(userTextField.getText());
        		if (Controller.getInstance(roomNum).getState()==1)
                	mainScene(thisStage);
                else
                actiontarget.setText("连接主控机失败，请与鹏辉联系");
        	}catch(Exception e1){
        		actiontarget.setText("请输入正确的房间号");
        	}
        });
        
        
        Scene scene = new Scene(grid, 300, 250);
        
        thisStage.setTitle("登录");
        thisStage.setScene(scene);
        scene.getStylesheets().add
         (Main.class.getResource("application.css").toExternalForm());
        thisStage.show();
    }
    public void mainScene(Stage thisStage){
    	Controller.getInstance(0).registerObserver(this);
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(100,100,100,100));
        
        Text scenetitle = new Text("鹏辉空调遥控");
        grid.add(scenetitle, 0, 0, 2, 1);
        scenetitle.setId("welcome-text");

        Label roomNumber = new Label("房间号:"+Integer.toString(roomNum));
        grid.add(roomNumber, 0, 1, 2, 1);
        
        runningState = new Label("运行状态:    ");
        grid.add(runningState, 2, 1, 4, 1);
        
        currentTemperature = new Label("当前温度:     ");
        grid.add(currentTemperature, 6, 1, 4, 1);
        
        runningMode = new Label("工作模式:    ");
        grid.add(runningMode, 0, 2, 4, 1);
        
        currentTurboSpeed = new Label("当前风速:    ");
        grid.add(currentTurboSpeed, 4, 2, 2, 1);
        
        currentCost = new Label("费用:    ");
        grid.add(currentCost, 4, 3, 2, 1);
        
        currentEnergyConsumption = new Label("能耗:    ");
        grid.add(currentEnergyConsumption, 4, 4, 2, 1);
        
        Label settings = new Label("设定:");
        grid.add(settings, 1, 5, 4, 1);
        
        Label label1 = new Label("目标温度:");
        grid.add(label1, 0, 6, 2, 1);
        
        TextField userTemperature = new TextField();
        grid.add(userTemperature, 2, 6, 4, 1);
        
        Label label2 = new Label("目标风速:");
        grid.add(label2, 0, 7, 2, 1);
        
        ComboBox<String> userTurboSpeed = new ComboBox<String>();
        userTurboSpeed.getItems().addAll(
        	    "低速风",
        	    "中速风",
        	    "高速风"
        	);
        grid.add(userTurboSpeed, 2, 7, 4, 1);
        
        Button btn = new Button("确定");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 5, 8);
        
        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");
        
        btn.setOnAction((ActionEvent e) -> {
        	try{
        		int temp1=Integer.parseInt(userTemperature.getText());
        		Wind temp2;
        		if (userTurboSpeed.getValue().equals("低速风"))
        			temp2=Wind.LOW;
        		else if (userTurboSpeed.getValue().equals("中速风"))
        			temp2=Wind.MEDIUM;
        		else temp2 = Wind.HIGH;
        		if (!Controller.getInstance(roomNum).sendDesiredTemperatureAndWind(temp1, temp2))
                actiontarget.setText("连接主控机失败，请与鹏辉联系");
        	}catch(Exception e1){
        		actiontarget.setText("请输入正确的温度值");
        	}
            System.out.println("Hello World!");
        });
        
        
        Scene scene = new Scene(grid, 300, 250);
        
        thisStage.setTitle("主界面");
        thisStage.setHeight(350);
        thisStage.setWidth(450);
        thisStage.setScene(scene);
        scene.getStylesheets().add
         (Main.class.getResource("application.css").toExternalForm());
        thisStage.show();
    }
    public void update(Client client){
    	if (client.getState()==State.RUNNING)
    		runningState.setText("运行状态:运行中");
    	else if (client.getState()==State.WAITING)
    		runningState.setText("运行状态:等待中");
    	else runningState.setText("运行状态:待机中");
    	
    	currentTemperature.setText("当前温度:"+Double.toString(client.getTemperature()));
    	
    	if (client.getMode()==Mode.COOL)
    		runningMode.setText("工作模式:制冷");
    	else runningMode.setText("工作模式:制热");
    	
    	if (client.getWind()==Wind.LOW)
    		currentTurboSpeed.setText("当前风速:低速");
    	else if (client.getWind()==Wind.MEDIUM)
    		currentTurboSpeed.setText("当前风速:中速");
    	else currentTurboSpeed.setText("当前风速:高速");
    	
    	currentCost.setText("费用:"+Double.toString(client.getFee()));
    	
    	currentEnergyConsumption.setText("能耗:"+Double.toString(client.getConsumption()));
    }
}
