import processing.core.*;
import controlP5.*;
public class GUIpage extends PApplet  {
	ControlP5 cp5;
	
	//Sliders variables
	float sliderR,sliderA1,sliderA2,sliderA3,sliderA4,sliderA5,sliderX,sliderY,sliderZ;
	//Toggles variables
	boolean joint2,joint3;
	//Sliders array
	String[] sliders ={"sliderR","sliderA1","sliderA2","sliderA3","sliderA4","sliderA5"};
	//Sliders for XYZ
	String[] slidersXYZ ={"sliderX","sliderY","sliderZ"};
	//Text field array
	String[] values  ={"R","A1","A2","A3","A4","A5"};
	//Toggle array
	String[] toggles ={"joint2","joint3","inverse"};
	
	
	int distance = 40; 							//distance between sliders
	float fieldSize = 350;  					//Size of display field
	float coordLine = 100; 						//Size of separate coordinate line
	float AB = 200;								//Len of first joint
	float BC = 200;								//Len of second joint
	float CD = 100;								//Len of third joint
	float DS = 50;								//Len of fourth joint
	float jointDiam = 20;						//Diameter of each joint
	
	public static void main(String[] args) {
		PApplet.main("GUIpage");
	}
	
	public void settings(){
		size(800,600, P3D);
	  }
	
	  public void setup() {
		  cp5 = new ControlP5(this);
		  
		  for(int i=0;i<sliders.length;i++){
			  cp5.addSlider(sliders[i])
		     .setPosition(20+i*distance,20)
		     .setSize(20,180)
		     .setRange(0,180)
		     .setLabelVisible(false)
		     ;
			  
		  cp5.addTextfield(values[i])
		     .setPosition(20+i*distance,220)
		     .setSize(30,15)
		     .setFocus(true)
		     .setColor(color(255,0,0))
		     .setFont(createFont("arial",15))
		     ;

		  cp5.addTextlabel("label"+values[i])
            .setPosition(20+i*distance,270)
            .setColorValue(color(0,0,255))
            .setFont(createFont("arial",15))
            ;
		 
		  }	
		  
		  for (int i = 0; i < toggles.length; i++) {
			cp5.addToggle(toggles[i])
					.setPosition(20 + (i+2) * distance, 300)
					.setSize(20, 20)
					.setLabelVisible(false)
					.setValue(false);
		}
		  
		  ((Slider)cp5.getController("sliderR")).setRange(0,360);
		  ((Slider)cp5.getController("sliderA4")).setValue(90);
		  ((Slider)cp5.getController("sliderA5")).setValue(90);
		  ((Toggle)cp5.getController("inverse")).setSize(20,50);
		  
	  }

	  public void draw() {
		  background(0);
		  stroke(255);
		  noFill();
		  ((Textlabel)cp5.getController("labelR")).setText(str((int)sliderR));
		  ((Textlabel)cp5.getController("labelA1")).setText(str((int)sliderA1));
		  ((Textlabel)cp5.getController("labelA2")).setText(str((int)sliderA2));
		  ((Textlabel)cp5.getController("labelA3")).setText(str((int)sliderA3));
		  ((Textlabel)cp5.getController("labelA4")).setText(str((int)sliderA4));
		  ((Textlabel)cp5.getController("labelA5")).setText(str((int)sliderA5));
		   
		  pushMatrix();
		  translate(300,500,0);

		  //Draw field
		  
		  line(0,0,0,0,-fieldSize,0);
		  
		  beginShape();
		  vertex(0,0,0);
		  vertex(0,0,-fieldSize);
		  vertex(fieldSize,0,-fieldSize);
		  vertex(fieldSize,0,0);
		  endShape(CLOSE);
		  
		  
		  //First joint
		  
		  angledBox(0,0,0,AB,jointDiam,sliderR,sliderA1,0); 
		  
		  //Second joint
		  
		  
		  float yb = sin(radians(sliderA1))*AB;
		  float AT = sqrt(AB*AB-yb*yb); 
		  float zb = sin(radians(sliderR))*AT; 
		  float xb = cos(radians(sliderR))*AT;	  
		  
		  xb = (sliderA1>90)? -1*xb:xb;
		  zb = (sliderA1>90)? -1*zb:zb;
		  
		  
		  if(joint2){
		  ((Slider)cp5.getController("sliderA2")).setValue(90-sliderA1);  	
		  angledBox(xb,yb,zb,BC,jointDiam,sliderR,-90+sliderA1+sliderA2,0); 
		  }else{
		  angledBox(xb,yb,zb,BC,jointDiam,sliderR,-90+sliderA1+sliderA2,0);
		  }
		  
		  //Third joint
		  
		  
		  float AC  = sqrt(AB*AB+BC*BC-2*AB*BC*cos(radians(90+sliderA2)));
		  float BAC = degrees(acos((AB*AB+AC*AC-BC*BC)/(2*AB*AC)));
		  float CAF = ((90+sliderA2)>180)?sliderA1+BAC:sliderA1-BAC;
		  
		  float yc = sin(radians(CAF))*AC;
		  float AF = sqrt(AC*AC-yc*yc);
		  
		  float zc = sin(radians(sliderR))*AF; 		  
		  float xc = cos(radians(sliderR))*AF;
		  
		  xc = (CAF>90)? -1*xc:xc;
		  zc = (CAF>90)? -1*zc:zc;
		  
		  if(joint3){
		  ((Slider)cp5.getController("sliderA3")).setValue(180-sliderA1-sliderA2); 
		  angledBox(xc,yc,zc,CD,jointDiam,sliderR,-180+sliderA1+sliderA2+sliderA3,0);
	  	  }else{
		  angledBox(xc,yc,zc,CD,jointDiam,sliderR,-180+sliderA1+sliderA2+sliderA3,0);
	  	  }
		  
		  //Fourth joint
		  float BAD,AD,DAG;
		  if((90+sliderA3)<=180){
			  
		  float BD = sqrt(BC*BC+CD*CD-2*BC*CD*cos(radians(90+sliderA3)));
		  float CBD = degrees(acos((BC*BC+BD*BD-CD*CD)/(2*BC*BD)));
		  float ABD = (90+sliderA2) - CBD;
		   AD = sqrt( AB*AB+BD*BD-2*AB*BD*cos(radians(ABD)));
		   BAD =  degrees(acos((AB*AB+AD*AD-BD*BD)/(2*AB*AD)));
		   DAG = sliderA1 - BAD;
		  }else{
			  
		  float BD = sqrt(BC*BC+CD*CD-2*BC*CD*cos(radians(270-sliderA3)));
		  float DBC = degrees(acos((BD*BD+BC*BC-CD*CD)/(2*BD*BC)));
		  float ABD = 90+sliderA2+DBC;
		   AD = sqrt(AB*AB+BD*BD-2*AB*BD*cos(radians(ABD)));
		   BAD = degrees(acos((AB*AB+AD*AD-BD*BD)/(2*AB*AD))); 
		   DAG = sliderA1 + BAD;	  	 
		  }
			  DAG = sliderA1 - BAD;
			  float yd = sin(radians(DAG))*AD;
			  float AG = sqrt(AD*AD-yd*yd);
			  
			  float zd = sin(radians(sliderR))*AG; 		  
			  float xd = cos(radians(sliderR))*AG;
			  
			  xd = (DAG>90)? -1*xd:xd;
			  zd = (DAG>90)? -1*zd:zd;
		  
		  angledEnd(xd,yd,zd,DS,jointDiam,sliderR,-180+sliderA1+sliderA2+sliderA3+sliderA4,-90+sliderA5);
		  
		  popMatrix();
	  }
	  
	  
	  
