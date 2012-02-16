package mvc;

import java.nio.FloatBuffer;

public class Light {
	FloatBuffer matSpecular;
	FloatBuffer lightPosition;
	FloatBuffer whiteLight; 
	FloatBuffer lModelAmbient;
	
	public Light() {
		
	}
	
	public Light(FloatBuffer matSpecular, FloatBuffer lightPosition, FloatBuffer whiteLight, FloatBuffer lModelAmbient) {
		this.matSpecular = matSpecular;
		this.lightPosition = lightPosition;
		this.whiteLight = whiteLight;
		this.lModelAmbient = lModelAmbient;
	}
}