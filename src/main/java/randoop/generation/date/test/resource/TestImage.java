package randoop.generation.date.test.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.formats.jpeg.JpegImageParser;

public class TestImage {
	
	public static void TestImages(String s) {
//		if (args.length != 1) {
//			System.err.println("Usage: Driver <inputfile>");
//			return;
//		}
		try {
//			final File file = new File (args[0]);
			JpegImageParser p = new JpegImageParser();
			BufferedImage image = p.getBufferedImage(s.getBytes(), new HashMap<>());
			if (image != null) {
				
			}
		} catch (IOException | ImageReadException e) {
			System.err.println("Error reading image");
			e.printStackTrace();
		}

		System.out.println("Done.");
	}
	
}
