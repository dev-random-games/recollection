package mvc;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

public class TextureUtil {
	static Texture textureFromText(String text){
		BufferedImage image = new BufferedImage(text.length() * 10, 15, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		g.drawString(text, 0, 10);
		try {
			return BufferedImageUtil.getTexture(null, image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
