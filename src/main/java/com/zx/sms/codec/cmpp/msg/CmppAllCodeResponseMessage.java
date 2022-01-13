package com.zx.sms.codec.cmpp.msg;

import com.zx.sms.codec.cmpp.packet.CmppPacketType;

/**
 * 
 *
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppAllCodeResponseMessage extends DefaultMessage {
	private static final long serialVersionUID = -4852540410843278972L;
	private String code = "100";
	private short version = 0x10;
	private int jsonBodyLength;
	private String body ;

	public CmppAllCodeResponseMessage(int sequenceId) {
		super(CmppPacketType.CMPPALLCODERESPONSE, sequenceId);
	}
	public CmppAllCodeResponseMessage(Header header) {
		super(CmppPacketType.CMPPALLCODERESPONSE, header);
	}

	public CmppAllCodeResponseMessage() {
		super(CmppPacketType.CMPPALLCODERESPONSE);
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
		return String.format("CmppAllCodeResponseMessage [version=%s, code=%s, sequenceId=%s, body=%s]", version, code, getHeader().getSequenceId(),body);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getJsonBodyLength() {
		return jsonBodyLength;
	}

	public void setJsonBodyLength(int jsonBodyLength) {
		this.jsonBodyLength = jsonBodyLength;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
