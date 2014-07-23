package com.next.eswaraj;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;

public class IssueButtonAdapter extends BaseAdapter {

	private Context mContext;
	private List<CategoryWithChildCategoryDto> categoryDtos;
	private ImageLoader imageLoader;
	NetworkImageView networkImage;

    public IssueButtonAdapter(Context c, List<CategoryWithChildCategoryDto> categoryDtos, ImageLoader imageLoader) {
        mContext = c;
        this.categoryDtos = categoryDtos;
        this.imageLoader = imageLoader;
    }

    public int getCount() {
        return categoryDtos.size();
    }

    public Object getItem(int position) {
        return categoryDtos.get(position);
    }
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	CategoryWithChildCategoryDto categoryDto = categoryDtos.get(position);
    	LayoutInflater inflater = LayoutInflater.from(mContext);
		LinearLayout mainButtonView = (LinearLayout)inflater.inflate(R.layout.main_button_layout, parent, false);
		NetworkImageView networkImage = (NetworkImageView)mainButtonView.findViewById(R.id.networkImage);
		if(categoryDto.getImageUrl() == null || categoryDto.getImageUrl().trim().equals("")){
		}else{
			networkImage.setImageUrl(categoryDto.getImageUrl(), imageLoader);
		}
		
		TextView textView = (TextView)mainButtonView.findViewById(R.id.issue_button_bottom_text);
		textView.setText(categoryDto.getName());
		
		return mainButtonView;
    }


}
