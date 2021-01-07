package mech.mania.engine.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.concurrent.TimeUnit;

/**
 * An alternative for BufferedReader that has a millis timeout.
 * https://stackoverflow.com/questions/804951/is-it-possible-to-read-from-a-inputstream-with-a-timeout
 *
 * @author Dario
 */
public class SafeBufferedReader extends BufferedReader {

    private long millisTimeout;

    private long millisInterval = 100;

    public SafeBufferedReader(Reader in, int sz, long millisTimeout) {
        super(in, sz);
        this.millisTimeout = millisTimeout;
    }

    public SafeBufferedReader(Reader in, long millisTimeout) {
        super(in);
        this.millisTimeout = millisTimeout;
    }

    /**
     * This is probably going to kill readLine performance. You should study BufferedReader and completely override the method.
     *
     * It should mark the position, then perform its normal operation in a nonblocking way, and if it reaches the timeout then reset position and throw IllegalThreadStateException
     */
    @Override
    public String readLine() throws IOException {
        waitReadyLine();
        return super.readLine();
    }

    @Override
    public int read() throws IOException {
        try {
            waitReady();
        } catch(IllegalThreadStateException e) {
            return -2; // I'd throw a runtime here, as some people may just be checking if int < 0 to consider EOS
        }
        return super.read();
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        try {
            waitReady();
        } catch(IllegalThreadStateException e) {
            return -2;  // I'd throw a runtime here, as some people may just be checking if int < 0 to consider EOS
        }
        return super.read(cbuf);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        try {
            waitReady();
        } catch(IllegalThreadStateException e) {
            return 0;
        }
        return super.read(cbuf, off, len);
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        try {
            waitReady();
        } catch(IllegalThreadStateException e) {
            return 0;
        }
        return super.read(target);
    }

    public long getMillisTimeout() {
        return millisTimeout;
    }

    public void setMillisTimeout(long millisTimeout) {
        this.millisTimeout = millisTimeout;
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        this.millisTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public long getMillisInterval() {
        return millisInterval;
    }

    public void setMillisInterval(long millisInterval) {
        this.millisInterval = millisInterval;
    }

    public void setInterval(long time, TimeUnit unit) {
        this.millisInterval = TimeUnit.MILLISECONDS.convert(time, unit);
    }

    /**
     * This is actually forcing us to read the buffer twice in order to determine a line is actually ready.
     *
     * @throws IllegalThreadStateException
     * @throws IOException
     */
    protected void waitReadyLine() throws IllegalThreadStateException, IOException {
        long timeout = System.currentTimeMillis() + millisTimeout;
        waitReady();

        super.mark(1);
        try {
            while(System.currentTimeMillis() < timeout) {
                while(ready()) {
                    int charInt = super.read();
                    if(charInt==-1) return; // EOS reached
                    char character = (char) charInt;
                    if(character == '\n' || character == '\r' ) return;
                }
                try {
                    Thread.sleep(millisInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore flag
                    break;
                }
            }
        } finally {
            super.reset();
        }
        throw new IllegalThreadStateException("readLine timed out");

    }

    protected void waitReady() throws IllegalThreadStateException, IOException {
        if(ready()) return;
        long timeout = System.currentTimeMillis() + millisTimeout;
        while(System.currentTimeMillis() < timeout) {
            if(ready()) return;
            try {
                Thread.sleep(millisInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore flag
                break;
            }
        }
        if(ready()) return; // Just in case.
        throw new IllegalThreadStateException("read timed out");
    }

}
