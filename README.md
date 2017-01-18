# ImageRecognition

###### Setup

   **1.** If you want to test math, change `line 28` from `learn(symbols, reader)` to `doMath(symbols, reader)`. Otherwise, keep it on math.

   **2.** Find the paint file located under `../NetBeansProjects/ImageRecognition/images/tests/test.png`. Right click and `Edit with paint`. Draw a symbol that corresponds with your above selection

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

