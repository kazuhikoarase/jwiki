package jwiki.core;

/**
 * ILine
 * @author kazuhiko arase
 */
public interface ILine<T> {
	int getLineNumber();
	T get();
}