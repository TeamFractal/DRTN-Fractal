package io.github.teamfractal.exception;

public class InvalidControlCharException extends RuntimeException {

	public InvalidControlCharException(char c, int pos) {
		super(String.format("Unknown escape char 0x%04x(%c) at position %d", c, c, pos));
	}
}
