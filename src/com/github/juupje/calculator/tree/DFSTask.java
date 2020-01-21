package com.github.juupje.calculator.tree;

import java.util.function.Consumer;

public abstract class DFSTask implements Consumer<Node<?>> {
	protected Object[] obj;
	protected boolean usesFlags = false;
	
	public DFSTask() {
		this(false);
	}
	public DFSTask(boolean usesFlags) {
		this.usesFlags = usesFlags;
	}
	
	public DFSTask(boolean usesFlags, Object... obj) {
		this.usesFlags = usesFlags;
		this.obj = obj;
	}
	
	public void setUsesFlags(boolean usesFlags) {
		this.usesFlags = usesFlags;
	}
	
	public boolean usesFlags() {
		return usesFlags;
	}
}
