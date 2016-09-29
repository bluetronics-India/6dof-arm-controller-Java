import processing.core.*;
import controlP5.*;
import java.io.*;
import java.util.*;
import processing.serial.*;

public class GUIpage extends PApplet  {
	ControlP5 cp5;
	
	PApplet parent;
	Serial port;
	BufferedReader reader;
	int configNumber = 1, currentRow, distance = 40,reading; // distance between sliders
	
	
	
	float fieldSize = 350, // Size of display field
			coordLine = 100, // Size of separate coordinate line
			AB = 200, // Len of first joint
			BC = 200, // Len of second joint
			CD = 100, // Len of third joint
			DS = 50, // Len of fourth joint
			fullLen = AB + BC + CD, jointDiam = 20, // Diameter of each joint
			// Sliders variables
			sliderR, sliderA1, sliderA2, sliderA3, sliderA4, sliderA5, sliderX, sliderY, sliderZ,sliderA6,
			// Slider change variables
			lastSliderR, lastSliderA1, lastSliderA2, lastSliderA3, lastSliderA4, lastSliderA5, Xpos = 500, Ypos = 600,
			Zpos = 0;
			
	String[] sliders = { "sliderR", "sliderA1", "sliderA2", "sliderA3", "sliderA4", "sliderA5" ,"sliderA6"},

			slidersXYZ = { "sliderX", "sliderY", "sliderZ" },

			valuesXYZ = { "X", "Y", "Z" },

			values = { "R", "A1", "A2", "A3", "A4", "A5","A6" },

			toggles = { "joint2", "joint3", "inverse", "writeBackground", "readAll" },

			buttons = { "R", "A1", "A2", "A3", "A4", "A5","A6"};



	boolean joint2, joint3, inverse, setupFinished, changedSetup, changedGUI, writeBackground, readAll,readLast , lastWrit,finished;

	String line;

	List<int[]> rowList = new ArrayList<int[]>();
	List<String[]> configList = new ArrayList<String[]>();
	
	public static void main(String[] args) {
		PApplet.main("GUIpage");
	}
		
	public void settings() {
		size(1000, 700, P3D);
		//fullScreen(P3D);
	}

