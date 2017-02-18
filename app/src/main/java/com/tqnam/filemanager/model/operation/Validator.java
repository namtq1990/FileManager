/*
 * MIT License
 *
 * Copyright (c) 2017 Tran Quang Nam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tqnam.filemanager.model.operation;

import com.tqnam.filemanager.model.ItemExplorer;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by quangnam on 11/25/16.
 * Project FileManager-master
 */
public class Validator {

    public static final int MODE_FILE_EXIST = 0x1;
    public static final int MODE_PERMISSION = 0x2;
    public static final int MODE_SAME_FILE = 0x4;

    private HashMap<ItemExplorer, Integer> mListViolated;
    private ValidateAction mValidateAction;

    public Validator() {
        mListViolated = new HashMap<>();
    }

    /**
     * Custom validate action, action can validate itself mode to match with your purpose
     */
    public void setValidateAction(ValidateAction validateAction) {
        mValidateAction = validateAction;
    }

    public Set<ItemExplorer> getListViolated() {
        return mListViolated.keySet();
    }

    public void setItemViolated(ItemExplorer item, int mode, boolean isViolated) {
        Integer flag = mListViolated.get(item);
        if (flag == null) flag = 0;
        flag = setModeViolated(flag, mode, isViolated);

        mListViolated.put(item, mode);
    }

    public void setItemSafe(ItemExplorer item) {
        mListViolated.remove(item);
    }

    public void validate(ItemExplorer item) {
        int mode = 0;

        if (mValidateAction != null) {
            mode = mValidateAction.validate(item);
        } else {

            if (item.exists()) {
                mode = setModeViolated(MODE_FILE_EXIST, mode, true);
            }
            if ((item.exists() && !item.canWrite())
                    || (!item.exists() && !item.getParentItem().canWrite())) {
                mode = setModeViolated(MODE_PERMISSION, mode, true);
            }
        }

        if (mode != 0)
            mListViolated.put(item, mode);
    }

    public boolean isItemViolated(ItemExplorer item) {
        return mListViolated.get(item) != null;
    }

    public boolean isModeViolated(ItemExplorer item, int modeToCheck) {
        Integer flag = mListViolated.get(item);

        if (flag == null)
            return false;

        return isModeViolate(flag, modeToCheck);
    }

    public boolean isModeViolate(int flag, int modeToCheck) {
        return (flag & modeToCheck) != 0;
    }

    public void setModeViolated(ItemExplorer item, int mode, boolean violated) {
        Integer flag = mListViolated.get(item);

        if (flag != null) {
            setModeViolated(flag, mode, violated);
        }
    }

    public int setModeViolated(int flag, int mode, boolean violated) {
        if (violated)
            return flag | mode;
        return flag & (~mode);
    }

    public void clear() {
        mListViolated.clear();
    }

    /**
     * Interface to implement action to validate yourself.
     */
    public interface ValidateAction {
        int validate(ItemExplorer item);
    }
}
