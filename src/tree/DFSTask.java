package tree;

import java.util.function.Consumer;

public abstract class DFSTask implements Consumer<Node<?>> {
	protected Object[] obj;
	public DFSTask(Object... obj) {
		this.obj = obj;
	}
}