	public void setup() {
		cp5 = new ControlP5(this);
		
		reader = createReader("config.txt");
		try {
		    line = reader.readLine();
		  } catch (IOException e) {
		    e.printStackTrace();
		    line = null;
		  }
		  if (line == null) {
		    noLoop();  
		  } else {
		    String[] pieces = split(line," = ");
		    configList.add(new String[]{ pieces[1]});
		  }
		
		if(configList.get(0)[0] == "true"){
		port = new Serial(this,Serial.list()[0], 9600);
		}
		
		for (int i = 0; i < sliders.length; i++) {
			cp5.addSlider(sliders[i])
				.setPosition(20 + i * distance, 20)
				.setSize(20, 180)
				.setRange(0, 180)
				.setLabelVisible(false);

			cp5.addTextfield(values[i])
				.setPosition(20 + i * distance, 220)
				.setSize(30, 15).setFocus(true)
				.setColor(color(255, 0, 0))
				.setFont(createFont("arial", 15));

			cp5.addTextlabel("label" + values[i])
				.setPosition(20 + i * distance, 270)
				.setColorValue(color(0, 0, 255))
				.setFont(createFont("arial", 15));

		}

		for (int i = 0; i < toggles.length; i++) {
			cp5.addToggle(toggles[i])
				.setPosition(20 + (i + 3) * distance, 300)
				.setSize(20, 20)
				.setLabelVisible(false)
				.setValue(false);
		}

		for (int i = 0; i < slidersXYZ.length; i++) {

			cp5.addSlider(slidersXYZ[i]).setPosition(20 + i * distance, 330)
				.setSize(20, 180)
				.setRange(0, fullLen)
				.setLabelVisible(false);
			cp5.addTextfield(valuesXYZ[i])
				.setPosition(20 + i * distance, 530)
				.setSize(30, 15).setFocus(true)
				.setColor(color(255, 0, 0))
				.setFont(createFont("arial", 15));
			cp5.addTextlabel("label" + valuesXYZ[i])
				.setPosition(15 + i * distance, 580)
				.setColorValue(color(0, 0, 255))
				.setFont(createFont("arial", 15));

		}
		
		for(int i = 0;i<buttons.length ;i++){
			cp5.addButton("button"+buttons[i])
		     .setPosition(20 + i * distance,300)
		     .setSize(20,20)
		     .setLabelVisible(false)	
		     .setColorBackground(color(199,0,255));
		     ;
			
		}		
		
		((Slider) cp5.getController("sliderR")).setRange(0, 360);
		((Slider) cp5.getController("sliderA6")).setRange(0,90);
		((Slider) cp5.getController("sliderX")).setRange(-fullLen, fullLen);
		((Slider) cp5.getController("sliderZ")).setRange(-fullLen, fullLen);
		((Slider) cp5.getController("sliderZ")).setValue(0);
		((Slider) cp5.getController("sliderX")).setValue(0);
		((Slider) cp5.getController("sliderA4")).setValue(90);
		((Slider) cp5.getController("sliderA5")).setValue(90);
		
		
		((Toggle) cp5.getController("inverse")).setSize(60, 20);
		((Toggle) cp5.getController("inverse")).setColorBackground(color(0,0,255));
		((Toggle) cp5.getController("inverse")).setPosition(20 + 3 * distance, 330);
		((Toggle) cp5.getController("inverse")).setColorActive(color(0,255,0));
		
		((Toggle) cp5.getController("joint2")).setColorActive(color(0,255,0));
		((Toggle) cp5.getController("joint3")).setColorActive(color(0,255,0));

		((Toggle) cp5.getController("joint2")).setPosition(20 + 3 * distance, 370);
		((Toggle) cp5.getController("joint3")).setPosition(20 + 4 * distance, 370);
		
		((Toggle) cp5.getController("writeBackground")).setPosition(20 + 3 * distance, 400);
		((Toggle) cp5.getController("writeBackground")).setColorBackground(color(0,255,255));
		((Toggle) cp5.getController("writeBackground")).setColorActive(color(255,0,0));
		((Toggle) cp5.getController("writeBackground")).setSize(60, 20);
		
		((Toggle) cp5.getController("readAll")).setPosition(20 + 3 * distance, 430);
		((Toggle) cp5.getController("readAll")).setColorBackground(color(0,255,255));
		((Toggle) cp5.getController("readAll")).setColorActive(color(255,0,0));
		((Toggle) cp5.getController("readAll")).setSize(60, 20);
		

	}

	public void draw() {
		
		background(0);
		stroke(255);
		noFill();		
		fieldDraw();
		
		if (writeBackground&&!readAll) {
			write();
		}

		if (readAll&&!writeBackground) {
			read();
		}
		
		armDraw();
		if(configList.get(0)[0] == "true"){
			port.write(str(sliderA1)+","+str(sliderA2)+","+str(sliderA3)
			+","+str(sliderR)+","+str(sliderA4)+","+str(sliderA5)+","+str(sliderA6)+"\n");
		}

		popMatrix();
	}

	public void controlEvent(ControlEvent theEvent) {
		if (theEvent.isAssignableFrom(Textfield.class)) {
			cp5.getController("slider" + theEvent.getName())
					.setValue(Integer.parseInt(cp5.get(Textfield.class, theEvent.getName()).getText()));
		}
		if(theEvent.isAssignableFrom(Button.class)){
			String val = theEvent.getController().getName();
			switch(val){
			case "buttonR": 
				((Slider) cp5.getController("sliderR")).setValue(0);
				break;
			case "buttonA1":
				((Slider) cp5.getController("sliderA1")).setValue(90);
				break;
			case "buttonA2":
				((Slider) cp5.getController("sliderA2")).setValue(90);
				break;
			case "buttonA3":
				((Slider) cp5.getController("sliderA3")).setValue(90);
				break;
			case "buttonA4":
				((Slider) cp5.getController("sliderA4")).setValue(90);
				break;
			case "buttonA5":
				((Slider) cp5.getController("sliderA5")).setValue(90);
				break;	
			case "buttonA6":
				((Slider) cp5.getController("sliderA6")).setValue(0);
				break;
			}	
		}
		if(theEvent.isAssignableFrom(Toggle.class)){
			String val = theEvent.getController().getName();
			if(val == "readAll"&&!readAll){
			finished = false;
			}
		}
		
	}

