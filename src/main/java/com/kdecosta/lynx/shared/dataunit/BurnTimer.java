package com.kdecosta.lynx.shared.dataunit;

import java.io.*;

public class BurnTimer implements Serializable {
    @Serial
    private static final long serialVersionUID = 877388033621677237L;

    private int timer;

    public BurnTimer() {
        this(0);
    }

    public BurnTimer(int startTime) {
        this.timer = startTime;
    }

    public void tick() {
        if (this.timer > 0) this.timer--;
    }

    public void set(int time) {
        this.timer = time;
    }

    public int remaining() {
        return timer;
    }

    public int remainingInSeconds() {
        return timer / 20;
    }

    public void setInSeconds(int seconds) {
        this.timer = 20 * seconds;
    }

    public boolean isTimerDone() {
        return timer > 0;
    }

    @Override
    public String toString() {
        return Integer.toString(timer);
    }

    public static byte[] getBytes(BurnTimer timer) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(timer);
            out.flush();
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static BurnTimer fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            if (o instanceof BurnTimer timer) return timer;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return null;
    }

    public void reset() {
        this.timer = 0;
    }
}
