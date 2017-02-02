# ImageRecognition

This was an attempt at making an image-recognition machine from scratch. No real machine learning/vision algorithms were used.

When an image is input, it is first cropped/split into sub-images each containing one symbol.
This cropped image is then compared to a collection of past examples. The highest accuracy is presented, and awaits user input. If the user indicates that the guess was wrong, the user is prompted for the correct answer and this image is stored for later comparisons.

To compare an image with an example, the image/example are first both scaled *down* to the same size by averaging out pixel clusters. Scaling up was not implemented at the time of this project, although it's probably not too hard.
An iteration begins through the pixels of the original image, where a kind of outward circular pulse is sent out from each pixel in the image, and the distance until collision with a matching pixel in the example image is recorded. This distance is used to determine the score of the individual pixel and then the combined (and slightly averaged) score of each pixel in the test image is used as the certainty of the test image corresponding to the example image. This process is repeated for all examples under a given symbol. This entire process is then repeated for all other symbols and in the end the best guess is presented.

At one point, there was an attempt to make a kind of 'pixel density' approach to guessing. To do this, each symbol got its own image file and when new images were being read in, their overlap with existing pixels in the density map determined the total score. This data was stored in the pixel's RGB value (using base-255, so theoretically there could have been a LOT of images). This approach, however, did not work better than the "pulse one" (described above) possibly due to image resizing inaccuracies.

###### Setup

   **1.** If you want to test math, change `line 28` from `learn(symbols, reader)` to `doMath(symbols, reader)`. Otherwise, keep it on math.

   **2.** Find the paint file located under `../NetBeansProjects/ImageRecognition/images/tests/test.png`. Right click and `Edit with paint`. Draw a symbol that corresponds with your above selection
    - If an image contains more than one symbol, ensure that each symbol is separated by at least one entire vertical column of whitespace

   **3.** Run the main file.

###### Math mode

   **1.** If the equation is recognized, the answer will be dislpayed. If it is the correct answer, type "yes". Otherwise, type "no", and follow the steps in **learning mode**.

###### Learning mode

   - A symbol is represented by *symbol_identity(symbol_display_string)*. The display string is purely for display purposes and should not be input into any prompt -- the symbol_identity is what is needed.

   **1.** If a symbol is recognized with 100% accuracy, a prompt to confirm its identity will not show up. Otherwise, the symbol's real *symbol_identity* must be inserted. Here is a list of the important symbol identities in the *symbol_identity(symbol_display_string)* format:
     - *div(/)*
     - _x(*)_
     - *gt(>)*
     - *lt(<)*
    
   - A symbol with 100% accuracy will not be remembered again