	public void angledBox(float x, float y, float z, float jointLen, float jointDia, float baseR, float angleR,
			float angleE) {

		pushMatrix();

		translate(x, -y, -z);
		rotateY(radians(baseR));
		rotateZ(radians(-angleR));
		rotateX(radians(angleE));

		stroke(255, 0, 0);
		line(0, 0, 0, 0, -coordLine, 0);
		stroke(0, 255, 0);
		line(0, 0, 0, coordLine, 0, 0);
		stroke(0, 0, 255);
		line(0, 0, 0, 0, 0, -coordLine);

		stroke(255);
		translate(jointLen / 2, 0, 0);

		box(jointLen, -jointDia, -jointDia); // Box is facing X-direction with
												// its jointLen and
												// jointDia is faceing Y and Z
												// directions
		popMatrix();
	}

	public void angledEnd(float x, float y, float z, float jointLen, float jointDia, float baseR, float angleR,
			float angleE) {

		pushMatrix();

		translate(x, -y, -z);
		rotateY(radians(baseR));
		rotateZ(radians(-angleR));
		rotateX(radians(angleE));

		stroke(255, 0, 0);
		line(0, 0, 0, 0, -coordLine, 0);
		stroke(0, 255, 0);
		line(0, 0, 0, coordLine, 0, 0);
		stroke(0, 0, 255);
		line(0, 0, 0, 0, 0, -coordLine);

		stroke(255);
		translate(0, jointLen / 2, 0);

		box(jointDia, -jointLen, -jointDia); // Box is facing X-direction with
												// its jointLen and
												// jointDia is faceing Y and Z
												// directions
		popMatrix();
	}

