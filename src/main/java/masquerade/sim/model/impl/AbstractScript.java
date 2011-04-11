package masquerade.sim.model.impl;

import masquerade.sim.model.Script;

/**
 * Script base class definition common properties
 */
public abstract class AbstractScript implements Script {

	private String name;
	private String description = "";

	public AbstractScript(String name) {
		super();
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	/**
     * @param description Short description of this script
     */
    public void setDescription(String description) {
    	this.description = description;
    }
}
