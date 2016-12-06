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

    private HashMap<ItemExplorer, Integer> mListViolated;
    private ValidateAction mValidateAction;

    public Validator() {
        mListViolated = new HashMap<>();
    }

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

    public int setModeViolated(int flag, int mode, boolean violated) {
        if (violated)
            return flag | mode;
        return flag & (~mode);
    }

    public void clear() {
        mListViolated.clear();
    }

    public interface ValidateAction {
        int validate(ItemExplorer item);
    }
}
