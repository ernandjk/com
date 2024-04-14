package com.onesignal;

import java.net.InetAddress;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class TLS12SocketFactory extends SSLSocketFactory
{
    SSLSocketFactory sslSocketFactory;
    
    public TLS12SocketFactory(final SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
    
    private Socket enableTLS(final Socket socket) {
        if (socket instanceof SSLSocket) {
            ((SSLSocket)socket).setEnabledProtocols(new String[] { "TLSv1.2" });
        }
        return socket;
    }
    
    public Socket createSocket() throws IOException {
        return this.enableTLS(this.sslSocketFactory.createSocket());
    }
    
    public Socket createSocket(final String s, final int n) throws IOException {
        return this.enableTLS(this.sslSocketFactory.createSocket(s, n));
    }
    
    public Socket createSocket(final String s, final int n, final InetAddress inetAddress, final int n2) throws IOException {
        return this.enableTLS(this.sslSocketFactory.createSocket(s, n, inetAddress, n2));
    }
    
    public Socket createSocket(final InetAddress inetAddress, final int n) throws IOException {
        return this.enableTLS(this.sslSocketFactory.createSocket(inetAddress, n));
    }
    
    public Socket createSocket(final InetAddress inetAddress, final int n, final InetAddress inetAddress2, final int n2) throws IOException {
        return this.enableTLS(this.sslSocketFactory.createSocket(inetAddress, n, inetAddress2, n2));
    }
    
    public Socket createSocket(final Socket socket, final String s, final int n, final boolean b) throws IOException {
        return this.enableTLS(this.sslSocketFactory.createSocket(socket, s, n, b));
    }
    
    public String[] getDefaultCipherSuites() {
        return this.sslSocketFactory.getDefaultCipherSuites();
    }
    
    public String[] getSupportedCipherSuites() {
        return this.sslSocketFactory.getSupportedCipherSuites();
    }
}
