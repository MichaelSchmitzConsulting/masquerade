package masquerade.sim.model;

import masquerade.sim.model.impl.AbstractChannel;

public class ChannelStub extends AbstractChannel {

	private String someProperty = "x";
	private String name;
	
	public ChannelStub(String name) {
		super(name);
		this.name = name;
	}
	
	@Override
	public Class<? extends ChannelListener<? extends Channel>> listenerType() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isActive() {
		throw new UnsupportedOperationException();
	}

	public String getSomeProperty() {
		return someProperty;
	}

	public void setSomeProperty(String someProperty) {
		this.someProperty = someProperty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((someProperty == null) ? 0 : someProperty.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChannelStub other = (ChannelStub) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (someProperty == null) {
			if (other.someProperty != null)
				return false;
		} else if (!someProperty.equals(other.someProperty))
			return false;
		return true;
	}
}
