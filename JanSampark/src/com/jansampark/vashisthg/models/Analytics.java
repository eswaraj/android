package com.jansampark.vashisthg.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Analytics  implements Parcelable {
	
	private int issueCategory;
	private int templateId;
	private int count;
	
	public Analytics() {
		
	}
	
	public Analytics(Parcel in) {
		readFromParcel(in);
	}
	public int getIssueCategory() {
		return issueCategory;
	}
	public void setIssueCategory(int issueCategory) {
		this.issueCategory = issueCategory;
	}
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(issueCategory);
		dest.writeInt(templateId);
		dest.writeInt(count);
	}
	
	private void readFromParcel(Parcel in) {  
		// We just need to read back each // field in the order that it was // written to the parcel 
		issueCategory = in.readInt(); 
		templateId = in.readInt(); 
		count = in.readInt();
	}

	public static final Parcelable.Creator<Analytics> CREATOR = new Parcelable.Creator<Analytics>() {
		public Analytics createFromParcel(Parcel in) {
			return new Analytics(in);
		}

		public Analytics[] newArray(int size) {
			return new Analytics[size];
		}
	};

}
