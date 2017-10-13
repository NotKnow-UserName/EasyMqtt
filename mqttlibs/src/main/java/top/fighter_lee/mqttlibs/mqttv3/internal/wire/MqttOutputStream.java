/*******************************************************************************
 * Copyright (c) 2009, 2014 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Dave Locke - initial API and implementation and/or initial documentation
 */
package top.fighter_lee.mqttlibs.mqttv3.internal.wire;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import top.fighter_lee.mqttlibs.mqttv3.MqttException;
import top.fighter_lee.mqttlibs.mqttv3.internal.ClientState;


/**
 * An <code>MqttOutputStream</code> lets applications write instances of
 * <code>MqttWireMessage</code>. 
 */
public class MqttOutputStream extends OutputStream {
	private static final String CLASS_NAME = MqttOutputStream.class.getName();

	private ClientState clientState = null;
	private BufferedOutputStream out;
	
	public MqttOutputStream(ClientState clientState, OutputStream out) {
		this.clientState = clientState;
		this.out = new BufferedOutputStream(out);
	}
	
	public void close() throws IOException {
		out.close();
	}
	
	public void flush() throws IOException {
		out.flush();
	}
	
	public void write(byte[] b) throws IOException {
		out.write(b);
		clientState.notifySentBytes(b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		clientState.notifySentBytes(len);
	}
	
	public void write(int b) throws IOException {
		out.write(b);
	}

	/**
	 * Writes an <code>MqttWireMessage</code> to the stream.
	 * @param message The {@link MqttWireMessage} to send
	 * @throws IOException if an exception is thrown when writing to the output stream.
	 * @throws MqttException if an exception is thrown when getting the header or payload
	 */
	public void write(MqttWireMessage message) throws IOException, MqttException {
		final String methodName = "write";
		byte[] bytes = message.getHeader();
		byte[] pl = message.getPayload();
//		out.write(message.getHeader());
//		out.write(message.getPayload());
		out.write(bytes,0,bytes.length);
		clientState.notifySentBytes(bytes.length);
		
        int offset = 0;
        int chunckSize = 1024;
        while (offset < pl.length) {
        	int length = Math.min(chunckSize, pl.length - offset);
        	out.write(pl, offset, length);
        	offset += chunckSize;
        	clientState.notifySentBytes(length);
        }		
		
		// @TRACE 529= sent {0}
	}
}

