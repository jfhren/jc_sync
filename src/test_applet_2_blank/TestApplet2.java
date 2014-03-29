/*
 * Copyright Inria:
 * Jean-François Hren
 * 
 * jfhren[at]gmail[dot]com
 * michael[dot]hauspie[at]lifl[dot]com
 * 
 * This software is a computer program whose purpose is to synchronize
 * Java Card 2 applet data.
 * 
 * This software is governed by the CeCILL-B license under French
 * law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the
 * CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its
 * terms.
 */

package test_applet_2_blank;

import javacard.framework.Applet;
import javacard.framework.AppletEvent;
import javacard.framework.APDU;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;

public class TestApplet2 extends Applet implements AppletEvent {

    private boolean bTrue = true;
    private boolean bFalse = false;
    private byte b = (byte)0x42;
    private short s = (short)0xDEAD;
    private final static short booleanArraySize = (short)32;
    private boolean[] booleanArray;
    private final static short bArraySize = (short)32;
    private static byte[] bArray;
    private short[] sArray;
    SomeTestClass2 someTestClass2 = null;

    public TestApplet2() {

        booleanArray = new boolean[booleanArraySize];
        for(short i = (short)0; i < booleanArraySize; i++)
            booleanArray[i] = ((i % 2) == 0);

        bArray = new byte[bArraySize];
        for(short i = (short)0; i < bArraySize; i++)
            bArray[i] = (byte)i;

        new SomeTestClass2();
        sArray = SomeTestClass2.sArray;

    }


    public static void install(byte[] bArray, short bOffset, byte bLength)
        throws ISOException {

        if(bArray[bOffset] == (byte)0)
            new TestApplet2().register();
        else
            new TestApplet2().register(bArray, (short)(bOffset + 1), bArray[bOffset]);

    }

