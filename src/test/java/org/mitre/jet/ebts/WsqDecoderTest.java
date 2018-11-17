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

import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * User: cforter
 */
public class WsqDecoderTest {

    private static final Logger log = LoggerFactory.getLogger(WsqDecoderTest.class);

    @Test
    public void decodeWsqImage() throws Exception {
        File wsqFile = new File(ClassLoader.getSystemResource("a001.wsq").toURI());

        // Use ImageIO to read the input WSQ image.
        BufferedImage sourceImage = ImageIO.read(wsqFile);
        Assert.assertNotNull("Failed to read WSQ input image data.", sourceImage);

        // Store the height and width of the read image.
        int sourceWidth = sourceImage.getWidth();
        int sourceHeight = sourceImage.getHeight();

        // Write the buffered image out as a BMP.
        ByteArrayOutputStream bmpImageDataStream = new ByteArrayOutputStream();
        ImageIO.write(sourceImage, "bmp", bmpImageDataStream);
        bmpImageDataStream.flush();

        // Verify that some amount of image data was written.
        byte[] bmpImageData = bmpImageDataStream.toByteArray();
        Assert.assertTrue("Converting WSQ to BMP produced an empty BMP image.", bmpImageData.length > 0);

        BufferedImage targetImage = ImageIO.read(new ByteArrayInputStream(bmpImageData));
        Assert.assertNotNull("Failed to read BMP image that was converted from original WSQ.", targetImage);

        // Compare dimensions
        Assert.assertEquals(sourceWidth, targetImage.getWidth());
        Assert.assertEquals(sourceHeight, targetImage.getHeight());
    }
}
