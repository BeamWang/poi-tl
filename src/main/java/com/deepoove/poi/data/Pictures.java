/*
 * Copyright 2014-2020 Sayi
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
package com.deepoove.poi.data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

import com.deepoove.poi.data.style.PictureStyle;
import com.deepoove.poi.data.style.PictureStyle.PictureAlign;
import com.deepoove.poi.util.BufferedImageUtils;
import com.deepoove.poi.util.ByteUtils;
import com.deepoove.poi.util.UnitUtils;
import com.deepoove.poi.xwpf.WidthScalePattern;

/**
 * Factory method to build {@link PictureRenderData} instances.
 * 
 * @author Sayi
 */
public class Pictures {
    private Pictures() {
    }

    public static PictureBuilder ofLocal(String path) {
        return of(() -> ByteUtils.getLocalByteArray(new File(path)), PictureType.suggestFileType(path));
    }

    public static PictureBuilder ofUrl(String url, PictureType pictureType) {
        return of(() -> ByteUtils.getUrlByteArray(url), pictureType);
    }

    public static PictureBuilder ofUrl(String url) {
        return of(() -> ByteUtils.getUrlByteArray(url));
    }

    public static PictureBuilder ofStream(InputStream inputStream, PictureType pictureType) {
        return ofBytes(ByteUtils.toByteArray(inputStream), pictureType);
    }

    public static PictureBuilder ofStream(InputStream inputStream) {
        return ofBytes(ByteUtils.toByteArray(inputStream));
    }

    public static PictureBuilder ofBufferedImage(BufferedImage image, PictureType pictureType) {
        return of(() -> BufferedImageUtils.getBufferByteArray(image, pictureType.format()), pictureType);
    }

    public static PictureBuilder ofBase64(String base64, PictureType pictureType) {
        return of(() -> ByteUtils.getBase64ByteArray(base64), pictureType);
    }

    public static PictureBuilder ofBytes(byte[] bytes) {
        return ofBytes(bytes, null);
    }

    public static PictureBuilder ofBytes(byte[] bytes, PictureType pictureType) {
        return new PictureBuilder(pictureType, () -> bytes);
    }

    public static PictureBuilder of(Supplier<byte[]> supplier) {
        return of(supplier, null);
    }

    public static PictureBuilder of(Supplier<byte[]> supplier, PictureType pictureType) {
        return new PictureBuilder(pictureType, supplier);
    }

    /**
     * Builder to build {@link PictureRenderData} instances.
     */
    public static class PictureBuilder implements RenderDataBuilder<PictureRenderData> {

        private PictureRenderData data;

        private PictureBuilder(PictureType pictureType, Supplier<byte[]> supplier) {
            data = new PictureRenderData(0, 0, pictureType, supplier);
        }

        public PictureBuilder size(int width, int height) {
            PictureStyle style = getPictureStyle();
            style.setWidth(width);
            style.setHeight(height);
            style.setScalePattern(WidthScalePattern.NONE);
            return this;
        }

        private PictureStyle getPictureStyle() {
            PictureStyle style = data.getPictureStyle();
            if (null == style) {
                style = new PictureStyle();
                data.setPictureStyle(style);
            }
            return style;
        }

        public PictureBuilder sizeInCm(double widthCm, double heightCm) {
            return size(UnitUtils.cm2Pixel(widthCm), UnitUtils.cm2Pixel(heightCm));
        }

        public PictureBuilder fitSize() {
            PictureStyle style = getPictureStyle();
            style.setScalePattern(WidthScalePattern.FIT);
            return this;
        }

        public PictureBuilder altMeta(String altMeta) {
            data.setAltMeta(altMeta);
            return this;
        }

        public PictureBuilder left() {
            PictureStyle style = getPictureStyle();
            style.setAlign(PictureAlign.LEFT);
            return this;
        }

        public PictureBuilder center() {
            PictureStyle style = getPictureStyle();
            style.setAlign(PictureAlign.CENTER);
            return this;
        }

        public PictureBuilder right() {
            PictureStyle style = getPictureStyle();
            style.setAlign(PictureAlign.RIGHT);
            return this;
        }

        @Override
        public PictureRenderData create() {
            return data;
        }
    }

}