	 public void controlEvent(ControlEvent theEvent) {
		  if(theEvent.isAssignableFrom(Textfield.class)) {
		    cp5.getController("slider"+theEvent.getName())
		    .setValue(Integer.parseInt(cp5.get(Textfield.class,theEvent.getName()).getText()));
		  }		  
		}
		
	 public void angledBox(float x,float y, float z,float jointLen,float jointDia,float baseR,float angleR,float angleE){
		 
		 pushMatrix();
		 
		 translate(x,-y,-z);
		 rotateY(radians(baseR));
		 rotateZ(radians(-angleR));
		 rotateX(radians(angleE));
		 
		 stroke(255,0,0);
		 line(0,0,0,0,-coordLine,0);
		 stroke(0,255,0);
		 line(0,0,0,coordLine,0,0);
		 stroke(0,0,255);
		 line(0,0,0,0,0,-coordLine);
		 
		 stroke(255);
		 translate(jointLen/2,0,0);
		 
		 box(jointLen,-jointDia,-jointDia);  //Box is facing X-direction with its jointLen and 
		 									 //jointDia is faceing Y and Z directions
		 popMatrix();
	  }
	 	
	 public void angledEnd(float x,float y, float z,float jointLen,float jointDia,float baseR,float angleR,float angleE){
		 
		 pushMatrix();
		 
		 translate(x,-y,-z);
		 rotateY(radians(baseR));
		 rotateZ(radians(-angleR));
		 rotateX(radians(angleE));
		 
		 stroke(255,0,0);
		 line(0,0,0,0,-coordLine,0);
		 stroke(0,255,0);
		 line(0,0,0,coordLine,0,0);
		 stroke(0,0,255);
		 line(0,0,0,0,0,-coordLine);
		 
		 stroke(255);
		 translate(0,jointLen/2,0);
		 
		 box(jointDia,-jointLen,-jointDia);  //Box is facing X-direction with its jointLen and 
		 									 //jointDia is faceing Y and Z directions
		 popMatrix();
	  }

	 public void compute(float xTarget,float yTarget,float zTarget){
		 
		 	line(0,0,0,xTarget,yTargetzTarget);
		 	
		    float AG=sqrt(xTarget*xTarget+zTarget*zTarget);
		    float GAX=degrees(acos(xTarget/AG));
		    
		    for(int a2=90;a2<=180;a2++){
		      for(int a1=90;a1<=180;a1++){
		    	  
		         float BD=sqrt(BC*BC+CD*CD-2*BC*CD*cos(radians(a2)));
		         float CBD=degrees(acos((BC*BC+BD*BD-CD*CD)/(2*BC*BD)));
		         float AD=sqrt(AB*AB+BD*BD-2*AB*BD*cos(radians(a1-CBD)));
		         float BAD=degrees(acos((AB*AB+AD*AD-BD*BD)/(2*AB*AD)));
		         
		         if((AD+1>ad)&&(AD-1<ad)){ 
		          ((Slider)cp5.getController("sliderA1")).setRange(A6+BAD);
		   		  ((Slider)cp5.getController("sliderA2")).setValue(a1-90);
		   		  ((Slider)cp5.getController("sliderA3")).setValue(a2-90);
	            }
		         
		      }
		    } 
		  }
	  
}
