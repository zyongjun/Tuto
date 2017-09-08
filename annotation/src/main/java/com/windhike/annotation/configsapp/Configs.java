package com.windhike.annotation.configsapp;

import android.graphics.Color;

public class Configs {
        public static final String ANNOTATION_IBOS = "ibosannotation";
        public static final String ANNOTATION_IBOS_TEMP = "ibosannotation_tmp";
        public static final String KEY_ANNOTATION_DRAW_INDEX = "KEY_ANNOTATION_DRAW_INDEX";
        public static final String KEY_ANNOTATION_DRAW_NEW_PATH = "KEY_ANNOTATION_DRAW_NEW_PATH";
        public static final String KEY_ANNOTATION_SHARE_CONVERSATION = "KEY_ANNOTATION_SHARE_CONVERSATION";
        public static final String ENCRYPT_KEY = "ENCRYPT_KEY";
        public static final String FLAG_BLUR_OPACTIY = "FLAG_BLUR_OPACTIY";
        public static final String FLAG_EDIT_FILE_NAME = "FLAG_EDIT_FILE_NAME";
        public static final String FLAG_INDEX_COLOR = "FLAG_INDEX_COLOR";
        public static final String FLAG_INDEX_CURRENT_CHOOSE_DRAW = "FLAG_INDEX_CURRENT_CHOOSE_DRAW";
        public static final String FLAG_SYSNC_ANNOTATION_TIME = "FLAG_SYSNC_ANNOTATION_TIME";
        public static final String FLAG_SYSNC_UPLOAD_ANNOTATION_TIME = "FLAG_SYSNC_UPLOAD_ANNOTATION_TIME";
        public static final String FLAG_INDEX_STROKEWIDTH = "FLAG_INDEX_STROKEWIDTH";
        public static final String FLAG_MODE_DRAWING = "FLAG_MODE_DRAWING";
        public static final String FLAG_MODE_COLOR = "FLAG_MODE_COLOR";
        public static final String FLAG_ORIGINAL_FILE_NAME = "FLAG_ORIGINAL_FILE_NAME";
        public static final String FLAG_THUMBNAIL_FILE_NAME = "FLAG_THUMBNAIL_FILE_NAME";
        public static final int[] LIST_COLOR = new int[]{Color.parseColor("#f65252"), Color.parseColor("#fb6731"), Color.parseColor("#ffdf03"), Color.parseColor("#a2ef1b"), Color.parseColor("#1792f9"), Color.parseColor("#d237ee")};
        public static final float[] LIST_STROKE_WIDTH = new float[]{3.0f, 7.0f, 14.0f};
        public static final float[] LIST_TEXT_SIZE = new float[]{34.0f, 46.0f, 65.0f};
        public static final String ROOT_FOLDER_NAME = "ibos_iAnnotation";
        public static final float SHOW_MENU_EDGE_WIDTH = 10.0f;
        public static final float SLIDING_MENU_WIDTH = 0.5f;
        public static final String TYPE_FORMAT_PROJECT_NAME = ".dat";

//        public static final String KEY_IS_PICTURE_ANNOTATION_IN_PROGRESS = "KEY_IS_PICTURE_ANNOTATION_IN_PROGRESS";

        public enum DrawingState {
                Idle,
                ListenForNewTouchPoint,
                DrawingOrMove,
                Pinch,
                Pan,
                DisableAction
        }

        public enum ShapeAction {
                AddNewShape,
                MoveOrResizeShape,
                ReszieShape,
                MoveShapeToTop,
                DeleteShape,
                ChangeBlurOpacity,
                ChangeTextColorOrStroke
        }
}

