package com.kdecosta.lynx.energy;

import com.kdecosta.lynx.Lynx;

import java.io.*;

public class EnergyUnit implements Serializable {
    @Serial
    private static final long serialVersionUID = 6845346364204052962L;

    public static final long MAX_ENERGY = 999999999999999999L;
    public static final long MIN_ENERGY = 0L;

    private long energy;
    private long injectionRate;
    private long extractionRate;

    public EnergyUnit() {
        this(0L);
    }

    public EnergyUnit(long startingEnergy) {
        this.energy = startingEnergy;
        this.injectionRate = 0L;
        this.extractionRate = 0L;
    }

    public long getEnergy() {
        return this.energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    public void add(long energy) throws EnergyUnitTooLargeException {
        try {
            this.energy += energy;
            if (this.energy > MAX_ENERGY)
                throw new EnergyUnitTooLargeException("this energy unit exceeds maximum energy value");

            Lynx.LOGGER.info(String.format("In adder, %d ", this.energy));
        } catch (Exception e) {
            throw new EnergyUnitTooLargeException("this energy unit exceeds maximum energy value");
        }
    }

    public void add(EnergyUnit energy) throws EnergyUnitTooLargeException {
        this.add(energy.getEnergy());
    }

    public void subtract(long energy) {
        try {
            this.energy -= energy;
            if (this.energy < 0) this.energy = 0;
        } catch (Exception e) {
            this.energy = 0;
        }
    }

    public void subtract(EnergyUnit energy) {
        this.subtract(energy.getEnergy());
    }

    public void increment() throws EnergyUnitTooLargeException {
        this.add(1);
    }

    public void decrement() {
        this.subtract(1);
    }

    @Override
    public String toString() {
        return Long.toString(energy);
    }

    public long injectionRate() {
        return injectionRate;
    }

    public void setInjectionRate(long injectionRate) {
        this.injectionRate = injectionRate;
    }

    public long extractionRate() {
        return extractionRate;
    }

    public void setExtractionRate(long extractionRate) {
        this.extractionRate = extractionRate;
    }

    public void tick() throws EnergyUnitTooLargeException {
        this.add(injectionRate);
        this.subtract(extractionRate);
    }

    public static class EnergyUnitTooLargeException extends Exception {

        @Serial
        private static final long serialVersionUID = -7491850250898701885L;

        public EnergyUnitTooLargeException(String s) {
            super(s);
        }
    }

    public static byte[] getBytes(EnergyUnit energy) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(energy);
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

    public static EnergyUnit fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            if (o instanceof EnergyUnit energyUnit) return energyUnit;
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

}
