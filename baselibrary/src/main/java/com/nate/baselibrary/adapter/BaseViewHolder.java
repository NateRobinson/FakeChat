package com.nate.baselibrary.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: BaseViewHolder
 * @Description: ViewHolder封装类
 * @author Nate
 * @date 2015年4月26日 上午10:03:34
 * 
 */
public class BaseViewHolder
{
    /**
     * 视图容器
     */
    private SparseArray<View> mViews;
    
    /**
     * 位置标识
     */
    private int mPosition;
    
    /**
     * 视图
     */
    private View mConvertView;
    
    /**
     * 私有化构造方法，不让外界进行使用
     * 
     * @param context
     * @param parent
     * @param layoutId
     * @param position
     */
    private BaseViewHolder(Context context, ViewGroup parent, int layoutId, int position)
    {
        this.mViews = new SparseArray<View>();
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mPosition = position;
        mConvertView.setTag(this);
    }
    
    /**
     * 
     * @param context
     * @param parent
     * @param layoutId
     * @param position
     * @param convertView
     * @return 静态方法获取到viewholder类实例
     */
    public static BaseViewHolder get(Context context, ViewGroup parent, int layoutId, int position, View convertView)
    {
        if (convertView == null)
        {
            return new BaseViewHolder(context, parent, layoutId, position);
        }
        else
        {
            BaseViewHolder holder = (BaseViewHolder)convertView.getTag();
            // 复用视图，但是position要更新
            holder.mPosition = position;
            return holder;
        }
    }
    
    /**
     * 
     * @param viewId 控件id
     * @return 根据控件id获取到控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T)view;
    }
    
    /**
     * @return 返回视图类
     */
    public View getConvertView()
    {
        return mConvertView;
    }
    
    /**
     * @Title: getPosition
     * @param @return 返回当前的position
     * @return int 返回类型
     */
    public int getPosition()
    {
        return mPosition;
    }
    
    /**
     * @param viewId
     * @param content
     * @return 设置textview相关
     */
    public BaseViewHolder setTextView(int viewId, String content)
    {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }
    
    /**
     * @Title: setImg
     * @Description: 设置网络图片
     * @param @param viewId
     * @param @param path
     * @param @return 设定文件
     * @return BaseViewHolder 返回类型
     * @throws
     */
    public BaseViewHolder setImgPath(int viewId, String path)
    {
        ImageView iv = getView(viewId);
        //DrawableUtil.DisplayRoundImg(iv, path);
        return this;
    }
    
    /**
     * @Title: setNormalImgPath
     * @Description: 展示正常网络图片
     * @param @param viewId
     * @param @param path
     * @param @return 设定文件
     * @return BaseViewHolder 返回类型
     * @throws
     */
    public BaseViewHolder setNormalImgPath(int viewId, String path)
    {
        ImageView iv = getView(viewId);
        //DrawableUtil.DisplayImg(iv, path);
        return this;
    }
    
    /**
     * @Title: setImg
     * @Description: 设置drawable图片
     * @param @param ViewId
     * @param @param drawable
     * @param @return 设定文件
     * @return BaseViewHolder 返回类型
     * @throws
     */
    @SuppressWarnings("deprecation")
    public BaseViewHolder setTvDra(int viewId, Drawable drawable)
    {
        TextView tv = getView(viewId);
        tv.setBackgroundDrawable(drawable);
        return this;
    }
    
    /**
     * @Title: setImgRes
     * @Description: 根据资源id设置图片
     * @param @param viewId
     * @param @param resId
     * @param @return 设定文件
     * @return BaseViewHolder 返回类型
     * @throws
     */
    public BaseViewHolder setImgRes(int viewId, int resId)
    {
        ImageView iv = getView(viewId);
        iv.setBackgroundResource(resId);
        return this;
    }
    /**
     * @Title: setImgRes
     * @Description: 根据资源id设置复选框
     * @param @param viewId
     * @param @param resId
     * @param @return 设定文件
     * @return BaseViewHolder 返回类型
     * @throws
     */
    public BaseViewHolder setCheckBox(int viewId, boolean isCheck)
    {
    	CheckBox iv = getView(viewId);
        iv.setChecked(isCheck);
        return this;
    }
    // TODO 可以根据自己的需要编写更多适用的方法。。。
}
