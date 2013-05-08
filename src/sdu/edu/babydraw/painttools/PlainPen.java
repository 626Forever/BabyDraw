package sdu.edu.babydraw.painttools;

import sdu.edu.babydraw.interfaces.ToolInterface;
import android.graphics.Paint;

//∆’Õ®ª≠± 
public class PlainPen extends PenAbstract implements ToolInterface {
	public PlainPen(int size, int penColor) {
		this(size,penColor,Paint.Style.STROKE);
	}

	public PlainPen(int size, int penColor,Paint.Style style ) {
		super(size, penColor,style);
	}

	@Override
	public String toString() {
		return "\tplainPen: " + "\tshap: " + currentShape + "\thasDraw: "
				+ hasDraw() + "\tsize: " + penSize + "\tstyle:" +style;
	}
}
