package com.tqnam.filemanager.explorer;

/**
 * Created by tqnam on 10/28/2015.
 */
public interface ExplorerView {

    public static int ANIM_TYPE_NONE = 0;
    public static int ANIM_TYPE_OPEN_NEXT = 1;
    public static int ANIM_TYPE_BACK = 2;

    /**
     * Update list item to UI
     *
     * param animType animation type one of @ANIM_TYPE_BACK, @ANIM_TYPE_NONE, @ANIM_TYPE_OPEN_NEXT
     */
    void updateList();

    /**
     * Handle when don't have permission to execute UI
     */
    void onErrorPermission();
}
