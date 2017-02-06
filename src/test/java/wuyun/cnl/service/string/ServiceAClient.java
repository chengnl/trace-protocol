package wuyun.cnl.service.string;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import wuyun.cnl.ThreadTrace;
import wuyun.cnl.protocol.TTraceStringProtocol;

public class ServiceAClient {
	public static void main(String[] args) {
		TTransport transport = null;
		try {
			transport = new TSocket("127.0.0.1", 8080);
			transport.open();
			TProtocol protocol = new TTraceStringProtocol(new TBinaryProtocol(transport));
			ServiceA.Client client = new ServiceA.Client(protocol);
			System.out.println(client.HelloWorld("test A"));
			System.out.println("CALL ServiceA traceID:="+ThreadTrace.get());
			
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			transport.close();
		}
	}
}
