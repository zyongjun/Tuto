package com.windhike.tuto.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.windhike.tuto.R;
import com.windhike.tuto.TutoApplication;
import com.windhike.tuto.reuse.ToolbarBuilder;
import com.windhike.tuto.view.IBaseRecordsView;
import java.util.ArrayList;
import java.util.List;
/**
 * author: zyongjun on 2017/3/12 0012.
 * email: zhyongjun@windhike.cn
 */

public class AnnotationSelectRecordsPresenter extends BaseAnnotationRecordsPresenter<AnnotationSelectRecordsPresenter.IAnnotationSelectRecordsView>{
    private List<Boolean> selectList = new ArrayList<>();

    public interface IAnnotationSelectRecordsView extends IBaseRecordsView {
    }

    public AnnotationSelectRecordsPresenter() {
        registViewTemplate(0,AnnotationMultiSelectRecordHolder.class);
    }

    @Override
    public void initHead() {
        mView.getToolbarBuilder().showLeft(false)
                .withBack(true)
                .withTitle("批注记录")
                .showRight(true)
                .withRight("发送")
                .withRightClick(new ToolbarBuilder.OnClickRight() {
                    @Override
                    public void onClickRight() {
                        if (managerImageObjects==null||managerImageObjects.getListChooseDrawObject().size() == 0) {
                            return;
                        }
                        int size = managerImageObjects.getListChooseDrawObject().size();
                        ArrayList<Integer> selectProjects = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            if (selectList.get(i)) {
                                selectProjects.add(i);
                            }
                        }
                        Activity activity = (Activity) mView.getViewContext();
                        if (selectProjects.size() == 0) {
                            activity.setResult(Activity.RESULT_CANCELED);
                            Toast.makeText(TutoApplication.getInstance(),"请选择至少一个批注", Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent();
                            intent.putIntegerArrayListExtra(Intent.EXTRA_RETURN_RESULT,selectProjects);
                            activity.setResult(Activity.RESULT_OK,intent);
                            activity.finish();
                        }
                    }
                })
                .show();
    }

    public List<Boolean> getSelectList() {
        return selectList;
    }

    @Override
    public View.OnClickListener itemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.id.position_id);
                selectList.set(position,!selectList.get(position));
                mView.notifyDataChanged();
            }
        };
    }

    @Override
    protected void initOtherBaseOnProject() {
        selectList.clear();
        int size = managerImageObjects.getListChooseDrawObject().size();
        for (int i = 0; i <size ; i++) {
            selectList.add(false);
        }
    }
}
