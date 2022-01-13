package com.zx.sms.connect.manager.cmpp;

import com.alibaba.fastjson.JSONObject;
import com.zx.sms.codec.cmpp.msg.*;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.time.DateFormatUtils;

import com.zx.sms.common.util.CachedMillisecondClock;
import com.zx.sms.handler.api.AbstractBusinessHandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

@Sharable
public class CMPPResponseSenderHandler extends AbstractBusinessHandler {
	
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    	//此时未经过长短信合并
    	if (msg instanceof CmppDeliverRequestMessage) {
    		CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) msg;
    		CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
			responseMessage.setResult(0);
			responseMessage.setMsgId(e.getMsgId());
			ctx.channel().writeAndFlush(responseMessage);
    	}else if (msg instanceof CmppSubmitRequestMessage) {
    		CmppSubmitRequestMessage e = (CmppSubmitRequestMessage) msg;
    		CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
			resp.setResult(0);
			ctx.channel().writeAndFlush(resp);
			
			if(e.getRegisteredDelivery()==1) {
				final CmppDeliverRequestMessage deliver = new CmppDeliverRequestMessage();
				deliver.setDestId(e.getSrcId());
				deliver.setSrcterminalId(e.getDestterminalId()[0]);
				CmppReportRequestMessage report = new CmppReportRequestMessage();
				report.setDestterminalId(deliver.getSrcterminalId());
				report.setMsgId(resp.getMsgId());
				String t = DateFormatUtils.format(CachedMillisecondClock.INS.now(), "yyMMddHHmm");
				report.setSubmitTime(t);
				report.setDoneTime(t);
				report.setStat("DELIVRD");
				report.setSmscSequence(0);
				deliver.setReportRequestMessage(report);
				ctx.executor().submit(new Runnable() {
					public void run() {
							ctx.channel().writeAndFlush(deliver);
					}
				});
			}
			
			
    	}else if (msg instanceof CmppQueryRequestMessage) {
			CmppQueryRequestMessage e = (CmppQueryRequestMessage) msg;
			CmppQueryResponseMessage res = new CmppQueryResponseMessage(e.getHeader().getSequenceId());
			ctx.channel().writeAndFlush(res);
		}else if (msg instanceof CmppAllCodeRequestMessage){
    		//打印用户标识

			AttributeKey<String> key = AttributeKey.valueOf("user");
			if(ctx.channel().hasAttr(key) || ctx.channel().attr(key).get() != null)
				System.out.println(ctx.channel().attr(key).get());

			CmppAllCodeRequestMessage e = (CmppAllCodeRequestMessage) msg;
			System.out.println("服务端收到请求："+e.getBody());
//			CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
//			resp.setResult(0);
//			ctx.channel().writeAndFlush(resp);

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", "100");
			jsonObject.put("name", "服务端响应");
			String body = jsonObject.toJSONString();
			CmppAllCodeResponseMessage cmppAllCodeResponseMessage = new CmppAllCodeResponseMessage(e.getHeader().getSequenceId());
			cmppAllCodeResponseMessage.setJsonBodyLength(body.length());
			cmppAllCodeResponseMessage.setBody(body);
			ctx.channel().writeAndFlush(cmppAllCodeResponseMessage);

		}else if (msg instanceof CmppAllCodeResponseMessage){
			CmppAllCodeResponseMessage e = (CmppAllCodeResponseMessage) msg;
			System.out.println("服务端收到响应："+e.getBody());
		}
    	
    	ctx.fireChannelRead(msg);
    }
    
	@Override
	public String name() {
		return "CMPPResponseSenderHandler";
	}

}
