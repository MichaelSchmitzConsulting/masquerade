package masquerade.sim.app.ui2.view;

/**
 * Channel information as displayed in the {@link ChannelView}
 */
public class ChannelInfo {
	private final String id;
	private final String  type;
	private final boolean isPersistent;
	public ChannelInfo(String id, String type, boolean isPersistent) {
		this.id = id;
		this.type = type;
		this.isPersistent = isPersistent;
	}
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public boolean isPersistent() {
		return isPersistent;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ChannelInfo other = (ChannelInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}