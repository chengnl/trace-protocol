package wuyun.cnl;

import java.util.UUID;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolDecorator;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

public class TTraceProtocol extends TProtocolDecorator{
     public final String TRACEID_SEPARATOR=":";
     private TraceIDFunc func;//跟踪ID产生函数
	 public static class Factory implements TProtocolFactory {
		private static final long serialVersionUID = 1L;
		private TProtocolFactory protocolFactory;
		private TraceIDFunc func;
		public Factory(TProtocolFactory protocolFactory){
			this.protocolFactory=protocolFactory;
		}
		public Factory(TProtocolFactory protocolFactory,TraceIDFunc func){
			this.protocolFactory=protocolFactory;
			this.func=func;
			
		}

		@Override
		public TProtocol getProtocol(TTransport trans) {
			if(this.func!=null){
				return new TTraceProtocol(this.protocolFactory.getProtocol(trans),this.func);
			}else{
				return new TTraceProtocol(this.protocolFactory.getProtocol(trans));
			}
		}
		 
	 }
	
	public TTraceProtocol(TProtocol protocol){
		super(protocol);
		this.func=new TraceIDFunc(){
			@Override
			public String genTraceID() {
				return UUID.randomUUID().toString();
			}
		};
	}
	public TTraceProtocol(TProtocol protocol,TraceIDFunc func){
		super(protocol);
		this.func=func;
		
	}
	
	public void writeMessageBegin(TMessage tMessage) throws TException{
		String traceID =ThreadTraceID.get();
		if(traceID==null){
			traceID=ThreadTraceID.genTraceID(this.func);
			ThreadTraceID.set(traceID);
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
		ThreadTraceID.set(traceID);
		return new TMessage(name,message.type,message.seqid) ;
    }
	
}
