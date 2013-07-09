package com.jansampark.vashisthg;

import android.os.Parcel;
import android.os.Parcelable;

public class IssueItem implements Parcelable {
	private int issueId;	
	private int templateId;
	private String issueName;
	
	public IssueItem() {
		
	}
	
	public IssueItem(Parcel in) {
		readFromParcel(in);
	}

	public String getIssueName() {
		return issueName;
	}

	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int issueId) {
		this.templateId = issueId;
	}

	public int getIssueId() {
		return issueId;
	}

	public void setIssueId(int issueId) {
		this.issueId = issueId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(issueId);
		dest.writeInt(templateId);
		dest.writeString(issueName);
	}
	
	private void readFromParcel(Parcel in) {  
		// We just need to read back each // field in the order that it was // written to the parcel 
		issueId = in.readInt(); 
		templateId = in.readInt(); 
		issueName = in.readString();
	}

	public static final Parcelable.Creator<IssueItem> CREATOR = new Parcelable.Creator<IssueItem>() {
		public IssueItem createFromParcel(Parcel in) {
			return new IssueItem(in);
		}

		public IssueItem[] newArray(int size) {
			return new IssueItem[size];
		}
	};
}