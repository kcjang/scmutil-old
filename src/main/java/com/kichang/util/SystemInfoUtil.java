package com.kichang.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

public class SystemInfoUtil {
	
	static enum OSType {
		LINUX,
		UNKNOWN
	}
	
	private final static String CPU_MODEL_COMMAND 	= "grep 'model name' /proc/cpuinfo | awk -F \":\" '{gsub(/^[ \t]+/, \"\", $2); print $2}' | uniq"; 
	private final static String CPU_NUM_COMMAND 		= "grep 'physical id' /proc/cpuinfo | sort -u | wc -l"; 
	private final static String CORE_NUM_COMMAND 		= "grep 'core id' /proc/cpuinfo | sort -u | wc -l"; 
	private final static String MEM_PHYSIC_COMMAND 	= "free | grep 'Mem' | awk '{print $2}'"; 
	private final static String MEM_FREE_COMMAND 		= "free | grep 'Mem' | awk '{print $4}'"; 


	public static OSType os_type() {
		String os = System.getProperty("os.name");
		if ("Linux".equalsIgnoreCase(os)) {
			return OSType.LINUX;
		}

		return OSType.UNKNOWN;
	}
	
	public static String cpu_model() {
		String model = "unknown";
		String s = null;
		
		switch (os_type()) {
		case LINUX:
			try {
				String[] cmd = {
					"/bin/sh",
					"-c",
					CPU_MODEL_COMMAND
				};
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		       BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		       while ((s = stdInput.readLine()) != null) {
	                model = s;
	            }
		       while ((s = stdError.readLine()) != null) {
	                System.err.println(s);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		
		return model;
	}
	
	public static int cpu_num() {
		int num = 0;
		String s = null;
		
		switch (os_type()) {
		case LINUX:
			try {
				String[] cmd = {
					"/bin/sh",
					"-c",
					CPU_NUM_COMMAND
				};
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		       BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		       while ((s = stdInput.readLine()) != null) {
		    	   num = Integer.parseInt(s);
	            }
		       while ((s = stdError.readLine()) != null) {
	                System.err.println(s);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		return num;
		
	}

	
	
	
	
	public static int core_num() {
		int num = 0;
		String s = null;
		
		switch (os_type()) {
		case LINUX:
			try {
				String[] cmd = {
					"/bin/sh",
					"-c",
					CORE_NUM_COMMAND
				};
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		       BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		       while ((s = stdInput.readLine()) != null) {
		    	   num = Integer.parseInt(s);
	            }
		       while ((s = stdError.readLine()) != null) {
	                System.err.println(s);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		return num;
	}
	
	public static int mem_physic() {
		int num = 0;
		String s = null;
		
		switch (os_type()) {
		case LINUX:
			try {
				String[] cmd = {
					"/bin/sh",
					"-c",
					MEM_PHYSIC_COMMAND
				};
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		       BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		       while ((s = stdInput.readLine()) != null) {
		    	   num = Integer.parseInt(s);
	            }
		       while ((s = stdError.readLine()) != null) {
	                System.err.println(s);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		return num;
	}
	
	public static int mem_free() {
		
		int num = 0;
		String s = null;
		
		switch (os_type()) {
		case LINUX:
			try {
				String[] cmd = {
					"/bin/sh",
					"-c",
					MEM_FREE_COMMAND
				};
				Process p = Runtime.getRuntime().exec(cmd);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		       BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		       while ((s = stdInput.readLine()) != null) {
		    	   num = Integer.parseInt(s);
	            }
		       while ((s = stdError.readLine()) != null) {
	                System.err.println(s);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		return num;
	}

	public static String ipaddress() throws UnableNetworkInterfaceException {
		String ipaddress = "";
		Enumeration e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration ee = n.getInetAddresses();
			   
			    while (ee.hasMoreElements())
			    {
			       InetAddress i = (InetAddress) ee.nextElement();
			       String ip = i.getHostAddress();
			       if ( !i.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip) )
			       	ipaddress += ip + ";";
			    }
			}
		} catch (SocketException e1) {
			throw new UnableNetworkInterfaceException();
		}
		
		return ipaddress;
	}

	public static String user_home() {
		return System.getProperty("user.home");
	}

	
}
