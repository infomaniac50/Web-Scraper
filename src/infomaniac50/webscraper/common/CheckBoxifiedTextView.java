/* $Id: BulletedTextView.java 57 2007-11-21 18:31:52Z steven $
 *
 * Copyright 2007 Steven Osborn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Code modifications by Daniel Ricciotti
 * This code was built using the IconifiedText tutorial by Steven Osborn
 * http://www.anddev.org/iconified_textlist_-_the_making_of-t97.html
 * 
 * Copyright 2008 Daniel Ricciotti
 */
package infomaniac50.webscraper.common;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckBoxifiedTextView extends LinearLayout {
     
     private TextView mText;
     private CheckBox mCheckBox;
     private CheckBoxifiedText mCheckBoxText;
     
     public CheckBoxifiedTextView(Context context, CheckBoxifiedText aCheckBoxifiedText) {
          super(context);

          /* First CheckBox and the Text to the right (horizontal),
           * not above and below (vertical) */
          this.setOrientation(HORIZONTAL);
          mCheckBoxText = aCheckBoxifiedText;
          mCheckBox = new CheckBox(context);
          mCheckBox.setPadding(0, 0, 20, 0); // 5px to the right
          
          /* Set the initial state of the checkbox. */
          mCheckBox.setChecked(aCheckBoxifiedText.getChecked());
          
          
          /* At first, add the CheckBox to ourself
           * (! we are extending LinearLayout) */
          addView(mCheckBox,  new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
          
          mText = new TextView(context);
          mText.setText(aCheckBoxifiedText.getText());
          //mText.setPadding(0, 0, 15, 0);
          addView(mText, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
     }

     public void setText(String words) {
          mText.setText(words);
     }
     public void setCheckBoxState(boolean bool)
     {
    	 mCheckBox.setChecked(mCheckBoxText.getChecked());
    	 mCheckBoxText.setChecked(true);
     }
}