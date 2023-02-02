package ltsa.ui.update.utilities;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.util.Hashtable;

/**
 * Created by Victor Wjugow on 16/06/15.
 */
public class NonEditableGraphView extends mxGraph {

	public static final String INITIAL_STYLE = "is";
	public static final String REACHABLE_STYLE = "as";
	public static final String NEXT_STYLE = "ns";
	public static final String UNREACHABLE_STYLE = "us";

	@Override
	public boolean isCellEditable(Object cell) {
		return false;
	}

	@Override
	public boolean isCellMovable(Object cell) {
		return cell instanceof mxCell && ((mxCell) cell).isVertex();
	}

	@Override
	public boolean isCellDisconnectable(Object obj1, Object obj2, boolean flag) {
		return false;
	}

	@Override
	public boolean isLabelMovable(Object obj) {
		return false;
	}

	@Override
	public boolean isCellConnectable(Object obj) {
		return false;
	}

	public void setDefaultStyle() {
		mxStylesheet stylesheet = this.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		style.put(mxConstants.STYLE_OPACITY, 100);
		style.put(mxConstants.STYLE_FONTCOLOR, "#774444");
		stylesheet.putCellStyle("ROUNDED", style);

		Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
		edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
		edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
		edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
		edgeStyle.put(mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation);
		stylesheet.putCellStyle("MY_EDGE", edgeStyle);

		Hashtable<String, Object> initialStateStyle = new Hashtable<String, Object>();
		initialStateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		initialStateStyle.put(mxConstants.STYLE_OPACITY, 75);
		initialStateStyle.put(mxConstants.STYLE_FONTCOLOR, "#EAEAEA");
		initialStateStyle.put(mxConstants.STYLE_FILLCOLOR, "#0083FF");
		stylesheet.putCellStyle(INITIAL_STYLE, initialStateStyle);

		Hashtable<String, Object> nextStateStyle = new Hashtable<String, Object>();
		nextStateStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		nextStateStyle.put(mxConstants.STYLE_OPACITY, 100);
		nextStateStyle.put(mxConstants.STYLE_FONTCOLOR, "#004040");
		nextStateStyle.put(mxConstants.STYLE_FILLCOLOR, "#00E1E1");
		stylesheet.putCellStyle(NEXT_STYLE, nextStateStyle);

		Hashtable<String, Object> possibleStyle = new Hashtable<String, Object>();
		possibleStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		possibleStyle.put(mxConstants.STYLE_OPACITY, 100);
		possibleStyle.put(mxConstants.STYLE_FONTCOLOR, "#004040");
		possibleStyle.put(mxConstants.STYLE_FILLCOLOR, "#A0FF72");
		stylesheet.putCellStyle(REACHABLE_STYLE, possibleStyle);

		Hashtable<String, Object> forbiddenStyle = new Hashtable<String, Object>();
		forbiddenStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		forbiddenStyle.put(mxConstants.STYLE_OPACITY, 100);
		forbiddenStyle.put(mxConstants.STYLE_FONTCOLOR, "#BFBFBF");
		forbiddenStyle.put(mxConstants.STYLE_FILLCOLOR, "#585858");
		stylesheet.putCellStyle(UNREACHABLE_STYLE, forbiddenStyle);
	}
}