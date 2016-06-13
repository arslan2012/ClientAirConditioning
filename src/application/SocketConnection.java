package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class SocketConnection{
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private double tmpTemperature;
	private Wind tmpWind;
	
	public boolean createConnection(String IP,int port) {
		try {
			socket = new Socket(IP, port);
			InputStream inStream = socket.getInputStream();
			in = new Scanner(inStream);
			OutputStream outStream = socket.getOutputStream();
			out = new PrintWriter(outStream);
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public Socket getSocket() {
		return socket;
	}


	public Scanner getIn() {
		return in;
	}


	public PrintWriter getOut() {
		return out;
	}


	public boolean sendStartUp(int roomNumber) {//从机上线
		try {
			out.println("1 "
					+ Integer.toString(roomNumber) + " ");
			out.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public synchronized boolean sendDesiredTemperatureAndWind(double temperature, Wind wind) {
		try {
			out.println("3 "
					+ Double.toString(temperature) + " "+Integer.toString(wind.getValue())+" ");
			out.flush();
			this.tmpTemperature=temperature;
			this.tmpWind=wind;
			System.out.println("sent"+temperature);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendStopWind() {
		try {
			out.println("5 ");
			out.flush();
			System.out.println("stop");
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendCurTemperature(double temperature) {
		try {
			out.println("9 "
					+ Double.toString(temperature) + " ");
			out.flush();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void decode(Client client) {
		if (in.hasNext()) {
			int infoNumber = in.nextInt();
			client.setPower(true);
			switch (infoNumber) {
			/*上线后主机返回信息*/
			case 2:
				in.hasNext();
				int mode = in.nextInt();
				if (mode == 0)
					client.setMode(Mode.COOL);
				else
					client.setMode(Mode.HEAT);
				break;
				
			/*调节温度请求回应*/
			case 4:
				in.hasNext();
				int isReasonable = in.nextInt();
				if (isReasonable == 0) {
					client.setState(State.WAITING);
					client.setGoalTemperature(tmpTemperature);
					client.setWind(tmpWind);
					}
				else
					client.setState(State.STANDBY);
				break;
				
			/*主机送送风信息*/
			case 6:
				client.setState(State.RUNNING);
				break;
				
			/*主机送停风信息*/
			case 7:
				in.hasNext();
				in.nextInt();//待修改
				client.setState(State.STANDBY);
				client.setSumConsumption(client.getConsumption()+client.getSumConsumption());
				client.setSumFee(client.getFee()+client.getSumFee());
				client.setFee(0);
				client.setConsumption(0);
				break;
				
			/*请求获取温度*/
			case 8:
				sendCurTemperature(client.getTemperature());
				break;
			
			/*发送一次计费用量信息*/
			case 10:
				in.hasNext();
				double consumption = in.nextDouble();
				in.hasNext();
				double fee = in.nextDouble();
				
				client.setConsumption(consumption);
				client.setFee(fee);
				break;
			default:
				break;
			}
		}else client.setPower(false);
	}
	
	public boolean closeConnection() {
		try {
			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
