package masquerade.sim.model.impl;

import masquerade.sim.model.Script;

/**
 * Script base class definition common properties
 */
public abstract class AbstractScript implements Script {

	private String description = "";

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
