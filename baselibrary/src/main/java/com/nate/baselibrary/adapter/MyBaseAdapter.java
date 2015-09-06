package com.nate.baselibrary.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * @ClassName: BaseAdapter
 * @Description: 封装的listviewadapter
 * @author Nate
 * @date 2015年4月26日 上午10:06:33
 * @param <T>
 */
public abstract class MyBaseAdapter<T> extends ArrayAdapter<T>
{
    /**
     * listview的item资源id
     */
    private int resourceId;
    
    private List<T> list = new ArrayList<T>();
    
    public MyBaseAdapter(Context context, int resource, List<T> list)
    {
        super(context, resource, list);
        this.list = list;
        this.resourceId = resource;
    }
    
    public MyBaseAdapter(Context context, int resource, List<T> list, List<T> listother, int flag)
    {
        super(context, resource, list);
        this.list = list;
        this.resourceId = resource;
    }
    
    @Override
    public T getItem(int position)
    {
        return list.get(position);
    }
    
    @Override
    public int getCount()
    {
        return list == null ? 0 : list.size();
    }
    
    @Override
    public long getItemId(int id)
    {
        return id;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        BaseViewHolder viewHolder = BaseViewHolder.get(getContext(), parent, resourceId, position, convertView);
        // 设置每个item控件
        setConvert(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }
    
    /**
     * 
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     * @param @param viewHolder
     * @param @param t 设定文件
     * @return void 返回类型
     */
    public abstract void setConvert(BaseViewHolder viewHolder, T t);
    
    public void setList(List<T> list)
    {
        this.list = list;
    }
    
    public List<T> getList()
    {
        return list;
    }
    
}
