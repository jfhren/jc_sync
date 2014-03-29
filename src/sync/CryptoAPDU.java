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

package sync;

import javacard.security.Key;

/**
 * This interface describes the methods required for encrypting and decrypting the data contained in an APDU.
 */
interface CryptoAPDU {

    /**
     * Initializing parameters for the cipher.
     * @param key The key for the cipher.
     * @param bArray Parameters for the cipher.
     */
    public void init(Key key, byte bArray[]);  

    /**
     * Encrypt the current APDU buffer starting from offset and going on for len bytes.
     * @param offset Where should the encryption start from in the current APDU buffer.
     * @param len The length of the section to encrypt.
     * @return Return the number of bytes encrypted rounded up to the block size of the cipher or return -1 in case of error.
     */
    public short encryptAPDU(short offset, short len);

    /**
     * Decrypt the current APDU buffer starting from offset and going on for len bytes.
     * @param offset Where should the decryption start from in the current APDU buffer.
     * @param len The length of the section to decrypt.
     * @return Return true if the decryption is successful, false else.
     */
    public boolean decryptAPDU(short offset, short len);

    /**
     * Return the maximum usable length in the APDU buffer (usually 128bytes).
     * @return The length.
     */
    public short getMaxAPDUBufferLength();

}