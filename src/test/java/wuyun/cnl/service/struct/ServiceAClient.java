package wuyun.cnl.service.struct;

import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import wuyun.cnl.ThreadTrace;
import wuyun.cnl.TraceFunc;
import wuyun.cnl.protocol.TTraceStructProtocol;
import wuyun.cnl.trace.Trace;

public class ServiceAClient {
	public static void main(String[] args) {
		TTransport transport = null;
		try {
			transport = new TSocket("127.0.0.1", 8080);
			transport.open();
			TProtocol protocol= new TTraceStructProtocol(new TCompactProtocol(transport),new TraceFunc(){
					@Override
					public Trace genTrace() {
						Trace trace = new Trace();
						trace.setTraceID(UUID.randomUUID().toString());
						trace.setCallInfo("Service");
						return trace;
					}
				 });
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