	public void compute(float xTarget, float yTarget, float zTarget) {

		float GAX;

		float AG = sqrt(xTarget * xTarget + zTarget * zTarget);
		float AD = sqrt(AG * AG + yTarget * yTarget);
		float DAG = degrees(asin(yTarget / AD));
		if (xTarget > 0) {
			if (zTarget < 0) {
				GAX = degrees(asin(-zTarget / AG));
			} else if (zTarget > 0) {
				GAX = 360 - (degrees(asin(zTarget / AG)));
			} else {
				GAX = 0;
			}
		} else if (xTarget == 0) {
			if (zTarget > 0) {
				GAX = 270;
			} else if (zTarget < 0) {
				GAX = 90;
			} else {
				GAX = 0;
			}
		} else {
			if (zTarget < 0) {
				GAX = 180 - (degrees(asin(-zTarget / AG)));
			} else if (zTarget > 0) {
				GAX = 180 + (degrees(asin(zTarget / AG)));
			} else {
				GAX = 180;
			}
		}
		((Slider) cp5.getController("sliderR")).setValue(GAX);

		for (int a1 = 90; a1 <= 180; a1++) {
			for (int a2 = 90; a2 <= 180; a2++) {

				float BD = sqrt(BC * BC + CD * CD - 2 * BC * CD * cos(radians(a2)));
				float CBD = degrees(acos((BC * BC + BD * BD - CD * CD) / (2 * BC * BD)));
				float ADt = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(a1 - CBD)));
				float BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));

				if ((AD + 1 > ADt) && (AD - 1 < ADt)) {

					((Slider) cp5.getController("sliderA1")).setValue((int) (BAD + DAG));
					((Slider) cp5.getController("sliderA2")).setValue((int) (a1 - 90));
					((Slider) cp5.getController("sliderA3")).setValue((int) (a2 - 90));

				}

			}
		}
	}

	public void fieldDraw() {
		((Textlabel) cp5.getController("labelR")).setText(str((int) sliderR));
		((Textlabel) cp5.getController("labelA1")).setText(str((int) sliderA1));
		((Textlabel) cp5.getController("labelA2")).setText(str((int) sliderA2));
		((Textlabel) cp5.getController("labelA3")).setText(str((int) sliderA3));
		((Textlabel) cp5.getController("labelA4")).setText(str((int) sliderA4));
		((Textlabel) cp5.getController("labelA5")).setText(str((int) sliderA5));
		((Textlabel) cp5.getController("labelX")).setText(str((int) sliderX));
		((Textlabel) cp5.getController("labelY")).setText(str((int) sliderY));
		((Textlabel) cp5.getController("labelZ")).setText(str((int) sliderZ));
		((Textlabel) cp5.getController("labelA6")).setText(str((int) sliderA6));
		
		pushMatrix();
		translate(Xpos, Ypos, Zpos);

		// Draw field

		line(0, 0, 0, 0, -fieldSize, 0);

		beginShape();
		vertex(0, 0, 0);
		vertex(0, 0, -fieldSize);
		vertex(fieldSize, 0, -fieldSize);
		vertex(fieldSize, 0, 0);
		endShape(CLOSE);
	}

	public void secondJoint(){
		// Second joint

				float yb = sin(radians(sliderA1)) * AB;
				float AT = sqrt(AB * AB - yb * yb);
				float zb = sin(radians(sliderR)) * AT;
				float xb = cos(radians(sliderR)) * AT;

				xb = (sliderA1 > 90) ? -1 * xb : xb;
				zb = (sliderA1 > 90) ? -1 * zb : zb;

				if (joint2) {
					((Slider) cp5.getController("sliderA2")).setValue(90 - sliderA1);
					angledBox(xb, yb, zb, BC, jointDiam, sliderR, -90 + sliderA1 + sliderA2, 0);
				} else {
					angledBox(xb, yb, zb, BC, jointDiam, sliderR, -90 + sliderA1 + sliderA2, 0);
				}
	}
	
	public void thirdJoint(){
		
		// Third joint

		float AC = sqrt(AB * AB + BC * BC - 2 * AB * BC * cos(radians(90 + sliderA2)));
		float BAC = degrees(acos((AB * AB + AC * AC - BC * BC) / (2 * AB * AC)));
		float CAF = ((90 + sliderA2) > 180) ? sliderA1 + BAC : sliderA1 - BAC;

		float yc = sin(radians(CAF)) * AC;
		float AF = sqrt(AC * AC - yc * yc);

		float zc = sin(radians(sliderR)) * AF;
		float xc = cos(radians(sliderR)) * AF;

		xc = (CAF > 90) ? -1 * xc : xc;
		zc = (CAF > 90) ? -1 * zc : zc;

		if (joint3) {
			((Slider) cp5.getController("sliderA3")).setValue(180 - sliderA1 - sliderA2);
			angledBox(xc, yc, zc, CD, jointDiam, sliderR, -180 + sliderA1 + sliderA2 + sliderA3, 0);
		} else {
			angledBox(xc, yc, zc, CD, jointDiam, sliderR, -180 + sliderA1 + sliderA2 + sliderA3, 0);
		}
	}
	
	public void fourthJoint(){
		
		// Fourth joint
				float BAD, AD, DAG;
				if ((sliderA2 + 90) <= 180) {

					if ((90 + sliderA3) <= 180) {

						float BD = sqrt(BC * BC + CD * CD - 2 * BC * CD * cos(radians(90 + sliderA3)));
						float CBD = degrees(acos((BC * BC + BD * BD - CD * CD) / (2 * BC * BD)));
						float ABD = (90 + sliderA2) - CBD;
						AD = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(ABD)));
						BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));
						DAG = sliderA1 - BAD;

					} else {

						float BD = sqrt(BC * BC + CD * CD - 2 * BC * CD * cos(radians(270 - sliderA3)));
						float DBC = degrees(acos((BD * BD + BC * BC - CD * CD) / (2 * BD * BC)));
						float ABD = 90 + sliderA2 + DBC;

						if (ABD >= 180) {
							AD = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(360 - ABD)));
							BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));
							DAG = sliderA1 + BAD;
						} else {
							AD = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(ABD)));
							BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));
							DAG = sliderA1 - BAD;
						}

					}
				} else {
					if ((90 + sliderA3) <= 180) {

						float BD = sqrt(BC * BC + CD * CD - 2 * BC * CD * cos(radians(90 + sliderA3)));
						float CBD = degrees(acos((BC * BC + BD * BD - CD * CD) / (2 * BC * BD)));
						float ABD = (90 + sliderA2) - CBD;
						
						if(ABD>=180){
							AD = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(ABD)));
							BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));
							DAG = sliderA1 + BAD;
						}else{
							AD = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(ABD)));
							BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));
							DAG = sliderA1 - BAD;
						}
						
						

					} else {

						float BD = sqrt(BC * BC + CD * CD - 2 * BC * CD * cos(radians(270 - sliderA3)));
						float DBC = degrees(acos((BD * BD + BC * BC - CD * CD) / (2 * BD * BC)));
						float ABD = 90 + sliderA2 + DBC;
						AD = sqrt(AB * AB + BD * BD - 2 * AB * BD * cos(radians(ABD)));
						BAD = degrees(acos((AB * AB + AD * AD - BD * BD) / (2 * AB * AD)));
						DAG = sliderA1 + BAD;

					}

				}
				
				float yd = sin(radians(DAG)) * AD;
				float AG = sqrt(AD * AD - yd * yd);

				float zd = sin(radians(sliderR)) * AG;
				float xd = cos(radians(sliderR)) * AG;

				xd = (DAG > 90) ? -1 * xd : xd;
				zd = (DAG > 90) ? -1 * zd : zd;

				angledEnd(xd, yd, zd, DS, jointDiam, sliderR, -180 + sliderA1 + sliderA2 + sliderA3 + sliderA4, -90 + sliderA5);
				
				if (!inverse) {
					((Slider) cp5.getController("sliderX")).setValue((int) (xd));
					((Slider) cp5.getController("sliderY")).setValue((int) (yd));
					((Slider) cp5.getController("sliderZ")).setValue((int) (zd));
				}
	}
	
	public void armDraw() {
		// First joint

		if (inverse) {
			compute(sliderX, sliderY, sliderZ);
		}

		angledBox(0, 0, 0, AB, jointDiam, sliderR, sliderA1, 0);

		secondJoint();

		thirdJoint();
		
		fourthJoint();
		
		
		
	}

	public void keyPressed() {
		  if (key == ESC) {

		  }else if(key == ' '){
  
		  }
		}
	
	
	public void write(){
		   boolean done = true;
			if(sliderR!=lastSliderR){
				done = interact(done);
			}
			if(sliderA1!=lastSliderA1){
				done = interact(done);
			}
			if(sliderA2!=lastSliderA2){
				done = interact(done);
			}
			if(sliderA3!=lastSliderA3){
				done = interact(done);
			}
			if(sliderA4!=lastSliderA4){
				done = interact(done);
			}
			if(sliderA5!=lastSliderA5){
				done = interact(done);
			}
			
			
		}

	
	public boolean interact(boolean active){
		if(active){
			
				int[] list= { (int)((Slider) cp5.getController("sliderR")).getValue(),
						(int)((Slider) cp5.getController("sliderA1")).getValue(),
						(int)((Slider) cp5.getController("sliderA2")).getValue(),
						(int)((Slider) cp5.getController("sliderA3")).getValue(),
						(int)((Slider) cp5.getController("sliderA4")).getValue(),
						(int)((Slider) cp5.getController("sliderA5")).getValue()};
		
				rowList.add(new int[]{ (int)((Slider) cp5.getController("sliderR")).getValue(),
						(int)((Slider) cp5.getController("sliderA1")).getValue(),
						(int)((Slider) cp5.getController("sliderA2")).getValue(),
						(int)((Slider) cp5.getController("sliderA3")).getValue(),
						(int)((Slider) cp5.getController("sliderA4")).getValue(),
						(int)((Slider) cp5.getController("sliderA5")).getValue()});

				currentRow++;

				lastSliderR = list[0];
				lastSliderA1 = list[1];
				lastSliderA2 = list[2];
				lastSliderA3 = list[3];
				lastSliderA4 = list[4];
				lastSliderA5 = list[5];
				
				
				
				println(list[0],
						list[1],
						list[2],
						list[3],
						list[4],
						list[5],
						currentRow);
				
		}
		return false;
	}

	
	public void read(){
		if (!finished&&reading < currentRow) {
			int list[] = rowList.get(reading);
			((Slider) cp5.getController("sliderR")).setValue(list[0]);
			((Slider) cp5.getController("sliderA1")).setValue(list[1]);
			((Slider) cp5.getController("sliderA2")).setValue(list[2]);
			((Slider) cp5.getController("sliderA3")).setValue(list[3]);
			((Slider) cp5.getController("sliderA4")).setValue(list[4]);
			((Slider) cp5.getController("sliderA5")).setValue(list[5]);
			reading++;
			delay(15);
			finished = (reading == currentRow) ? true : false;
		}else{
			reading=0;
		}
	}
	}

