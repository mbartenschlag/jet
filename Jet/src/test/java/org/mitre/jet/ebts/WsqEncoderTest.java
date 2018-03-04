/*
 * Copyright 2014 The MITRE Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mitre.jet.ebts;

import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import org.jnbis.Bitmap;
import org.jnbis.WSQEncoder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.File;

/**
* Created by cfortner on 9/8/14.
*/
public class WsqEncoderTest {

    private static final Logger log = LoggerFactory.getLogger(WsqEncoderTest.class);

    private void convertToWsq(String sourceImageFile) throws Exception {
        checkConvertedImage(ImageIO.read(ClassLoader.getSystemResource(sourceImageFile).openStream()));
    }

    private void checkConvertedImage(BufferedImage sourceImage) throws Exception {
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        ByteArrayOutputStream wsqImageDataStream = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "WSQ", wsqImageDataStream);

        BufferedImage wsqImage = ImageIO.read(new ByteArrayInputStream(wsqImageDataStream.toByteArray()));
        int targetWidth = wsqImage.getWidth();
        int targetHeight = wsqImage.getHeight();

        Assert.assertEquals(sourceWidth, targetWidth);
        Assert.assertEquals(sourceHeight, targetHeight);
    }

    @Test
    public void testBmp500() throws Exception {
        convertToWsq("sample-gray-500.bmp");
    }

    @Test
    public void testBmpNonBiometric() throws Exception {
        ImageIO.setUseCache(false);

        File imageFile = new File(ClassLoader.getSystemResource("image_not_provided.bmp").toURI());

        BufferedImage bi = ImageIO.read(new ByteArrayInputStream(Files.toByteArray(imageFile)));

        BufferedImage result = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics g = result.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Bitmap bitmap = new Bitmap(
                (byte[])result.getRaster().getDataElements(0, 0, bi.getWidth(), bi.getHeight(), null),
                bi.getWidth(),
                bi.getHeight(),
                500,
                8, 1);
        WSQEncoder.encode(baos, bitmap, 0.75);

        byte[] outputImage = baos.toByteArray();
        Assert.assertNotNull(outputImage);
        Assert.assertTrue(outputImage.length > 0);
    }

    // TODO: Purpose of this test is unclear. Remove it?
//    @Test
//    public void testRawRectangle() throws Exception {
//
//        File imageFile = new File(ClassLoader.getSystemResource("sample.raw").toURI());
//
//        Bitmap bitmap = new Bitmap(Files.toByteArray(imageFile),640,480,500,8,-1);
//
//        log.debug("File shared/rectangle.raw read");
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        WSQEncoder.encode(baos,bitmap,0.75);
//        baos.flush();
//        byte[] outputImage = baos.toByteArray();
//
//        log.debug("Image data encoded, compressed byte length = {}", outputImage.length);
//
//        File outputFile = new File("JET/src/test/resources/test-output/testRawRectangle-Output.wsq");
//        Files.write(outputImage, outputFile);
//        log.debug("Image data written to file shared/rectangle.wsq");
//        Assert.assertNotNull(Files.toByteArray(outputFile));
//    }

    @Test
    public void testRawReference() throws Exception {

        File imageFile = new File(ClassLoader.getSystemResource("a001.raw").toURI());

        Bitmap bitmap = new Bitmap(Files.toByteArray(imageFile),1508,1008,500,8,-1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WSQEncoder.encode(baos,bitmap,0.75);
        baos.flush();
        byte[] outputImage = baos.toByteArray();
        Assert.assertNotNull(outputImage);
        Assert.assertTrue(outputImage.length > 0);

    }

    @Test
    public void testPng() throws Exception {
        convertToWsq("sample.png");
    }
}
