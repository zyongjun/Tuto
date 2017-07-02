package cn.ibos.ui.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import com.windhike.annotation.configsapp.Configs
import com.windhike.annotation.model.PreferenceConnector
import com.windhike.tuto.R
import com.windhike.tuto.TutoApplication
import com.windhike.tuto.reuse.kotlin.ctx
import kotlinx.android.synthetic.main.dialog_drawing_shape.*

/**
 * author: zyongjun on 2017/2/19 0019.
 * email: zhyongjun@windhike.cn
 */
class DrawingMenuDialog(ctx: Context, var clickBody: (View) -> Unit) : Dialog(ctx) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(ctx)
        val layout = inflater.inflate(R.layout.dialog_drawing_shape, null)
        window.setBackgroundDrawable(ColorDrawable())
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)

        //The below code is EXTRA - to dim the parent view by 70%
        val lp: WindowManager.LayoutParams = window.attributes
        lp.dimAmount = 0.7f
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = lp
        addContentView(layout, ViewGroup.LayoutParams(-1, -1))

        val drawMode = PreferenceConnector.readInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 0)
        when (drawMode) {
            0 -> ivPen.setImageResource(R.mipmap.ic_pen_blue)
            1 -> ivArrow.setImageResource(R.mipmap.ic_arrow_blue)
            2 -> ivRectange.setImageResource(R.mipmap.ic_rectange_blue)
            3 -> ivCircle.setImageResource(R.mipmap.ic_circle_blue)
            4 -> ivMask.setImageResource(R.mipmap.ic_mask_blue)
            6 -> ivEraser.setImageResource(R.mipmap.ic_eraser_blue)
        }
        ivPen.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 0)
            clickBody(it)
            dismiss()
        }
        ivRectange.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 2)
            clickBody(it)
            dismiss()
        }
        ivCircle.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 3)
            clickBody(it)
            dismiss()
        }
        ivArrow.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 1)
            clickBody(it)
            dismiss()
        }
        ivEraser.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 6)
            clickBody(it)
            dismiss()
        }
        ivMask.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_DRAWING, 4)
            clickBody(it)
            dismiss()
        }
        val colorMode = PreferenceConnector.readInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 1)
        when (colorMode) {
            1 -> ivColorOne.setBackgroundResource(R.drawable.ic_anno_color_one_selected)
            2 -> ivColorSecond.setBackgroundResource(R.drawable.ic_anno_color_two_selected)
            3 -> ivColorThird.setBackgroundResource(R.drawable.ic_anno_color_three_selected)
            4 -> ivColorFour.setBackgroundResource(R.drawable.ic_anno_color_four_selected)
            5 -> ivColorFive.setBackgroundResource(R.drawable.ic_anno_color_five_selected)
            6 -> ivColorSix.setBackgroundResource(R.drawable.ic_anno_color_six_selected)
        }
        ivColorOne.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 1)
            clickBody(it)
            dismiss()
        }
        ivColorSecond.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 2)
            clickBody(it)
            dismiss()
        }
        ivColorThird.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 3)
            clickBody(it)
            dismiss()
        }
        ivColorFour.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 4)
            clickBody(it)
            dismiss()
        }
        ivColorFive.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 5)
            clickBody(it)
            dismiss()
        }
        ivColorSix.setOnClickListener {
            PreferenceConnector.writeInteger(TutoApplication.getInstance(), Configs.FLAG_MODE_COLOR, 6)
            clickBody(it)
            dismiss()
        }
    }

}