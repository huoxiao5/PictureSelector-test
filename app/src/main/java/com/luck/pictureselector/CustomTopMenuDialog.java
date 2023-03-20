package com.luck.pictureselector;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

/**
 * 自定义可操作性dialog
 * Created by JiajiXu on 17-12-11.
 */
public class CustomTopMenuDialog extends BubbleDialog implements View.OnClickListener
{
    private ViewHolder mViewHolder;
    private OnClickCustomButtonListener mListener;

    public CustomTopMenuDialog(Context context)
    {
        super(context);
        setPosition(Position.BOTTOM);
        setTransParentBackground();
        BubbleLayout bubbleLayout = new BubbleLayout(context);
        //bubbleLayout.setBubbleColor(Color.YELLOW);
         bubbleLayout.setBubbleImageBgRes(R.drawable.top_menu_bg);
        setBubbleLayout(bubbleLayout);
        View rootView = LayoutInflater.from(context).inflate(R.layout.top_menu, null);
        mViewHolder = new ViewHolder(rootView);
        setBubbleContentView(rootView);
        mViewHolder.btn1.setOnClickListener(this);
        mViewHolder.btn2.setOnClickListener(this);
        mViewHolder.btn3.setOnClickListener(this);
        mViewHolder.btn4.setOnClickListener(this);
        Window window = getWindow();
       // window.setWindowAnimations(R.style.dialogWindowAnim);

    }

    @Override
    public void onClick(View v)
    {
        if (mListener != null)
        {
            int tag = Integer.parseInt(v.getTag().toString());
            mListener.onClick(v,tag);
        }
    }

    private static class ViewHolder
    {
        TextView btn1, btn2, btn3, btn4;
        public ViewHolder(View rootView)
        {
            btn1  = rootView.findViewById(R.id.top_menu_clear);
            btn1.setTag("0");
            btn2 = rootView.findViewById(R.id.top_menu_bak);
            btn2.setTag("1");
            btn3 = rootView.findViewById(R.id.top_menu_recover);
            btn3.setTag("2");
            btn4 = rootView.findViewById(R.id.top_menu_delete_bak);
            btn4.setTag("3");
        }
    }

    public void setClickListener(OnClickCustomButtonListener l)
    {
        this.mListener = l;
    }

    public interface OnClickCustomButtonListener
    {
        void onClick(View view,int index);
    }
}
