package com.zx.sms.connect.manager.cmpp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSONObject;
import com.zx.sms.codec.cmpp.msg.CmppAllCodeRequestMessage;
import com.zx.sms.common.GlobalConstance;
import com.zx.sms.common.util.ChannelUtil;
import com.zx.sms.connect.manager.*;
import io.netty.util.concurrent.Future;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.sms.connect.manager.EndpointEntity.SupportLongMessage;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.handler.api.BusinessHandlerInterface;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

/**
 * 经测试，35个连接，每个连接每200/s条消息 lenovoX250能承担7000/s消息编码解析无压力。
 * 10000/s的消息服务不稳定，开个网页，或者打开其它程序导致系统抖动，会有大量消息延迟 (超过500ms)
 *
 * 低负载时消息编码解码可控制在10ms以内。
 *
 */

public class TestCMPPEndPoint {
	private static final Logger logger = LoggerFactory.getLogger(TestCMPPEndPoint.class);

	@Test
	public void testCMPPEndpoint() throws Exception {
		ResourceLeakDetector.setLevel(Level.ADVANCED);
		final EndpointManager manager = EndpointManager.INS;

		CMPPServerEndpointEntity server = new CMPPServerEndpointEntity();
		server.setId("server");
		server.setHost("0.0.0.0");
		server.setPort(7890);
		server.setValid(true);
		// 使用ssl加密数据流
		server.setUseSSL(false);

		CMPPServerChildEndpointEntity child = new CMPPServerChildEndpointEntity();
		child.setId("901783");
		child.setChartset(Charset.forName("utf-8"));
		child.setGroupName("test");
		child.setUserName("901783");
		child.setPassword("ICP001");

		child.setValid(true);
		child.setVersion((short) 0x20);

		child.setMaxChannels((short) 1000000);
		child.setRetryWaitTimeSec((short) 30);
		child.setMaxRetryCnt((short) 3);
		
		//不开启IP白名单
//		List<String> iplist = new ArrayList<String>();
//		iplist.add("192.168.98.48/18");
//		child.setAllowedAddr(iplist);
		
		child.setReSendFailMsg(false);
//		 child.setWriteLimit(200);
//		 child.setReadLimit(200);
		List<BusinessHandlerInterface> serverhandlers = new ArrayList<BusinessHandlerInterface>();
		//接受短信
		CMPPMessageReceiveHandler receiver = new CMPPMessageReceiveHandler();
		serverhandlers.add(new AbstractBusinessHandler() {

		    @Override
		    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		    	CMPPResponseSenderHandler handler = new CMPPResponseSenderHandler();
		    	handler.setEndpointEntity(getEndpointEntity());
		    	ctx.pipeline().addAfter("sessionStateManager", handler.name(), handler);
		    	ctx.pipeline().remove(this);
		    }
			
			@Override
			public String name() {
				return "AddCMPPResponseSenderHandler";
			}
			
		});
		serverhandlers.add(receiver);
		child.setBusinessHandlerSet(serverhandlers);
		server.addchild(child);
		manager.addEndpointEntity(server);
		manager.addEndpointEntity(child);
		//创建多一个业务方账号
		CMPPServerChildEndpointEntity child2 = new CMPPServerChildEndpointEntity();
		child2.setId("991783");
		child2.setChartset(Charset.forName("utf-8"));
		child2.setGroupName("test");
		child2.setUserName("991783");
		child2.setPassword("ICP001");

		child2.setValid(true);
		child2.setVersion((short) 0x20);

		child2.setMaxChannels((short) 1);
		child2.setRetryWaitTimeSec((short) 30);
		child2.setMaxRetryCnt((short) 3);

		//不开启IP白名单
//		List<String> iplist = new ArrayList<String>();
//		iplist.add("192.168.98.48/18");
//		child.setAllowedAddr(iplist);

		child2.setReSendFailMsg(false);
		child2.setBusinessHandlerSet(serverhandlers);
		server.addchild(child2);
		manager.addEndpointEntity(child2);








		CMPPClientEndpointEntity client4;
		for (int j = 0; j<1; j++){
			client4 = getClient("client4"+j,1);
			manager.addEndpointEntity(client4);
			manager.openEndpoint(client4);
		}


		manager.openEndpoint(server);

		Thread.sleep(5000);
		manager.startConnectionCheckTask();
//		//开启定时任务, 服务端发起通用请求
//		EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {
//
//			@Override
//			public Boolean call() throws Exception {
//
//				return true;
//			}
//
//		}, future->true, 3000);



//		System.out.println("start.....");
		while (receiver.getCnt().get() < 2000000000) {
			Thread.sleep(3000);
			//服务端发送消息
//			CmppAllCodeRequestMessage msg = new CmppAllCodeRequestMessage();
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("code", "200");
//			jsonObject.put("name", "服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端务端请求服务端请求服务端请求服务端请求服务端请求服务端请求end");
//			String body = jsonObject.toJSONString();
//			msg.setJsonBodyLength(body.getBytes(GlobalConstance.defaultTransportCharset).length);
//			msg.setBody(body);
//			msg.setCode("200");
//			ChannelUtil.syncWriteBinaryMsgToEntity("991783", msg);
		}
		Thread.sleep(3000);
		EndpointManager.INS.close();
		Thread.sleep(4000);
		System.out.println("end.....");
	}


	private CMPPClientEndpointEntity getClient(String clientName, int count){
		CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
		client.setId(clientName);
//		client.setLocalhost("127.0.0.1");
		// client.setLocalport(65521);
		client.setHost("127.0.0.1");
		client.setPort(7890);
		client.setChartset(Charset.forName("utf-8"));
		client.setGroupName("test");
		client.setUserName("901783");
		client.setPassword("ICP001");

		client.setMaxChannels((short) 1);
		client.setVersion((short) 0x20);
		client.setRetryWaitTimeSec((short) 30);
		client.setUseSSL(false);
		// client.setWriteLimit(100);
		client.setReSendFailMsg(true);
		client.setSupportLongmsg(SupportLongMessage.BOTH);
		List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
		//发送短信
		CMPPSessionConnectedHandler sender = new CMPPSessionConnectedHandler(count);
		clienthandlers.add(sender);
		client.setBusinessHandlerSet(clienthandlers);
		return client;
	}
}
