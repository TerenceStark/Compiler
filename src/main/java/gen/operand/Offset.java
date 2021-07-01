package gen.operand;

import gen.operand.Operand;

public class Offset extends Operand {

    int offset;
    public Offset(int offset) {
        super();
        this.offset = offset;
    }

    @Override
    public String toString() {
        return this.offset + "";
    }

    public int getOffset(){
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getEncodedOffset() {
       /*
       * |--code--|--offset--|
       * 0x400 = 0b100 0000 0000
       * -3 = 0b100 0000 0011
       * */
        if(offset > 0) {
            return offset;
        }
        return 0x400 | -offset;
    }

    public static Offset decodeOffset(int offset) {
        /*
        * 0x400 = 0b100 0000 0000
        * 0x3ff = 0b011 1111 1111
        * -3 = 0b100 0000 011
        * -3 & 0x3ff = 0b000 0000 0011 = 3
        * offset = -offset
        * */
        if( (offset & 0x400) > 0 ) {
            offset = offset & 0x3ff;
            offset = - offset;
        }
        return new Offset(offset);
    }
}
