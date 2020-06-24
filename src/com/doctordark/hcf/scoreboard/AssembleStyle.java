package com.doctordark.hcf.scoreboard;

import lombok.Getter;

@Getter
public enum AssembleStyle {

    KOHI(true, 15),
    VIPER(true, -1),
    MODERN(false, 1);

    private boolean decending;
    public boolean isDecending() {
		return decending;
	}

	public void setDecending(boolean decending) {
		this.decending = decending;
	}

	public int getStartNumber() {
		return startNumber;
	}

	public void setStartNumber(int startNumber) {
		this.startNumber = startNumber;
	}

	private int startNumber;

    AssembleStyle(boolean decending, int startNumber) {
        this.decending = decending;
        this.startNumber = startNumber;
    }
}
