package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class SocketConnection implements Subject{
	private Socket socket;
	private Scanner in;
	private PrintWriter out;
	private final ArrayList<Observer> observers;
	
	public SocketConnection(){
		observers = new ArrayList<Observer>();
	}
	
	public boolean createConnection() {
		try {
			socket = new Socket("127.0.0.1", 8189);
			
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
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendDesiredTemperatureAndWind(double temperature, Wind wind) {
		try {
			out.println("3 "
					+ Double.toString(temperature) + " "+Integer.toString(wind.getValue())+" ");
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
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean sendCurTemperature(double temperature) {
		try {
			out.println("5 "
					+ Double.toString(temperature) + " ");
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
			
			switch (infoNumber) {
			
			/*上线后主机返回信息*/
			case 2:
				in.hasNext();
				int mode = in.nextInt();
				if (mode == 0)
					client.setMode(Mode.COOL);
				else
					client.setMode(Mode.HEAT);
				this.notifyObservers(client);
				break;
				
			/*调节温度请求回应*/
			case 4:
				in.hasNext();
				int isReasonable = in.nextInt();
				if (isReasonable == 0) 
					client.setState(State.WAITING);
				else
					client.setState(State.STANDBY);
				this.notifyObservers(client);
				break;
				
			/*主机送送风信息*/
			case 6:
				client.setState(State.RUNNING);
				this.notifyObservers(client);
				break;
				
			/*主机送停风信息*/
			case 7:
				in.hasNext();
				int preemption = in.nextInt();//待修改
				if (preemption==0)
				client.setState(State.STANDBY);
				else client.setState(State.WAITING);
				this.notifyObservers(client);
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
				
				client.setConsumption(consumption + client.getConsumption());
				client.setFee(fee + client.getFee());
				this.notifyObservers(client);
				break;
			default:
				break;
			}
		}
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


    public void registerObserver(Observer observer)
    {
        observers.add(observer);
    }

    public void removeObserver(Observer observer)
    {
        observers.remove(observer);
    }

    public void notifyObservers(Client client)
    {
        for (Observer o :observers)
        {
            o.update(client);
        }
    }
	
}
