package com.capacitorjs.plugins.camera;

import com.getcapacitor.JSObject;
import androidx.exifinterface.media.ExifInterface;

public class ExifWrapper
{
    private final String[] attributes;
    private final ExifInterface exif;
    
    public ExifWrapper(final ExifInterface exif) {
        this.attributes = new String[] { "ApertureValue", "Artist", "BitsPerSample", "BodySerialNumber", "BrightnessValue", "CameraOwnerName", "CFAPattern", "ColorSpace", "ComponentsConfiguration", "CompressedBitsPerPixel", "Compression", "Contrast", "Copyright", "CustomRendered", "DateTime", "DateTimeDigitized", "DateTimeOriginal", "DefaultCropSize", "DeviceSettingDescription", "DigitalZoomRatio", "DNGVersion", "ExifVersion", "ExposureBiasValue", "ExposureIndex", "ExposureMode", "ExposureProgram", "ExposureTime", "FileSource", "Flash", "FlashpixVersion", "FlashEnergy", "FocalLength", "FocalLengthIn35mmFilm", "FocalPlaneResolutionUnit", "FocalPlaneXResolution", "FocalPlaneYResolution", "FNumber", "GainControl", "Gamma", "GPSAltitude", "GPSAltitudeRef", "GPSAreaInformation", "GPSDateStamp", "GPSDestBearing", "GPSDestBearingRef", "GPSDestDistance", "GPSDestDistanceRef", "GPSDestLatitude", "GPSDestLatitudeRef", "GPSDestLongitude", "GPSDestLongitudeRef", "GPSDifferential", "GPSDOP", "GPSHPositioningError", "GPSImgDirection", "GPSImgDirectionRef", "GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSMapDatum", "GPSMeasureMode", "GPSProcessingMethod", "GPSSatellites", "GPSSpeed", "GPSSpeedRef", "GPSStatus", "GPSTimeStamp", "GPSTrack", "GPSTrackRef", "GPSVersionID", "ImageDescription", "ImageLength", "ImageUniqueID", "ImageWidth", "InteroperabilityIndex", "ISOSpeed", "ISOSpeedLatitudeyyy", "ISOSpeedLatitudezzz", "JPEGInterchangeFormat", "JPEGInterchangeFormatLength", "LensMake", "LensModel", "LensSerialNumber", "LensSpecification", "LightSource", "Make", "MakerNote", "MaxApertureValue", "MeteringMode", "Model", "NewSubfileType", "OECF", "OffsetTime", "OffsetTimeDigitized", "OffsetTimeOriginal", "AspectFrame", "PreviewImageLength", "PreviewImageStart", "ThumbnailImage", "Orientation", "PhotographicSensitivity", "PhotometricInterpretation", "PixelXDimension", "PixelYDimension", "PlanarConfiguration", "PrimaryChromaticities", "RecommendedExposureIndex", "ReferenceBlackWhite", "RelatedSoundFile", "ResolutionUnit", "RowsPerStrip", "ISO", "JpgFromRaw", "SensorBottomBorder", "SensorLeftBorder", "SensorRightBorder", "SensorTopBorder", "SamplesPerPixel", "Saturation", "SceneCaptureType", "SceneType", "SensingMethod", "SensitivityType", "Sharpness", "ShutterSpeedValue", "Software", "SpatialFrequencyResponse", "SpectralSensitivity", "StandardOutputSensitivity", "StripByteCounts", "StripOffsets", "SubfileType", "SubjectArea", "SubjectDistance", "SubjectDistanceRange", "SubjectLocation", "SubSecTime", "SubSecTimeDigitized", "SubSecTimeOriginal", "ThumbnailImageLength", "ThumbnailImageWidth", "TransferFunction", "UserComment", "WhiteBalance", "WhitePoint", "Xmp", "XResolution", "YCbCrCoefficients", "YCbCrPositioning", "YCbCrSubSampling", "YResolution" };
        this.exif = exif;
    }
    
    public void copyExif(String attribute) {
        try {
            final ExifInterface exifInterface = new ExifInterface(attribute);
            int n = 0;
            while (true) {
                final String[] attributes = this.attributes;
                if (n >= attributes.length) {
                    break;
                }
                attribute = this.exif.getAttribute(attributes[n]);
                if (attribute != null) {
                    exifInterface.setAttribute(this.attributes[n], attribute);
                }
                ++n;
            }
            exifInterface.saveAttributes();
        }
        catch (final Exception ex) {}
    }
    
    public void p(final JSObject jsObject, final String s) {
        jsObject.put(s, this.exif.getAttribute(s));
    }
    
    public void resetOrientation() {
        this.exif.resetOrientation();
    }
    
    public JSObject toJson() {
        final JSObject jsObject = new JSObject();
        if (this.exif == null) {
            return jsObject;
        }
        int n = 0;
        while (true) {
            final String[] attributes = this.attributes;
            if (n >= attributes.length) {
                break;
            }
            this.p(jsObject, attributes[n]);
            ++n;
        }
        return jsObject;
    }
}