    private void tweak_fields(APDU apdu) {

        byte[] buffer = apdu.getBuffer();
        byte ins = buffer[ISO7816.OFFSET_INS];
        boolean isSet = (ins >> 4) == (byte)0x00;
        boolean isGet = (ins >> 4) == (byte)0x01;
        boolean getArraySize = (ins >> 4) == (byte)0x02;
        boolean setArraySize = (ins >> 4) == (byte)0x03;
        byte p1 = buffer[ISO7816.OFFSET_P1];
        byte p2 = buffer[ISO7816.OFFSET_P2];

        if(ins == ISO7816.INS_SELECT)
            return;

        switch(ins & (byte)0x0F) {
            case (byte)0x00: /* booleanArray */
                if(isSet) {
                    booleanArray[(short)p1] = (p2 == (byte)1) ? true : false;
                } else if(getArraySize) {
                    buffer[0] = (byte)(booleanArray.length >> 8);
                    buffer[1] = (byte)(booleanArray.length & (short)0xFF);
                    apdu.setOutgoingAndSend((short)0, (short)2);
                } else if (setArraySize) {
                    boolean[] newArray = null;
                    short newArraySize = (short)(p1 & 0xFF);
                    byte type = JCSystem.isTransient(booleanArray);

                    if(type == JCSystem.NOT_A_TRANSIENT_OBJECT)
                        newArray = new boolean[newArraySize];
                    else
                        newArray = JCSystem.makeTransientBooleanArray(newArraySize, type);

                    if(newArraySize > booleanArray.length)
                        for(short i = 0; i < booleanArray.length; i++)
                            newArray[i] = booleanArray[i];
                    else
                        for(short i = 0; i < newArraySize; i++)
                            newArray[i] = booleanArray[i];

                    booleanArray = newArray;
                    JCSystem.requestObjectDeletion();
                } else if(isGet) {
                    buffer[0] = booleanArray[(short)p1] ? (byte)1 : (byte)0;
                    apdu.setOutgoingAndSend((short)0, (short)1);
                }
                break;

            case (byte)0x01: /* bArray */
                if(isSet) {
                    bArray[(short)p1] = p2;
                } else if(getArraySize) {
                    buffer[0] = (byte)(bArray.length >> 8);
                    buffer[1] = (byte)(bArray.length & (short)0xFF);
                    apdu.setOutgoingAndSend((short)0, (short)2);
                } else if (setArraySize) {
                    byte[] newArray = null;
                    short newArraySize = (short)(p1 & 0xFF);
                    byte type = JCSystem.isTransient(bArray);

                    if(type == JCSystem.NOT_A_TRANSIENT_OBJECT)
                        newArray = new byte[newArraySize];
                    else
                        newArray = JCSystem.makeTransientByteArray(newArraySize, type);

                    if(newArraySize > bArray.length)
                        for(short i = 0; i < bArray.length; i++)
                            newArray[i] = bArray[i];
                    else
                        for(short i = 0; i < newArraySize; i++)
                            newArray[i] = bArray[i];

                    bArray = newArray;
                    JCSystem.requestObjectDeletion();
                } else if(isGet) {
                    buffer[0] = bArray[(short)p1];
                    apdu.setOutgoingAndSend((short)0, (short)1);
                }
                break;

            case (byte)0x02: /* SomeTestClass2.sArray */
                if(isSet) {
                    SomeTestClass2.sArray[(short)p1] = (short)(((short)p2) & 0x00FF);
                } else if(getArraySize) {
                    buffer[0] = (byte)(SomeTestClass2.sArray.length >> 8);
                    buffer[1] = (byte)(SomeTestClass2.sArray.length & (short)0xFF);
                    apdu.setOutgoingAndSend((short)0, (short)2);
                } else if(setArraySize) {
                    short[] newArray = null;
                    short newArraySize = (short)(p1 & 0xFF);
                    byte type = JCSystem.isTransient(SomeTestClass2.sArray);

                    if(type == JCSystem.NOT_A_TRANSIENT_OBJECT)
                        newArray = new short[newArraySize];
                    else
                        newArray = JCSystem.makeTransientShortArray(newArraySize, type);

                    if(newArraySize > SomeTestClass2.sArray.length)
                        for(short i = 0; i < SomeTestClass2.sArray.length; i++)
                            newArray[i] = SomeTestClass2.sArray[i];
                    else
                        for(short i = 0; i < newArraySize; i++)
                            newArray[i] = SomeTestClass2.sArray[i];

                    SomeTestClass2.sArray = newArray;
                    JCSystem.requestObjectDeletion();
                } else if (isGet) {
                    buffer[0] = (byte)(SomeTestClass2.sArray[(short)p1] & 0xFF);
                    apdu.setOutgoingAndSend((short)0, (short)1);
                }
                break;

            case (byte)0x03: /*bTrue*/
                if(isSet) {
                    bTrue = (p2 == (byte)0) ? false : true;
                } else if(isGet) {
                    if(someTestClass2 != null)
                        buffer[0] = !bTrue ? (byte)1 : (byte)0;
                    else
                        buffer[0] = bTrue ? (byte)1 : (byte)0;
                    apdu.setOutgoingAndSend((short)0, (short)1);
                }
                break;

            case (byte)0x04: /*bFalse*/
                if(isSet) {
                    bFalse = (p2 == (byte)0) ? false : true;
                } else if(isGet) {
                    buffer[0] = bFalse ? (byte)1 : (byte)0;
                    apdu.setOutgoingAndSend((short)0, (short)1);
                }
                break;

            case (byte)0x05: /* b */
                if(isSet) {
                    b = p2;
                } else if(isGet) {
                    buffer[0] = b;
                    apdu.setOutgoingAndSend((short)0, (short)1);
                }
                break;

            case (byte)0x06: /* s */
                if(isSet) {
                    s = (short)((((short)p1) << 8)| (short)p2);
                } else if(isGet) {
                    buffer[0] = (byte)(s >> 8);
                    buffer[1] = (byte)(s & 0xFF);
                    apdu.setOutgoingAndSend((short)0, (short)2);
                }
        }

    }

    public void process(APDU apdu)
        throws ISOException {

        tweak_fields(apdu);

    }

    public void uninstall() {

        SomeTestClass2.sArray = null;
        bArray = null;
        JCSystem.requestObjectDeletion();
        
   }

}
