package wuyun.cnl.protocol;

import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

import wuyun.cnl.ThreadTrace;
import wuyun.cnl.TraceFunc;
import wuyun.cnl.trace.Trace;

public class TTraceStructProtocol extends TProtocolDecorator{
     private TraceFunc func;//跟踪ID产生函数
	 public static class Factory implements TProtocolFactory {
		private static final long serialVersionUID = 1L;
		private TProtocolFactory protocolFactory;
		private TraceFunc func;
		public Factory(TProtocolFactory protocolFactory){
			this.protocolFactory=protocolFactory;
		}
		public Factory(TProtocolFactory protocolFactory,TraceFunc func){
			this.protocolFactory=protocolFactory;
			this.func=func;
			
		}

		@Override
		public TProtocol getProtocol(TTransport trans) {
			if(this.func!=null){
				return new TTraceStructProtocol(this.protocolFactory.getProtocol(trans),this.func);
			}else{
				return new TTraceStructProtocol(this.protocolFactory.getProtocol(trans));
			}
		}
		 
	 }
	
	public TTraceStructProtocol(TProtocol protocol){
		super(protocol);
		this.func=new TraceFunc(){
			@SuppressWarnings("unchecked")
			@Override
			public Trace genTrace() {
				Trace trace = new Trace();
				trace.setTraceID(UUID.randomUUID().toString());
				return trace;
			}
		};
	}
	public TTraceStructProtocol(TProtocol protocol,TraceFunc func){
		super(protocol);
		this.func=func;
		
	}
	
	public void writeMessageEnd()throws TException{
		Trace trace =ThreadTrace.get();
		if(trace==null){
			trace=ThreadTrace.genTraceID(this.func);
			ThreadTrace.set(trace);
		}		
		trace.write(this);
		super.writeMessageEnd();
	}
	
	public void readMessageEnd() throws TException {
		Trace trace = new Trace();
		trace.read(this);
		if(!trace.isSetTraceID()){
			throw new TProtocolException(TProtocolException.INVALID_DATA,"Expected traceID is null");
		}		
		ThreadTrace.set(trace);		
		super.readMessageEnd();
	}
}
