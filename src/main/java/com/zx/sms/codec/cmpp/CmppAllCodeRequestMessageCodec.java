/**
 * 
 */
package com.zx.sms.codec.cmpp;

import com.zx.sms.codec.cmpp.msg.CmppAllCodeRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.*;
import com.zx.sms.common.GlobalConstance;
import com.zx.sms.common.util.CMPPCommonUtil;
import com.zx.sms.common.util.DefaultMsgIdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import org.marre.sms.SmsDcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.zx.sms.common.util.NettyByteBufUtil.toArray;

/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppAllCodeRequestMessageCodec extends MessageToMessageCodec<Message, CmppAllCodeRequestMessage> {
	private final static Logger logger = LoggerFactory.getLogger(CmppAllCodeRequestMessageCodec.class);
	private PacketType packetType;

	/**
	 *
	 */
	public CmppAllCodeRequestMessageCodec() {
		this(CmppPacketType.CMPPALLCODEREQUEST);
	}

	public CmppAllCodeRequestMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	/**
	 *
	 * @param ctx
	 * @param msg
	 * @param out
	 * @throws Exception
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		int commandId =  msg.getHeader().getCommandId();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}
		CmppAllCodeRequestMessage codeRequest = new CmppAllCodeRequestMessage(msg.getHeader());
		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());


		codeRequest.setCode(bodyBuffer.readCharSequence(CmppAllCodeRequest.CODE.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());
		codeRequest.setVersion(bodyBuffer.readUnsignedByte());
		int jsonBodyLength = bodyBuffer.readInt();
		codeRequest.setJsonBodyLength(jsonBodyLength);


		codeRequest.setBody(bodyBuffer.readCharSequence(jsonBodyLength,GlobalConstance.defaultTransportCharset).toString().trim());

		out.add(codeRequest);
		ReferenceCountUtil.release(bodyBuffer);
	}

	/**
	 *
	 * @param ctx
	 * @param msg
	 * @param out
	 * @throws Exception
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, CmppAllCodeRequestMessage msg, List<Object> out) throws Exception {
		ByteBuf bodyBuffer =  ctx.alloc().buffer(CmppAllCodeRequest.CODE.getBodyLength());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getCode().getBytes(GlobalConstance.defaultTransportCharset),
				CmppAllCodeRequest.CODE.getLength(), 0));
		bodyBuffer.writeByte(msg.getVersion());
		bodyBuffer.writeInt(msg.getJsonBodyLength());
		bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(msg.getBody().getBytes(GlobalConstance.defaultTransportCharset),
				msg.getJsonBodyLength(), 0));
		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);
	}
}
