/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.filter.convolve;

import gecv.PerformerBase;
import gecv.ProfileOperation;
import gecv.alg.filter.convolve.down.ConvolveDownNoBorderStandard;
import gecv.alg.filter.convolve.down.ConvolveDownNoBorderUnrolled_F32_F32;
import gecv.alg.filter.convolve.down.ConvolveDownNoBorderUnrolled_U8_I16;
import gecv.alg.filter.convolve.down.ConvolveDownNoBorderUnrolled_U8_I8_Div;
import gecv.alg.misc.ImageTestingOps;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.convolve.Kernel2D_F32;
import gecv.struct.convolve.Kernel2D_I32;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageSInt16;
import gecv.struct.image.ImageSInt32;
import gecv.struct.image.ImageUInt8;

import java.util.Random;

/**
 * Benchmark for different convolution operations.
 * @author Peter Abeles
 */
public class BenchmarkConvolveDown {
	static int imgWidth = 640;
	static int imgHeight = 480;
	static int radius;
	static int skip = 2;
	static long TEST_TIME = 1000;

	static Kernel2D_F32 kernel2D_F32;
	static Kernel1D_F32 kernelF32;
	static ImageFloat32 imgFloat32;
	static ImageFloat32 out_F32_D;
	static ImageFloat32 out_F32;
	static Kernel1D_I32 kernelI32;
	static Kernel2D_I32 kernel2D_I32;
	static ImageUInt8 imgInt8;
	static ImageSInt16 imgInt16;
	static ImageUInt8 out_I8;
	static ImageSInt16 out_I16;
	static ImageSInt32 out_I32;

	public static class HorizontalStandard_F32 extends PerformerBase
	{
		@Override
		public void process() {
			ConvolveDownNoBorderStandard.horizontal(kernelF32,imgFloat32,out_F32,skip);
		}
	}

	public static class HorizontalUnrolled_F32 extends PerformerBase
	{
		@Override
		public void process() {
			if( !ConvolveDownNoBorderUnrolled_F32_F32.horizontal(kernelF32,imgFloat32,out_F32,skip) )
				throw new RuntimeException();
		}
	}

	public static class VerticalStandard_F32 extends PerformerBase
	{
		@Override
		public void process() {
			ConvolveDownNoBorderStandard.vertical(kernelF32,imgFloat32,out_F32,skip);
		}
	}

	public static class VerticalUnrolled_F32 extends PerformerBase
	{
		@Override
		public void process() {
			if( !ConvolveDownNoBorderUnrolled_F32_F32.vertical(kernelF32,imgFloat32,out_F32,skip) )
				throw new RuntimeException();
		}
	}

	public static class Convolve2DStandard_F32 extends PerformerBase
	{
		@Override
		public void process() {
			ConvolveDownNoBorderStandard.convolve(kernel2D_F32,imgFloat32,out_F32,skip);
		}
	}

	public static class Convolve2DUnrolled_F32 extends PerformerBase
	{
		@Override
		public void process() {
			if( !ConvolveDownNoBorderUnrolled_F32_F32.convolve(kernel2D_F32,imgFloat32,out_F32,skip) )
				throw new RuntimeException();
		}
	}

	public static class VerticalStandard_U8_I16 extends PerformerBase
	{
		@Override
		public void process() {
			ConvolveDownNoBorderStandard.vertical(kernelI32,imgInt8,out_I16,skip);
		}
	}

	public static class VerticalUnrolled_U8_I16 extends PerformerBase
	{
		@Override
		public void process() {
			if( !ConvolveDownNoBorderUnrolled_U8_I16.vertical(kernelI32,imgInt8,out_I16,skip) )
				throw new RuntimeException();
		}
	}

	public static class VerticalStandard_U8_I8_Div extends PerformerBase
	{
		@Override
		public void process() {
			ConvolveDownNoBorderStandard.vertical(kernelI32,imgInt8,out_I8,skip,10);
		}
	}

	public static class VerticalUnrolled_U8_I8_Div extends PerformerBase
	{
		@Override
		public void process() {
			if( !ConvolveDownNoBorderUnrolled_U8_I8_Div.vertical(kernelI32,imgInt8,out_I8,skip,10) )
				throw new RuntimeException();
		}
	}

	public static void main( String args[] ) {

		int outWidth = imgWidth/skip;
		int outHeight = imgHeight/skip;

		imgInt8 = new ImageUInt8(imgWidth,imgHeight);
		imgInt16 = new ImageSInt16(imgWidth,imgHeight);
		out_I32 = new ImageSInt32(imgWidth,imgHeight);
		out_I16 = new ImageSInt16(imgWidth,imgHeight);
		out_I8 = new ImageUInt8(imgWidth,imgHeight);
		imgFloat32 = new ImageFloat32(imgWidth,imgHeight);
		out_F32_D = new ImageFloat32(outWidth,outHeight);
		out_F32 = new ImageFloat32(imgWidth,imgHeight);

		Random rand = new Random(234234);
		ImageTestingOps.randomize(imgInt8,rand, 0, 100);
		ImageTestingOps.randomize(imgInt16,rand,0,200);
		ImageTestingOps.randomize(imgFloat32,rand,0,200);


		System.out.println("=========  Profile Image Size "+imgWidth+" x "+imgHeight+" ==========");
		System.out.println();

		for( int radius = 1; radius < 10; radius += 1 ) {
			System.out.println("Radius: "+radius);
			System.out.println();
			BenchmarkConvolveDown.radius = radius;
			kernelF32 = KernelFactory.gaussian1D_F32(radius,true);
			kernelI32 = KernelFactory.gaussian1D_I32(radius);
			kernel2D_F32 = KernelFactory.gaussian2D_F32(1.0,radius,true);
			kernel2D_I32 = KernelFactory.gaussian2D_I32(1.0,radius);
			
			ProfileOperation.printOpsPerSec(new HorizontalStandard_F32(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new HorizontalUnrolled_F32(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new VerticalStandard_F32(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new VerticalUnrolled_F32(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new Convolve2DStandard_F32(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new Convolve2DUnrolled_F32(),TEST_TIME);
//
//			ProfileOperation.printOpsPerSec(new VerticalStandard_U8_I16(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new VerticalUnrolled_U8_I16(),TEST_TIME);
//
//			ProfileOperation.printOpsPerSec(new VerticalStandard_U8_I8_Div(),TEST_TIME);
//			ProfileOperation.printOpsPerSec(new VerticalUnrolled_U8_I8_Div(),TEST_TIME);

		}


	}
}