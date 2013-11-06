import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

public class PhotoCornersLayout implements LayoutManager2 {

	protected Map<Component, Constraint> constraintsMap = new HashMap<>();
	
	@Override
	@Deprecated
	public void addLayoutComponent(String name, Component comp) {
		// Do nothing
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			for(Component c: parent.getComponents()){
				layoutComponent(parent, c);
			}
		}
	}
	
	/** 
	 * lays out the specified component
	 * @param parent containing the component
	 * @param child component to be laid out (sized and positioned)
	 */
	private void layoutComponent(Container parent, Component child){
		float width = parent.getWidth();
		float height = parent.getHeight();
		Constraint c = constraintsMap.get(child);
		if(c==null){
			child.doLayout();
			return;
		}
		
		int x1Pos = 0;
		if(c.isRelativeXL){
			if(c.isLeftAnchored){
				x1Pos =(int) (width*c.xL);
			} else {
				x1Pos =(int) (width - (width*c.xL));
			}
		} else {
			if(c.isLeftAnchored){
				x1Pos = (int)c.xL;
			} else {
				x1Pos = (int) (width - c.xL);
			}
		}
		
		int y1Pos = 0;
		if(c.isRelativeYT){
			if(c.isTopAnchored){
				y1Pos = (int) (height * c.yT);
			} else {
				y1Pos =(int) (height - (height*c.yT));
			}
		} else {
			if(c.isTopAnchored){
				y1Pos = (int) c.yT;
			} else {
				y1Pos = (int) (height - c.yT);
			}
		}
		
		int x2Pos = 0;
		if(c.isRelativeXR){
			if(c.isRightAnchored){
				x2Pos =(int) (width - (width*c.xR));
			} else {
				x2Pos =(int) (width*c.xR);
			}
		} else {
			if(c.isRightAnchored){
				x2Pos = (int) (width - c.xR);
			} else {
				x2Pos = (int)c.xR;
			}
		}
		
		int y2Pos = 0;
		if(c.isRelativeYB){
			if(c.isBottomAnchored){
				y2Pos =(int) (height - (height*c.yB));
			} else {
				y2Pos = (int) (height * c.yB);
			}
		} else {
			if(c.isBottomAnchored){
				y2Pos = (int) (height - c.yB);
			} else {
				y2Pos = (int) c.yB;
			}
		}
		
		// apply
		System.out.println("x1 " + x1Pos);
		System.out.println("x2 " + x2Pos);
		System.out.println("y1 " + y1Pos);
		System.out.println("y2 " + y2Pos);
		child.setSize(x2Pos-x1Pos, y2Pos-y1Pos);
		child.setLocation(x1Pos, y1Pos);
	}
	

	public void addLayoutComponent(Component comp, Object constraints) {
		if(constraints == null){
			System.err.println("Warning by " + this.getClass().getCanonicalName() + 
					": no constraint specified for Component \n\t" + 
					comp.toString() + "\n\tof Class " + comp.getClass().getCanonicalName()+
					"\n\tComponent will possibly be displayed incorrectly.");
		} else if(!(constraints instanceof String)){
			System.err.println("Warning by " + this.getClass().getName() + 
					": unsupported constraint specified for Component \n\t" + 
					comp.toString() + "\n\tof Class " + comp.getClass().getName() +
					"\n\tConstraint will be ignored. Component will possibly be displayed incorrectly.");
		} else {
			
		Constraint c = Constraint.parseConstraints((String) constraints);
		constraintsMap.put(comp, c);
		}

	}

	@Override
	public void removeLayoutComponent(Component comp) {
		constraintsMap.remove(comp);
	}
	
	@Override
	@Deprecated
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	@Deprecated
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	@Deprecated
	public void invalidateLayout(Container target) {
	}
	
	
	protected static class Constraint {
		boolean isRelativeXL;
		boolean isLeftAnchored;
		float xL;
		
		boolean isRelativeYT;
		boolean isTopAnchored;
		float yT;
		
		boolean isRelativeXR;
		boolean isRightAnchored;
		float xR;
		
		boolean isRelativeYB;
		boolean isBottomAnchored;
		float yB;
		
		static Constraint parseConstraints(String constraints) {
			Constraint c = new Constraint();
			try {
			String tlString = constraints.toLowerCase().replace("topleft(", "#");
			String brString = constraints.toLowerCase().replace("bottomright(", "#");
			
			int i = tlString.indexOf('#');
			tlString = tlString.substring(i+1, tlString.indexOf(')', i));
			
			i = brString.indexOf('#');
			brString = brString.substring(i+1, brString.indexOf(')', i));
			
			String[] tl = tlString.split(",");
			tl[0] = tl[0].trim();
			tl[1] = tl[1].trim();
			
			String[] br = brString.split(",");
			br[0] = br[0].trim();
			br[1] = br[1].trim();
			
			c.isLeftAnchored = !tl[0].contains("-");
			c.isTopAnchored = !tl[1].contains("-");
			c.isRightAnchored = br[0].contains("-");
			c.isBottomAnchored = br[1].contains("-");
			
			c.isRelativeXL = tl[0].contains(".")||tl[0].contains("/");
			c.isRelativeYT = tl[1].contains(".")||tl[1].contains("/");
			c.isRelativeXR = br[0].contains(".")||br[0].contains("/");
			c.isRelativeYB = br[1].contains(".")||br[1].contains("/");
			
			if(tl[0].contains("/")){
				float f1 = Float.parseFloat(tl[0].split("/")[0]);
				float f2 = Float.parseFloat(tl[0].split("/")[1]);
				c.xL = Math.abs(f1/f2);
			} else {
				c.xL = Math.abs(Float.parseFloat(tl[0]));
			}
			
			if(tl[1].contains("/")){
				float f1 = Float.parseFloat(tl[1].split("/")[0]);
				float f2 = Float.parseFloat(tl[1].split("/")[1]);
				c.yT = Math.abs(f1/f2);
			} else {
				c.yT = Math.abs(Float.parseFloat(tl[1]));
			}
			
			if(br[0].contains("/")){
				float f1 = Float.parseFloat(br[0].split("/")[0]);
				float f2 = Float.parseFloat(br[0].split("/")[1]);
				c.xR = Math.abs(f1/f2);
			} else {
				c.xR = Math.abs(Float.parseFloat(br[0]));
			}
			
			if(br[1].contains("/")){
				float f1 = Float.parseFloat(br[1].split("/")[0]);
				float f2 = Float.parseFloat(br[1].split("/")[1]);
				c.yB = Math.abs(f1/f2);
			} else {
				c.yB = Math.abs(Float.parseFloat(br[1]));
			}
			} catch(Exception e) {
				synchronized (System.err) {
					System.err.println("Exception caught while parsing constraints :" + constraints);
					e.printStackTrace();
				}
			}
			return c;
		}
	}


	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		// TODO Auto-generated method stub
		return null;
	}
}
