package wuyun.cnl.service.string;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import wuyun.cnl.ThreadTrace;
import wuyun.cnl.protocol.TTraceStringProtocol;

public class ServiceAImpl implements ServiceA.Iface{

	@Override
	public String HelloWorld(String name) throws TException {
        System.out.println("ServiceA traceID:="+ThreadTrace.get()+",args："+name);
        //call();		
		new Thread(new Runnable(){
			@Override
			public void run() {
				call();
			}
			
		}).start();
		return "hello,world:"+name;
	}

	
   private void call(){
	   TTransport transport = null;
		try {
			transport = new TSocket("127.0.0.1", 8081);
			transport.open();
			TProtocol protocol = new TTraceStringProtocol(new TBinaryProtocol(transport));
			ServiceA.Client client = new ServiceA.Client(protocol);
			System.out.println(client.HelloWorld("test B"));
			System.out.println("CALL ServiceB traceID:="+ThreadTrace.get());
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			transport.close();
		}
   }
}
