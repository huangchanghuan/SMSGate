package com.zx.sms.codec.cmpp.msg;

import com.zx.sms.codec.cmpp.packet.CmppPacketType;
import com.zx.sms.common.GlobalConstance;

/**
 * 
 *
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppAllCodeRequestMessage extends DefaultMessage {
	private static final long serialVersionUID = -4852540410843279972L;
	private String code = "100";
	private short version = 0x10;
	private int jsonBodyLength;
	private String body ;

	public CmppAllCodeRequestMessage(Header header) {
		super(CmppPacketType.CMPPALLCODEREQUEST, header);
	}

	public CmppAllCodeRequestMessage() {
		super(CmppPacketType.CMPPALLCODEREQUEST);
	}

	/**
	 * @return the version
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return String.format("CmppAllCodeRequestMessage [version=%s, code=%s, sequenceId=%s]", version, code, getHeader().getSequenceId());
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getJsonBodyLength() {
		return jsonBodyLength;
	}

	public void setJsonBodyLength(int jsonBodyLength) {
		this.jsonBodyLength = jsonBodyLength;
	}
}
