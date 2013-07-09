package jwiki.core.impl;

import jwiki.core.ILine;

/**
 * Line
 * @author kazuhiko arase
 */
public class Line<T> implements ILine<T> {
	private final int lineNumber;
	private final T data;
	public Line(int lineNumber, T data) {
		this.lineNumber = lineNumber;
		this.data = data;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public T get() {
		return data;
	}
}