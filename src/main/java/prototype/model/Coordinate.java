package prototype.model;

import java.io.Serializable;

public final class Coordinate implements Serializable {
    private int _1;
    private int _2;
    private int signalPower;

    public Coordinate(int _1, int _2) {
        this._1 = _1;
        this._2 = _2;
        this.signalPower = 0;
    }


    public int get_1() {
        return _1;
    }

    public void set_1(int _1) {
        this._1 = _1;
    }

    public int get_2() {
        return _2;
    }

    public void set_2(int _2) {
        this._2 = _2;
    }

    public int getSignalPower() {
        return signalPower;
    }

    public void setSignalPower(final int signalPower) {
        this.signalPower = signalPower;
    }


    public boolean equals(Coordinate obj) {
        return obj._1 == this._1 && obj._2 == this._2;
    }

    @Override
    public String toString() {
        return "(" + this._1 + "|" + this._2 + ") Signal ["
                + signalPower + "/10]";
    }
}
