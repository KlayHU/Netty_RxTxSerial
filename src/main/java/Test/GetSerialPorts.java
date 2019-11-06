package Test;

import Lora.recive.MsgSendData;
import Lora.recive.Node;
import Lora.send.MsgRevData;
import gnu.io.*;
import netty.handler.msgHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


public final class GetSerialPorts {
    @Override
    public String toString() {
        return "GetSerialPorts{" +
                "serialPort1=" + serialPort1 +
                ", serialPortName='" + serialPortName + '\'' +
                '}';
    }

    public  SerialPort serialPort1;

    private String serialPortName;

    public GetSerialPorts(String serialPortName) {
        this.serialPortName=serialPortName;
    }


    public void setserialPort(SerialPort serialPort){

        this.serialPort1=serialPort;
    }


    public void run(){

        getSystemPort();

        final SerialPort serialPort = openSerialPort(serialPortName,9600);
        setserialPort(serialPort);
//        TCPServer tcpServer = new TCPServer(8888);
//        sendData(serialPort,);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int i = 1;
//                while(i<1000) {
//                    String s = "测试\n";
//                    byte[] bytes = s.getBytes();
//                    GetSerialPorts.sendData(serialPort, bytes);
//                    i++;
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();

        GetSerialPorts.setListenerToSerialPort(serialPort, new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent arg0) {
                if(arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    byte[] bytes = GetSerialPorts.readData(serialPort);
                    System.out.println("收到的数据长度："+bytes.length);
                    System.out.println("收到的数据："+new String(bytes));
                }
            }
        });
    }


    @SuppressWarnings("unchecked")
    public static List<String> getSystemPort(){
        List<String> systemPorts = new ArrayList<>();

        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while(portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            systemPorts.add(portName);
        }
        System.out.println("系统可用端口列表："+systemPorts);
        return systemPorts;
    }


    private static SerialPort openSerialPort(String serialPortName,int baudRate) {
        try {

            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);

            CommPort commPort = null;
            try {
                commPort = portIdentifier.open(serialPortName, 2222);
            } catch (PortInUseException e) {
                e.printStackTrace();
            }

            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;

                try {
                    serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                }
                System.out.println("开启串口成功，串口名称："+serialPortName);
                return serialPort;
            }
            else {

                throw new NoSuchPortException();
            }
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void closeSerialPort(SerialPort serialPort) {
        if(serialPort != null) {
            serialPort.close();
            System.out.println("关闭了串口："+serialPort.getName());
            serialPort = null;
        }
    }


    public static void sendData(SerialPort serialPort, byte[] data) {
        OutputStream os = null;
        try {
            os = serialPort.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public SerialPort getSerialPort1() {
        return serialPort1;
    }

    public void setSerialPort1(SerialPort serialPort1) {
        this.serialPort1 = serialPort1;
    }

    public String getSerialPortName() {
        return serialPortName;
    }

    public void setSerialPortName(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    private static byte[] readData(SerialPort serialPort) {
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = serialPort.getInputStream();
            int bufflenth = is.available();
            while (bufflenth != 0) {
                bytes = new byte[bufflenth];
                is.read(bytes);

                //发送到网关
                MsgSendData msgSendData = new MsgSendData();
                msgSendData.setMsgID((short) 1);

                Map<String, GetSerialPorts> portMap = PortManager.portMap;
                GetSerialPorts getSerialPorts=null;

                System.out.println(serialPort.toString());
                for(String key : portMap.keySet()){
                    System.out.println(portMap.get(key).toString());
                    GetSerialPorts com1 = portMap.get("COM1");
                    if((portMap.get(key).getSerialPort1().toString().equals(serialPort.toString()))&&key.equals("COM1")){

                            msgSendData.setLora_numb((short)0);
                        }else if((portMap.get(key).getSerialPort1().toString().equals(serialPort.toString()))&&key.equals("COM3")){
                            msgSendData.setLora_numb((short)1);
                        }

                }


                msgSendData.setMsgLength((short) (bytes.length+6));
                msgHandler.sendMsg(msgSendData,bytes);
                bufflenth = is.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }


    private static void setListenerToSerialPort(SerialPort serialPort, SerialPortEventListener listener) {
        try {

            serialPort.addEventListener(listener);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        serialPort.notifyOnDataAvailable(true);
        serialPort.notifyOnBreakInterrupt(true);
    }


}