/**
 * 
 */
package com.zx.sms.codec.cmpp.packet;



/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public enum CmppAllCodeRequest implements PacketStructure {
    CODE(CmppDataType.OCTERSTRING, true, 3),
    VERSION(CmppDataType.UNSIGNEDINT, true, 1),
    JSONBODYLENGTH(CmppDataType.UNSIGNEDINT, true, 4),
    BODY(CmppDataType.OCTERSTRING, false, 0);
    private DataType dataType;
    private boolean isFixFiledLength;
    private int length;
    private  int bodyLength =100;

    private CmppAllCodeRequest(DataType dataType, boolean isFixFiledLength, int length) {
        this.dataType = dataType;
        this.isFixFiledLength = isFixFiledLength;
        this.length = length;
    }
    public DataType getDataType() {
        return dataType;
    }
    public boolean isFixFiledLength() {
        return isFixFiledLength;
    }
    public boolean isFixPacketLength() {
    	return true;
    }
    public int getLength() {
        return length;
    }
    public int getBodyLength() {
        return bodyLength;
    }
}
