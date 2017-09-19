package com.translator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by hmoda on 25-Aug-17.
 */

public class ImageLoader {

    public static BufferedImage loadImage(String path){
        try {

            return ImageIO.read(ImageLoader.class.getResource(path));

        } catch (IOException e) {

            System.out.println("Something wrong with img loader");
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

}