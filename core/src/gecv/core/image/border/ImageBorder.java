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

package gecv.core.image.border;

import gecv.struct.image.ImageBase;

/**
 * A wrapper around a normal image that returns a numeric value if a pixel is requested that is outside of the image
 * boundary.  The additional sanity checks can significantly slow down algorithms and should only be used when needed.
 *
 * @author Peter Abeles
 */
public abstract class ImageBorder<T extends ImageBase> {

	T image;

	protected ImageBorder(T image) {
		this.image = image;
	}

	public T getImage() {
		return image;
	}
}