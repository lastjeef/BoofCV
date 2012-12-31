/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.factory.feature.describe;

import boofcv.abst.feature.describe.ConfigSurfDescribe;
import boofcv.abst.filter.blur.BlurFilter;
import boofcv.alg.feature.describe.*;
import boofcv.alg.feature.describe.brief.BriefDefinition_I32;
import boofcv.alg.feature.describe.impl.*;
import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.factory.interpolate.FactoryInterpolation;
import boofcv.struct.BoofDefaults;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;


/**
 * Creates algorithms for describing point features.
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"})
public class FactoryDescribePointAlgs {

	public static <T extends ImageSingleBand>
	DescribePointSurf<T> surfSpeed(ConfigSurfDescribe.Speed config, Class<T> imageType) {
		if( config == null )
			config = new ConfigSurfDescribe.Speed();
		config.checkValidity();


		return new DescribePointSurf<T>(config.widthLargeGrid,config.widthSubRegion,config.widthSample,
				config.weightSigma,config.useHaar,imageType);
	}

	public static <T extends ImageSingleBand>
	DescribePointSurf<T> surfStability(ConfigSurfDescribe.Stablility config, Class<T> imageType) {
		if( config == null )
			config = new ConfigSurfDescribe.Stablility();
		config.checkValidity();

		return new DescribePointSurfMod<T>(config.widthLargeGrid,config.widthSubRegion,config.widthSample,
				config.overLap,config.sigmaLargeGrid,config.sigmaSubRegion,config.useHaar,imageType);
	}

	public static <T extends ImageSingleBand>
	DescribePointBrief<T> brief(BriefDefinition_I32 definition, BlurFilter<T> filterBlur ) {
		Class<T> imageType = filterBlur.getInputType();

		if( imageType == ImageFloat32.class ) {
			return (DescribePointBrief<T> )new ImplDescribePointBrief_F32(definition,(BlurFilter<ImageFloat32>)filterBlur);
		} else if( imageType == ImageUInt8.class ) {
			return (DescribePointBrief<T> )new ImplDescribePointBrief_U8(definition,(BlurFilter<ImageUInt8>)filterBlur);
		} else {
			throw new IllegalArgumentException("Unknown image type: "+imageType.getSimpleName());
		}
	}

	// todo remove filterBlur for all BRIEF change to radius,sigma,type
	public static <T extends ImageSingleBand>
	DescribePointBriefSO<T> briefso(BriefDefinition_I32 definition, BlurFilter<T> filterBlur) {
		Class<T> imageType = filterBlur.getInputType();

		InterpolatePixel<T> interp = FactoryInterpolation.bilinearPixel(imageType);

		return new DescribePointBriefSO<T>(definition,filterBlur,interp);
	}

	public static DescribePointSift sift( int gridWidth , int numSamples , int numHistBins ) {
		return new DescribePointSift(gridWidth,numSamples,numHistBins
				,0.5, BoofDefaults.SCALE_SPACE_CANONICAL_RADIUS);
	}

	public static <T extends ImageSingleBand, D extends TupleDesc>
	DescribePointPixelRegion<T,D> pixelRegion( int regionWidth , int regionHeight , Class<T> imageType )
	{
		if( imageType == ImageFloat32.class ) {
			return (DescribePointPixelRegion<T,D>)new ImplDescribePointPixelRegion_F32(regionWidth,regionHeight);
		} else if( imageType == ImageUInt8.class ) {
			return (DescribePointPixelRegion<T,D>)new ImplDescribePointPixelRegion_U8(regionWidth,regionHeight);
		} else {
			throw new IllegalArgumentException("Unsupported image type");
		}
	}

	public static <T extends ImageSingleBand>
	DescribePointPixelRegionNCC<T> pixelRegionNCC( int regionWidth , int regionHeight , Class<T> imageType )
	{
		if( imageType == ImageFloat32.class ) {
			return (DescribePointPixelRegionNCC<T>)new ImplDescribePointPixelRegionNCC_F32(regionWidth,regionHeight);
		} else if( imageType == ImageUInt8.class ) {
			return (DescribePointPixelRegionNCC<T>)new ImplDescribePointPixelRegionNCC_U8(regionWidth,regionHeight);
		} else {
			throw new IllegalArgumentException("Unsupported image type");
		}
	}
}
