package mathobjects;

public interface MathObject {
	public abstract MathObject negate();
	public abstract MathObject invert();
	public abstract MathObject copy();
	public abstract MathObject evaluate();
	@Override
	public abstract String toString();
}
