package com.phonecompany.billing;

import java.time.LocalDateTime;

public class Call {
	private String number;
	private LocalDateTime callStart;
	private LocalDateTime callStop;

	public Call(String number, LocalDateTime callStart, LocalDateTime callStop) {
		this.number = number;
		this.callStart = callStart;
		this.callStop = callStop;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public LocalDateTime getCallStart() {
		return callStart;
	}

	public void setCallStart(LocalDateTime callStart) {
		this.callStart = callStart;
	}

	public LocalDateTime getCallStop() {
		return callStop;
	}

	public void setCallStop(LocalDateTime callStop) {
		this.callStop = callStop;
	}
}
