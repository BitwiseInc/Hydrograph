/*******************************************************************************
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

 

package hydrograph.ui.graph.figure;

import hydrograph.ui.graph.model.PortAlignmentEnum;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScalableFigure;
import org.eclipse.draw2d.geometry.Point;


/**
 * The Class FixedConnectionAnchor.
 * Implementation for connection anchor used for linking 2 components in canvas.
 * 
 * @author Bitwise
 */
public class FixedConnectionAnchor extends AbstractConnectionAnchor {

	private boolean allowMultipleLinks, linkMandatory;
	private String alignment;
	private int totalPortsOfThisType;
	private int sequence;
	private String terminal;

	

	/**
	 * Instantiates a new fixed connection anchor.
	 * 
	 * @param owner
	 *            the owner
	 * @param align
	 *            the type
	 * @param totalPortsOfThisType
	 *            the total ports of this type
	 * @param sequence
	 *            the sequence
	 * @param terminal 
	 * 			Port terminal
	 */
	public FixedConnectionAnchor(IFigure owner, String align, int totalPortsOfThisType, int sequence, String terminal) {
		super(owner);
		this.alignment=align;
		this.totalPortsOfThisType=totalPortsOfThisType;
		this.sequence=sequence;
		this.terminal = terminal;
	}

	/**
	 * @see org.eclipse.draw2d.AbstractConnectionAnchor#ancestorMoved(IFigure)
	 */
	@Override
	public void ancestorMoved(IFigure figure) {
		if (figure instanceof ScalableFigure)
			return;
		super.ancestorMoved(figure);
	}
	

	@Override
	public Point getLocation(Point arg0) {
		int xLocation =0, yLocation = 0;
		
			
		if(PortAlignmentEnum.LEFT.value().equalsIgnoreCase(this.alignment)){
			 xLocation=getOwner().getBounds().getTopLeft().x-1;
			 yLocation=getOwner().getBounds().getTopLeft().y+4;
		}else if(PortAlignmentEnum.RIGHT.value().equalsIgnoreCase(this.alignment)){
			 xLocation=getOwner().getBounds().getTopRight().x-1;
			 yLocation=getOwner().getBounds().getTopRight().y+4;
		}else if(PortAlignmentEnum.BOTTOM.value().equalsIgnoreCase(this.alignment)){
			 xLocation=getOwner().getBounds().getBottomRight().x-20;
			 yLocation=getOwner().getBounds().getBottomRight().y-2;
		}
		
		Point point= new Point(xLocation, yLocation);
		getOwner().getParent().translateToAbsolute(point);
		return point;
	}
		
	/**
	 * Gets the terminal.
	 * 
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}

	/**
	 * Gets the alignment.
	 * 
	 * @return the alignment
	 */
	public String getAlignment() {
		return alignment;
	}

	/**
	 * Gets the sequence.
	 * 
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * Sets the allow multiple links.
	 * 
	 * @param allowMultipleLinks
	 *            the new allow multiple links
	 */
	public void setAllowMultipleLinks(boolean allowMultipleLinks) {
		this.allowMultipleLinks = allowMultipleLinks;
	}

	/**
	 * Sets the link mandatory.
	 * 
	 * @param linkMandatory
	 *            the new link mandatory
	 */
	public void setLinkMandatory(boolean linkMandatory) {
		this.linkMandatory = linkMandatory;
	}

	/**
	 * Gets the total ports of this type.
	 * 
	 * @return the total ports of this type
	 */
	public int getTotalPortsOfThisType() {
		return totalPortsOfThisType;
	}

	
	@Override
	public String toString() {
				
		 return "\n******************************************"+
				"\nOwner: "+getOwner()+
				"\nallowMultipleLinks: "+this.allowMultipleLinks+
				"\nlinkMandatory: "+this.linkMandatory+
				"\nalignment: "+this.alignment+
				"\nsequence: "+this.sequence+
				"\nterminal: "+this.terminal+
				"\ntotalPortsOfThisType: "+this.totalPortsOfThisType+
				"\n******************************************\n";
		 
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof FixedConnectionAnchor) {
			FixedConnectionAnchor fa = (FixedConnectionAnchor) o;
			
			if ( fa.getOwner() == this.getOwner() &&
					fa.getAlignment().equals(this.alignment) &&
					fa.getTerminal().equals(this.terminal) &&
					fa.getTotalPortsOfThisType()==this.totalPortsOfThisType&&
					fa.getSequence() == this.sequence &&
					fa.allowMultipleLinks == this.allowMultipleLinks &&
					fa.linkMandatory == this.linkMandatory
				)
				return true;
			
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 17;
		int var1 = allowMultipleLinks?1:0;
		int var2 = linkMandatory?1:0;
		result = 31 * result + var1;
		result = 31 * result + var2;
		
		return result;
		
		
	}
	
}
