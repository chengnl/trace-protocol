package wuyun.cnl.protocol;

import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

import wuyun.cnl.ThreadTrace;
import wuyun.cnl.TraceFunc;

public class TTraceStringProtocol extends TProtocolDecorator{
     public final String TRACEID_SEPARATOR=":";
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
				return new TTraceStringProtocol(this.protocolFactory.getProtocol(trans),this.func);
			}else{
				return new TTraceStringProtocol(this.protocolFactory.getProtocol(trans));
			}
		}
		 
	 }
	
	public TTraceStringProtocol(TProtocol protocol){
		super(protocol);
		this.func=new TraceFunc(){
			@SuppressWarnings("unchecked")
			@Override
			public String genTrace() {
				return UUID.randomUUID().toString();
			}
		};
	}
	public TTraceStringProtocol(TProtocol protocol,TraceFunc func){
		super(protocol);
		this.func=func;
		
	}
	
	public void writeMessageBegin(TMessage tMessage) throws TException{
		String traceID =ThreadTrace.get();
		if(traceID==null){
			traceID=ThreadTrace.genTraceID(this.func);
			ThreadTrace.set(traceID);
		}
		super.writeMessageBegin(new TMessage(traceID + TRACEID_SEPARATOR + tMessage.name, tMessage.type, tMessage.seqid));
	}

	public TMessage readMessageBegin() throws TException {
		TMessage  message = super.readMessageBegin();
		String name = message.name;
		int charIndex = name.indexOf(TRACEID_SEPARATOR);
		if (charIndex==-1){
			throw new TProtocolException(TProtocolException.UNKNOWN,"Expected trace protocol is error");
		}
		String traceID=name.substring(0,charIndex);
		if(traceID.equals("")){
			throw new TProtocolException(TProtocolException.INVALID_DATA,"Expected traceID is null");
		}
		name=name.substring(charIndex+1);
		ThreadTrace.set(traceID);
		return new TMessage(name,message.type,message.seqid) ;
    }
	
}
