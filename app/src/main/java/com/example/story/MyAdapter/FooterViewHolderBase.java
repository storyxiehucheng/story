package com.example.story.MyAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.story.xiaoyao.R;

/**
 * Created by story on 2017/4/27.
 */

public class FooterViewHolderBase extends RecyclerView.ViewHolder {
    private TextView tv_pull_up;
    private final RelativeLayout pull_up_linearLayout;
    private final ProgressBar progress_pull_up;

    public FooterViewHolderBase(View itemView) {
        super(itemView);
        tv_pull_up= (TextView) itemView.findViewById(R.id.tv_pull_up);
        pull_up_linearLayout=(RelativeLayout)itemView.findViewById(R.id.pull_up_linearLayout);
        progress_pull_up=(ProgressBar)itemView.findViewById(R.id.progress_pull_up);
    }

    public void setFooterText(String text)
    {
        tv_pull_up.setText(text);
    }

    public void setProgressVisible(boolean isVisible)
    {
        if(isVisible)
        {
            progress_pull_up.setVisibility(View.VISIBLE);
        }
        else
            progress_pull_up.setVisibility(View.GONE);
    }
    public void setFootViewVisible(boolean isVisible)
    {
        if(isVisible)
        {
            pull_up_linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            pull_up_linearLayout.setVisibility(View.GONE);
        }
    }
}
