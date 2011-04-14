package masquerade.sim.ui;

public interface ActionListener<T, R, A> {
	R onAction(T target, A action) throws Exception;
}
