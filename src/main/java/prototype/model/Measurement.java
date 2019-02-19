package prototype.model;

import java.io.Serializable;

/*
A Class for storing current Position as Coordinate and Signal Strength to next Mast.
 */
public final class Measurement implements Serializable {
    private int _1;
    private int _2;
    private int signalStrength;

    public Measurement(int _1, int _2) {
        this._1 = _1;
        this._2 = _2;
        this.signalStrength = 0;
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



    public boolean equals(Measurement obj) {
        return obj._1 == this._1 && obj._2 == this._2;
    }

    @Override
    public String toString() {
        return "(" + this._1 + "|" + this._2 + ") Signal ["
                + signalStrength + "/10]";
    }

	public int getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(int signalStrength) {
		this.signalStrength = signalStrength;
	}
}
